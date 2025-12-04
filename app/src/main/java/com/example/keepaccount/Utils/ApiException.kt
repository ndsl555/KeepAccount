package com.example.keepaccount.Utils

class ApiException(
    message: String = "",
    val code: Int,
    val result: Int = -1,
) : RuntimeException(message)
