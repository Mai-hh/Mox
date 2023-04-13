package com.maihao.mox

import java.io.BufferedReader
import java.io.InputStreamReader
import java.nio.charset.Charset
import java.nio.file.Files
import java.nio.file.Paths
import kotlin.system.exitProcess

fun main(args: Array<String>) {
    if (args.size > 1) {
        println("Usage: mox [script]")
        exitProcess(64)
    } else if (args.size == 1) {
        Mox.runFile(args[0])
    } else {
        Mox.runPrompt()
    }
}

class Mox {
    companion object {
        var hadError = false

        internal fun runFile(path: String) {
            val bytes = Files.readAllBytes(Paths.get(path))
            run(String(bytes, Charset.defaultCharset()))

            if (hadError) exitProcess(65)
        }

        internal fun runPrompt() {
            val input = InputStreamReader(System.`in`)
            val reader = BufferedReader(input)

            while (true) {
                print("> ")
                val line = reader.readLine() ?: break
                run(line)
                hadError = false
            }
        }

        private fun run(source: String) {
            val scanner = Scanner(source)
            val tokens: List<Token> = scanner.scanTokens()
            val parser = Parser(tokens)
            val expression: Expr? = parser.parse()

            // Stop if there was a syntax error.
            if (hadError) return

            tokens.forEach {
                println(it)
            }

            println(AstPrinter().print(expression))
        }

        private fun report(line: Int, where: String, message: String) {
            System.err.println("[line $line] Error $where: $message")
            hadError = true
        }

        fun error(line: Int, message: String) {
            report(line, "", message)
        }

        fun error(token: Token, message: String) {
            if (token.type == TokenType.EOF) {
                report(token.line, " at end", message)
            } else {
                report(token.line, " at '${token.lexeme}'", message)
            }
        }
    }
}













