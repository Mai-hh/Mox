package com.maihao.tool

import java.io.IOException
import java.io.PrintWriter
import kotlin.jvm.Throws
import kotlin.system.exitProcess

fun main(args: Array<String>) {
    if (args.size != 1) {
        System.err.println("Usage: generate_ast <output directory>")
        exitProcess(64)
    }
    val outputDir = args[0]
    defineAst(
        outputDir,
        baseName = "Expr",
        types = listOf(
            "Assign   -> name: Token, value: Expr",
            "Binary   -> left: Expr, operator: Token, right: Expr",
            "Call     -> callee: Expr, paren: Token, arguments: List<Expr>",
            "Grouping -> expression: Expr",
            "Literal  -> value: Any?",
            "Variable -> name: Token",
            "Logical  -> left: Expr, operator: Token, right: Expr",
            "Unary    -> operator: Token, right: Expr",
            "Ternary  -> first: Expr, operator1: Token, second: Expr, operator2: Token, third: Expr"
        )
    )
    defineAst(outputDir, "Stmt", listOf(
        "Block        -> statements: List<Stmt>",
        "Expression   -> expression: Expr",
        "Function     -> name: Token, params: List<Token>, body: List<Stmt>",
        "If           -> condition: Expr, thenBranch: Stmt, elseBranch: Stmt?",
        "Print        -> expression: Expr",
        "Return       -> keyword: Token, value: Expr?",
        "Var          -> name: Token, initializer: Expr?",
        "While        -> condition: Expr, body: Stmt"
    ))
}

@Throws(IOException::class)
private fun defineAst(
    outputDir: String,
    baseName: String,
    types: List<String>
) {
    val path = "$outputDir/$baseName.kt"
    val writer = PrintWriter(path, Charsets.UTF_8)
    writer.run {
        println("package com.maihao.mox")
        println()
        println("sealed class $baseName {")
        defineVisitor(writer, baseName, types)
        for (type in types) {
            val className = type.split("->")[0].trim()
            val fields = type.split("->")[1].trim()
            defineType(writer, baseName, className, fields)
        }
        println()
        println("\tabstract fun <R> accept(visitor: Visitor<R>): R")
        println("}")
        close()
    }
}

private fun defineType(
    writer: PrintWriter,
    baseName: String,
    className: String,
    fieldList: String
) {
    writer.run {
        println("\tclass $className(${constructor(fieldList)}) : $baseName() {")

        // Visitor pattern.
        println()
        println("\t\toverride fun <R> accept(visitor: Visitor<R>): R {")
        println("\t\t\treturn visitor.visit$className$baseName(this)")
        println("\t\t}")
        println("\t}")
        println()
    }
}


private fun constructor(fieldList: String): String {
    val fields: List<String> = fieldList.split(", ")
    var params = ""
    for (field: String in fields) {
        params += "val $field,"
    }
    return params
}


private fun defineVisitor(
    writer: PrintWriter,
    baseName: String,
    types: List<String>
) {
    writer.run {
        println("\tinterface Visitor<R> {")
        types.forEach {
            val typeName = it.split("->")[0].trim()
            println("\t\tfun visit$typeName$baseName(${baseName.lowercase()}: $typeName): R")
        }
        writer.println("\t}")
    }
}
