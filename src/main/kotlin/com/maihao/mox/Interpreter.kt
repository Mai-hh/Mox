package com.maihao.mox

import com.maihao.mox.TokenType.*


class Interpreter : Expr.Visitor<Any?>, Stmt.Visitor<Unit> {

    // 唯一全局变量作用域
    private val globals = Environment()

    // 当前进入的作用域
    private var environment: Environment = globals


    private val locals: MutableMap<Expr, Int> = HashMap()

    init {
        globals.define("clock", object : MoxCallable {
            override fun call(interpreter: Interpreter, arguments: List<Any?>): Any {
                return System.currentTimeMillis() / 1000.0
            }

            override fun arity(): Int {
                return 0
            }

            override fun toString(): String {
                return "<native fn>"
            }
        })
    }

    internal fun interpret(statements: List<Stmt>) {
        try {
            for (statement: Stmt in statements) {
                execute(statement)
            }
        } catch (error: RuntimeError) {
            Mox.runtimeError(error)
        }
    }

    /* Stmt.Visitor */
    override fun visitWhileStmt(stmt: Stmt.While) {
        while (isTruthy(evaluate(stmt.condition))) {
            execute(stmt.body)
        }
    }

    override fun visitBlockStmt(stmt: Stmt.Block) {
        executeBlock(
            statements = stmt.statements,
            environment = Environment(
                enclosing = environment
            )
        )
    }

    override fun visitClassStmt(stmt: Stmt.Class) {
        var superclass: Any? = null
        if (stmt.superclass != null) {
            superclass = evaluate(stmt.superclass)
            if (superclass !is MoxClass) {
                throw RuntimeError(stmt.superclass.name, "Superclass must be a class.")
            }
        }

        environment.define(
            name = stmt.name.lexeme,
            value = null
        )

        if (stmt.superclass != null) {
            environment = Environment(enclosing = environment)
            environment.define(name = "super", value = superclass)
        }

        val methods = mutableMapOf<String, MoxFunction>()
        for (method in stmt.methods) {
            val function = MoxFunction(
                declaration = method,
                closure = environment,
                isInitializer = method.name.lexeme == "init"
            )
            methods[method.name.lexeme] = function
        }

        val klass = MoxClass(
            name = stmt.name.lexeme,
            superclass = superclass as MoxClass?,
            methods = methods
        )

        if (superclass != null) {
            environment = environment.enclosing!!
        }

        environment.assign(name = stmt.name, value = klass)
    }

    override fun visitIfStmt(stmt: Stmt.If) {
        if (isTruthy(evaluate(stmt.condition))) {
            execute(stmt.thenBranch)
        } else {
            stmt.elseBranch?.let { execute(it) }
        }
    }

    override fun visitExpressionStmt(stmt: Stmt.Expression) {
        evaluate(stmt.expression)
    }

    override fun visitFunctionStmt(stmt: Stmt.Function) {
        val function = MoxFunction(stmt, closure = environment, isInitializer = false)
        environment.define(stmt.name.lexeme, function)
    }

    override fun visitPrintStmt(stmt: Stmt.Print) {
        val value = evaluate(stmt.expression)
        println(stringify(value))
    }

    override fun visitReturnStmt(stmt: Stmt.Return) {
        val value = if (stmt.value != null) evaluate(stmt.value) else null
        throw Return(value)
    }

    override fun visitVarStmt(stmt: Stmt.Var) {
        var value: Any? = null
        if (stmt.initializer != null) {
            value = evaluate(stmt.initializer)
        }
        environment.define(stmt.name.lexeme, value)
    }

    /* Expr.Visitor */
    override fun visitLogicalExpr(expr: Expr.Logical): Any? {
        val left: Any? = evaluate(expr.left)

        if (expr.operator.type == OR) {
            if (isTruthy(left)) return left
        } else {
            if (!isTruthy(left)) return left
        }

        return evaluate(expr.right)
    }

    override fun visitSetExpr(expr: Expr.Set): Any? {
        val obj: Any? = evaluate(expr.obj)

        if (obj !is MoxInstance) {
            throw RuntimeError(expr.name, "Only instances have fields.")
        }

        val value: Any? = evaluate(expr.value)
        obj.set(expr.name, value)
        return value
    }

    override fun visitSuperExpr(expr: Expr.Super): Any? {
        val distance: Int = locals[expr] ?: 0
        val superclass = environment.getAt(
            distance = distance,
            name = "super"
        ) as MoxClass

        val obj = environment.getAt(
            distance = distance - 1,
            name = "this"
        ) as MoxInstance

        val method = superclass.findMethod(expr.method.lexeme)
            ?: throw RuntimeError(
                token = expr.method,
                message = "Undefined property '${expr.method.lexeme}'."
            )

        return method.bind(obj)
    }

    override fun visitThisExpr(expr: Expr.This): Any? {
        return lookUpVariable(expr.keyword, expr)
    }

    override fun visitVariableExpr(expr: Expr.Variable): Any? {
        return lookUpVariable(expr.name, expr)
    }

