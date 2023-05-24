package com.maihao.mox

import com.maihao.mox.TokenType.*

class Interpreter : Expr.Visitor<Any?> {

    override fun visitUnaryExpr(expr: Expr.Unary): Any? {
        val right = evaluate(expr.right)

        return when (expr.operator.type) {
            BANG -> !isTruthy(right)
            MINUS -> -(right as Double)
            else -> null
        }
    }

    override fun visitBinaryExpr(expr: Expr.Binary): Any? {
        val left = evaluate(expr.left)
        val right = evaluate(expr.right)

        when (expr.operator.type) {
            GREATER -> return (left as Double) > (right as Double)
            GREATER_EQUAL -> return (left as Double) >= (right as Double)
            LESS -> return (left as Double) < (right as Double)
            LESS_EQUAL -> return (left as Double) <= (right as Double)
            BANG_EQUAL -> return !isEqual(left, right)
            EQUAL_EQUAL -> return isEqual(left, right)
            MINUS -> return (left as Double).minus(right as Double)
            PLUS -> {
                if ((left is Double) && (right is Double)) return left.plus(right)
                if ((left is String) && (right is String)) return left + right
            }
            SLASH -> return (left as Double).div(right as Double)
            STAR -> return (left as Double).times(right as Double)
            else -> return null
        }

        return null
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
}