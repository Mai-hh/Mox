package com.maihao.mox

class MoxFunction(
    private val declaration: Stmt.Function,
    private val closure: Environment
) : MoxCallable {

    override fun arity() = declaration.params.size

    override fun call(interpreter: Interpreter, arguments: List<Any?>): Any? {
        val environment = Environment(enclosing = closure)

        for(i in 0 until  declaration.params.size) {
            environment.define(declaration.params[i].lexeme, arguments[i])
        }

        try {
            interpreter.executeBlock(declaration.body, environment)
        } catch (returnValue: Return) {
            return returnValue.value
        }

        return null
    }

    override fun toString(): String {
        return "<anonymous fn ${declaration.name.lexeme}>"
    }
}

class MoxLambda(
    private val closure: Environment
) : MoxCallable {

    private lateinit var params: List<Token>
    private lateinit var body: List<Stmt>

    constructor(
        declaration: Stmt.Lambda,
        closure: Environment
    ) : this(closure = closure) {
        params = declaration.params
        body = declaration.body
    }

    constructor(
        declaration: Expr.Lambda,
        closure: Environment
    ) : this(closure = closure) {
        params = declaration.params
        body = declaration.body
    }

    override fun arity() = params.size

    override fun call(interpreter: Interpreter, arguments: List<Any?>): Any? {
        val environment = Environment(enclosing = closure)

        for(i in params.indices) {
            environment.define(params[i].lexeme, arguments[i])
        }

        try {
            interpreter.executeBlock(body, environment)
        } catch (returnValue: Return) {
            return returnValue.value
        }

        return null
    }

}