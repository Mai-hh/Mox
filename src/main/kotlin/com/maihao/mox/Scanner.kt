package com.maihao.mox

import com.maihao.mox.TokenType.*


class Scanner(
    private val source: String
) {
    private val tokens = ArrayList<Token>()
    private var start = 0
    private var current = 0
    private var line = 1

    companion object {
        private val keywords = hashMapOf<String, TokenType?>(
            "and" to AND,
            "class" to CLASS,
            "else" to ELSE,
            "false" to FALSE,
            "for" to FOR,
            "fun" to FUN,
            "if" to IF,
            "nil" to NIL,
            "or" to OR,
            "print" to PRINT,
            "return" to RETURN,
            "super" to SUPER,
            "this" to THIS,
            "true" to TRUE,
            "var" to VAR,
            "while" to WHILE
        )
    }

    fun scanTokens(): List<Token> {
        while (!isAtEnd()) {
            // We are at the beginning of the next lexeme.
            start = current
            scanToken()
        }
        tokens.add(Token(EOF, "", null, line))
        return tokens
    }

    private fun scanToken() {
        when (val c = advance()) {
            '(' -> addToken(LEFT_PAREN)
            ')' -> addToken(RIGHT_PAREN)
            '{' -> addToken(LEFT_BRACE)
            '}' -> addToken(RIGHT_BRACE)
            ',' -> addToken(COMMA)
            '.' -> addToken(DOT)
            '-' -> addToken(MINUS)
            '+' -> addToken(PLUS)
            ';' -> addToken(SEMICOLON)
            '*' -> addToken(STAR)
            '!' -> addToken(if (match('=')) BANG_EQUAL else BANG)
            '=' -> addToken(if (match('=')) EQUAL_EQUAL else EQUAL)
            '<' -> addToken(if (match('=')) LESS_EQUAL else LESS)
            '>' -> addToken(if (match('=')) GREATER_EQUAL else GREATER)
            '/' -> {
                if (match('/')) {
                    // A comment goes until the end of the line.
                    commentsLine()
                } else if (match('*')) {
                    commentsBlock()
                } else {
                    addToken(SLASH)
                }
            }

            '?' -> addToken(QUESTION_MARK)
            ':' -> addToken(COLON)

            ' ',
            '\r',
            '\t' -> { }
            '\n' -> line++

            '"' -> string()

            else -> {
                if (isDigit(c)) {
                    number()
                } else if(isAlpha(c)) {
                    identifier()
                } else {
                    Mox.error(line, "Unexpected character.")
                }
            }
        }
    }

    private fun commentsLine() {
        while (peek() != '\n' && !isAtEnd()) advance()
    }

    private fun commentsBlock() {
        while (!isAtEnd()) {
            if (peek() == '*' && peekNext() == '/') {
                repeat(2) {
                    advance()
                }
                return
            }
            advance()
        }
        Mox.error(line, "No terminator in the comments block.")
    }

    private fun identifier() {
        while (isAlphaNumeric(peek())) advance()

        val text = source.substring(start, current)
        var type = keywords[text]
        if (type == null) {
            type = IDENTIFIER
        }
        addToken(type)
    }

    private fun isAlpha(c: Char): Boolean {
        return c in 'a'..'z' || c in 'A'..'Z' || c == '_'
    }

    private fun isAlphaNumeric(c: Char): Boolean {
        return isAlpha(c) || isDigit(c)
    }

    private fun isDigit(c: Char) = c in '0'..'9'

    private fun string() {
        while (peek() != '"' && !isAtEnd()) {
            if (peek() == '\n') line++
            advance()
        }

        if (isAtEnd()) {
            Mox.error(line, "Unterminated string.")
            return
        }

        // The closing ".
        advance()

        // Trim the surrounding quotes.
        val value = source.substring(start + 1, current - 1)
        addToken(STRING, value)
    }

    private fun number() {
        while (isDigit(peek())) advance()

        // Look for a fractional part.
        if (peek() == '.' && isDigit(peekNext())) {
            // Consume the "."
            advance()

            while (isDigit(peek())) advance()
        }

        addToken(NUMBER, source.substring(start, current).toDouble())
    }

    private fun peekNext(): Char {
        return if (current + 1 >= source.length) Char.MIN_VALUE else source[current + 1]
    }

    private fun peek(): Char {
        if (isAtEnd()) return Char.MIN_VALUE
        return source[current]
    }

    private fun isAtEnd(): Boolean {
        return current >= source.length
    }

    private fun advance() = source[current++]

    private fun addToken(type: TokenType, literal: Any? = null) {
        val text = source.substring(start, current)
        tokens.add(Token(type, text, literal, line))
    }

    private fun match(expected: Char): Boolean {
        if (isAtEnd()) return false
        if (source[current] != expected) return false

        current++
        return true
    }

}