    private fun lookUpVariable(name: Token, expr: Expr): Any? {
        val distance = locals[expr]
        return distance?.let {
            environment.getAt(it, name.lexeme)
        } ?: globals.get(name)
    }

    override fun visitUnaryExpr(expr: Expr.Unary): Any? {
        val right = evaluate(expr.right)

        return when (expr.operator.type) {
            BANG -> !isTruthy(right)
            MINUS -> {
                checkNumberOperand(expr.operator, right)
                -(right as Double)
            }

            else -> null
        }
    }

    private fun checkNumberOperand(operator: Token, operand: Any?) {
        if (operand is Double) return
        throw RuntimeError(operator, "Operand must be a number.")
    }

    private fun checkNumberOperands(
        operator: Token,
        left: Any?,
        right: Any?
    ) {
        if (left is Double && right is Double) return
        throw RuntimeError(operator, "Operands must be numbers.")
    }

    override fun visitAssignExpr(expr: Expr.Assign): Any? {
        val value = evaluate(expr.value)
        val distance = locals[expr]
        if (distance != null) {
            environment.assignAt(distance, expr.name, value)
        } else {
            globals.assign(expr.name, value)
        }
        return value
    }

    override fun visitBinaryExpr(expr: Expr.Binary): Any? {
        val left = evaluate(expr.left)
        val right = evaluate(expr.right)

        when (expr.operator.type) {
            GREATER -> {
                checkNumberOperands(expr.operator, left, right)
                return (left as Double) > (right as Double)
            }

            GREATER_EQUAL -> {
                checkNumberOperands(expr.operator, left, right)
                return (left as Double) >= (right as Double)
            }

            LESS -> {
                checkNumberOperands(expr.operator, left, right)
                return (left as Double) < (right as Double)
            }

            LESS_EQUAL -> {
                checkNumberOperands(expr.operator, left, right)
                return (left as Double) <= (right as Double)
            }

            BANG_EQUAL -> return !isEqual(left, right)
            EQUAL_EQUAL -> return isEqual(left, right)
            MINUS -> {
                checkNumberOperands(expr.operator, left, right)
                return (left as Double).minus(right as Double)
            }

            PLUS -> {
                if ((left is Double) && (right is Double)) return left.plus(right)
                if ((left is String) && (right is String)) return left + right
                throw RuntimeError(
                    expr.operator,
                    "Operands must be two numbers or two strings."
                )
            }

            SLASH -> {
                checkNumberOperands(expr.operator, left, right)
                return (left as Double).div(right as Double)
            }

            STAR -> {
                checkNumberOperands(expr.operator, left, right)
                return (left as Double).times(right as Double)
            }

            else -> return null
        }
    }

    override fun visitCallExpr(expr: Expr.Call): Any? {
        val callee = evaluate(expr.callee)

        val arguments = mutableListOf<Any?>()
        for (argument in expr.arguments) {
            arguments.add(evaluate(argument))
        }

        if (callee !is MoxCallable) {
            throw RuntimeError(expr.paren, "Can only call functions and classes.")
        }

        val function = callee
        if (arguments.size != function.arity()) {
            throw RuntimeError(
                expr.paren,
                "Expected ${function.arity()} arguments but got ${arguments.size}."
            )
        }
        return function.call(this, arguments)
    }

    override fun visitGetExpr(expr: Expr.Get): Any? {
        val obj: Any? = evaluate(expr.obj)
        if (obj is MoxInstance) {
            return obj.get(expr.name)
        }

        throw RuntimeError(expr.name, "Only instances have properties.")
    }

    override fun visitTernaryExpr(expr: Expr.Ternary): Any? {
        val first = evaluate(expr.first)
        val second = evaluate(expr.second)
        val third = evaluate(expr.third)

        return if (isTruthy(first)) second else third

    }

    override fun visitGroupingExpr(expr: Expr.Grouping): Any? {
        return evaluate(expr.expression)
    }

    override fun visitLiteralExpr(expr: Expr.Literal): Any? {
        return expr.value
    }


    private fun isTruthy(obj: Any?): Boolean {
        return when (obj) {
            null -> false
            is Boolean -> obj
            else -> true
        }
    }

    private fun isEqual(a: Any?, b: Any?): Boolean {
        return if (a == null && b == null) true
        else a?.equals(b) ?: false
    }


    internal fun evaluate(expr: Expr): Any? {
        return expr.accept(this)
    }

    internal fun execute(stmt: Stmt) {
        stmt.accept(this)
    }

    internal fun executeBlock(statements: List<Stmt>, environment: Environment) {
        val previous = this.environment
        try {
            this.environment = environment
            for (statement in statements) {
                execute(statement)
            }
        } finally {
            this.environment = previous
        }
    }

    private fun stringify(obj: Any?): String {
        if (obj == null) return "nil"

        if (obj is Double) {
            var text: String = obj.toString()
            if (text.endsWith(".0")) {
                text = text.substring(0, text.length - 2)
            }
            return text
        }

        return obj.toString()
    }

    fun resolve(expr: Expr, depth: Int) {
        locals[expr] = depth
    }

}