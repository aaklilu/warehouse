package com.example.warehouse

sealed class ProcessResult<T>
data class Success<T>(val value: T) : ProcessResult<T>()
data class Failure<T>(val value: T? = null, val errorMessage: String) : ProcessResult<T>()

infix fun <T> ProcessResult<T>.otherwise(f: (T?, String) -> Unit) =
    if (this is Failure) f(this.value, this.errorMessage) else Unit

infix fun <T> ProcessResult<T>.then(f: (T) -> ProcessResult<T>) =
    when (this) {
        is Success -> f(this.value)
        is Failure -> Failure(this.value, this.errorMessage)
    }
