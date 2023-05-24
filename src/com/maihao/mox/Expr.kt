package com.maihao.mox

sealed class Expr {
    interface Visitor<R> {
        fun visitBinaryExpr(expr: Binary): R
        fun visitGroupingExpr(expr: Grouping): R
        fun visitLiteralExpr(expr: Literal): R
        fun visitUnaryExpr(expr: Unary): R
        fun visitTernaryExpr(expr: Ternary): R
    }

    class Binary(val left: Expr, val operator: Token, val right: Expr) : Expr() {

        override fun <R> accept(visitor: Visitor<R>): R {
            return visitor.visitBinaryExpr(this)
        }
    }

    class Grouping(val expression: Expr) : Expr() {

        override fun <R> accept(visitor: Visitor<R>): R {
            return visitor.visitGroupingExpr(this)
        }
    }

    class Literal(val value: Any?) : Expr() {

        override fun <R> accept(visitor: Visitor<R>): R {
            return visitor.visitLiteralExpr(this)
        }
    }

    class Unary(val operator: Token, val right: Expr) : Expr() {

        override fun <R> accept(visitor: Visitor<R>): R {
            return visitor.visitUnaryExpr(this)
        }
    }

    class Ternary(
        val first: Expr,
        val operator1: Token,
        val second: Expr,
        val operator2: Token,
        val third: Expr
    ) : Expr() {
        override fun <R> accept(visitor: Visitor<R>): R {
            return visitor.visitTernaryExpr(this)
        }
    }


    abstract fun <R> accept(visitor: Visitor<R>): R
}
