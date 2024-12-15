package com.maihao.mox

class MoxFunction(
    private val declaration: Stmt.Function,
    private val closure: Environment,
    private val isInitializer: Boolean
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
            if (isInitializer) return closure.getAt(0, "this")

            return returnValue.value
        }

        if (isInitializer) return closure.getAt(0, "this")

        return null
    }

    fun bind(instance: MoxInstance): MoxFunction {
        val environment = Environment(enclosing = closure)
        environment.define(
            name = "this",
            value = instance
        )

        return MoxFunction(declaration, environment, isInitializer = isInitializer)
    }

    override fun toString(): String {
        return "<fn ${declaration.name.lexeme}>"
    }
}