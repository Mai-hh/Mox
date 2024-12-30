package com.maihao.mox

class MoxClass(val name: String, val superclass: MoxClass?, val methods: Map<String, MoxFunction>) : MoxCallable {

    fun findMethod(name: String): MoxFunction? {
        if (methods.containsKey(name)) {
            return methods[name]
        }

        if (superclass != null) {
            return superclass.findMethod(name)
        }

        return null
    }

    override fun arity(): Int {
        val initializer = findMethod("init")
        return initializer?.arity() ?: 0
    }

    override fun call(
        interpreter: Interpreter,
        arguments: List<Any?>
    ): Any? {
        val instance = MoxInstance(this)
        val initializer = findMethod("init")
        initializer?.bind(instance)?.call(interpreter, arguments)
        return instance
    }

    override fun toString(): String {
        return name
    }
}

class MoxInstance(private val klass: MoxClass) {

    private val fields = mutableMapOf<String, Any?>()

    fun get(name: Token): Any? {
        if (fields.containsKey(name.lexeme)) {
            return fields[name.lexeme]
        }

        val method = klass.findMethod(name.lexeme)
        if (method != null) {
            return method.bind(this)
        }

        throw RuntimeError(name, "Undefined property '${name.lexeme}'.")
    }

    fun set(name: Token, value: Any?) {
        fields[name.lexeme] = value
    }

    override fun toString(): String {
        return klass.name + " instance"
    }
}