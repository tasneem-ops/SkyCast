package com.example.skycast.model

sealed class Response<out T> {
    class Success<T>(val data : T) : Response<T>()
    class Failure(val msg : String) : Response<Nothing>()
    class Loading() : Response<Nothing>()
}