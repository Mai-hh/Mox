package com.maihao.mox


class AstPrinter : Expr.Visitor<String> {

    internal fun print(expr: Expr?) = expr?.accept(this)

    override fun visitAssignExpr(expr: Expr.Assign): String = parenthesis(
        name = "assignment ${expr.name}",
        exprs = arrayOf(
            expr.value
        )
    )

    override fun visitBinaryExpr(expr: Expr.Binary) = parenthesis(
        name = expr.operator.lexeme,
        exprs = arrayOf(
            expr.left,
            expr.right
        )
    )

    override fun visitCallExpr(expr: Expr.Call): String {
        return "1"
    }

    override fun visitTernaryExpr(expr: Expr.Ternary) = parenthesis(
        name = expr.operator1.lexeme + expr.operator2.lexeme,
        exprs = arrayOf(
            expr.first,
            expr.second,
            expr.third
        )
    )

    override fun visitGroupingExpr(expr: Expr.Grouping) = parenthesis(
        name = "group",
        exprs = arrayOf(expr.expression)
    )

    override fun visitLambdaExpr(expr: Expr.Lambda): String {
        return "2"
    }

    override fun visitLiteralExpr(expr: Expr.Literal): String {
        return if (expr.value == null) "nil"
        else {
            expr.value.toString()
        }
    }

    override fun visitVariableExpr(expr: Expr.Variable): String {
        return expr.name.toString()
    }

    override fun visitLogicalExpr(expr: Expr.Logical): String = parenthesis(
        name = expr.operator.lexeme,
        exprs = arrayOf(
            expr.left,
            expr.right
        )
    )


    override fun visitUnaryExpr(expr: Expr.Unary) = parenthesis(
        name = expr.operator.lexeme,
        exprs = arrayOf(expr.right)
    )

    private fun parenthesis(
        name: String,
        vararg exprs: Expr
    ): String {
        val builder = StringBuilder()
        builder.append("(").append(name)
        exprs.forEach {
            builder.append(" ")
            builder.append(it.accept(this))
        }
        builder.append(")")
        return builder.toString()
    }

    private fun rpnString(
        name: String,
        vararg exprs: Expr
    ): String {
        val builder = StringBuilder()
        exprs.forEach {
            builder.append(it.accept(this))
            builder.append(" ")
        }
        builder.append(name)
        return builder.toString()
    }
}

fun main(args: Array<String>) {
    val expression = Expr.Binary(
        left = Expr.Binary(
            left = Expr.Literal(1),
            operator = Token(
                type = TokenType.PLUS,
                lexeme = "+",
                literal = null,
                line = 1
            ),
            right = Expr.Literal(2)
        ),
        operator = Token(
            type = TokenType.STAR,
            lexeme = "*",
            literal = null,
            line = 1
        ),
        right = Expr.Binary(
            left = Expr.Literal(4),
            operator = Token(
                type = TokenType.MINUS,
                lexeme = "-",
                literal = null,
                line = 1
            ),
            right = Expr.Literal(3)
        )
    )
}
