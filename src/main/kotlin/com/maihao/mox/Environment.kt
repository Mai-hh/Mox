package com.maihao.mox

class Environment(
    var enclosing: Environment? = null
) {
    private val values: MutableMap<String, Any?> = mutableMapOf()
    internal fun define(name: String, value: Any?) {
        values[name] = value
    }

    internal operator fun get(name: Token): Any? {
        if (values.containsKey(name.lexeme)) {
            return values[name.lexeme]
        }

        enclosing?.run {
            return get(name)
        }

        throw RuntimeError(
            name,
            "Undefined variable '${name.lexeme}'."
        )
    }

    fun assign(name: Token, value: Any?) {
        if (values.containsKey(name.lexeme)) {
            values[name.lexeme] = value
            return
        }

        enclosing?.run {
            assign(name, value)
            return
        }

        throw RuntimeError(
            name,
            "Undefined variable '${name.lexeme}'."
        )
    }
}