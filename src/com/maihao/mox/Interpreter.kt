package com.maihao.mox

import com.maihao.mox.TokenType.*


class Interpreter : Expr.Visitor<Any?> {

    fun interpret(expression: Expr?) {
        try {
            val value = evaluate(expression!!)
            println(stringify(value))
        } catch (error: RuntimeError) {
            Mox.runtimeError(error)
        }
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


    private fun evaluate(expr: Expr): Any? {
        return expr.accept(this)
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
}