package com.maihao.mox

class Token(
    val type: TokenType,
    val lexeme: String,
    val literal: Any?,
    val line: Int
) {
    override fun toString() = "$type $lexeme $literal"
}