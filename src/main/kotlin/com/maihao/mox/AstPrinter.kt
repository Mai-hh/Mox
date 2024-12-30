package com.maihao.mox


class AstPrinter : Expr.Visitor<String>, Stmt.Visitor<String> {

    internal fun print(expr: Expr?) = expr?.accept(this)

    fun printProgram(statements: List<Stmt>): String {
        val builder = StringBuilder()
        for (stmt in statements) {
            builder.append(stmt.accept(this)).append("\n")
        }
        return builder.toString()
    }

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

    override fun visitCallExpr(expr: Expr.Call): String = parenthesis(
        name = "call",
        exprs = arrayOf(expr.callee, *expr.arguments.toTypedArray())
    )

    override fun visitGetExpr(expr: Expr.Get): String = parenthesis(
        name = "get ${expr.name}",
        exprs = arrayOf(expr.obj)
    )

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

    override fun visitSetExpr(expr: Expr.Set): String {
        return parenthesis(
            name = "set ${expr.name}",
            exprs = arrayOf(
                expr.obj,
                expr.value
            )
        )
    }

    override fun visitSuperExpr(expr: Expr.Super): String {
        return parenthesis(
            name = "super ${expr.keyword}, method ${expr.method}",
        )
    }

    override fun visitThisExpr(expr: Expr.This): String {
        return expr.keyword.toString()
    }


    override fun visitUnaryExpr(expr: Expr.Unary) = parenthesis(
        name = expr.operator.lexeme,
        exprs = arrayOf(expr.right)
    )

    override fun visitBlockStmt(stmt: Stmt.Block): String {
        val builder = StringBuilder("(block")
        for (s in stmt.statements) {
            builder.append(" ").append(s.accept(this))
        }
        builder.append(")")
        return builder.toString()
    }

    override fun visitClassStmt(stmt: Stmt.Class): String {
        TODO("Not yet implemented")
    }

    override fun visitExpressionStmt(stmt: Stmt.Expression): String {
        return parenthesis("expr", stmt.expression)
    }

    override fun visitFunctionStmt(stmt: Stmt.Function): String {
        val builder = StringBuilder("(fun ${stmt.name.lexeme} (")
        for ((i, param) in stmt.params.withIndex()) {
            if (i > 0) builder.append(" ")
            builder.append(param.lexeme)
        }
        builder.append(")")
        for (bodyStmt in stmt.body) {
            builder.append(" ").append(bodyStmt.accept(this))
        }
        builder.append(")")
        return builder.toString()
    }

    override fun visitIfStmt(stmt: Stmt.If): String {
        val elsePart = if (stmt.elseBranch != null) " " + stmt.elseBranch.accept(this) else ""
        return "(if ${stmt.condition.accept(this)} ${stmt.thenBranch.accept(this)}$elsePart)"
    }

    override fun visitPrintStmt(stmt: Stmt.Print): String {
        return parenthesis("print", stmt.expression)
    }

    override fun visitReturnStmt(stmt: Stmt.Return): String {
        return if (stmt.value != null) "(return ${stmt.value.accept(this)})" else "(return)"
    }

    override fun visitVarStmt(stmt: Stmt.Var): String {
        return if (stmt.initializer != null) {
            "(var ${stmt.name.lexeme} ${stmt.initializer.accept(this)})"
        } else {
            "(var ${stmt.name.lexeme} nil)"
        }
    }

    override fun visitWhileStmt(stmt: Stmt.While): String {
        return "(while ${stmt.condition.accept(this)} ${stmt.body.accept(this)})"
    }

    // 为了方便，也可以创建一个针对语句的辅助打印函数
    // 这里直接用在 Stmt 里使用 expr 时重复利用 expr 的 accept 方法。

    // 重载 parenthesis 用于直接处理单个Expr
    private fun parenthesis(name: String, vararg exprs: Expr?): String {
        val builder = StringBuilder("(").append(name)
        for (e in exprs) {
            builder.append(" ")
            if (e != null) builder.append(e.accept(this)) else builder.append("nil")
        }
        builder.append(")")
        return builder.toString()
    }
}
