package com.maihao.mox

class RuntimeError(
    val token: Token,
    message: String?
) : RuntimeException(message)