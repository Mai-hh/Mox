package com.maihao.mox


class AstPrinter : Expr.Visitor<String> {

    internal fun print(expr: Expr?) = expr?.accept(this)

    override fun visitBinaryExpr(expr: Expr.Binary) = parenthesis(
        name = expr.operator.lexeme,
        exprs = arrayOf(
            expr.left,
            expr.right
        )
    )

    override fun visitGroupingExpr(expr: Expr.Grouping) = parenthesis(
        name = "group",
        exprs = arrayOf(expr.expression)
    )

    override fun visitLiteralExpr(expr: Expr.Literal): String {
        return if (expr.value == null) "nil"
        else {
            expr.value.toString()
        }
    }


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

    val printer = AstPrinter()

    println(printer.print(expression))
}
