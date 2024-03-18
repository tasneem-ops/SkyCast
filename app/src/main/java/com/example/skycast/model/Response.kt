package com.example.skycast.model

sealed class Response<out T> {
    class Success<T>(data : T) : Response<T>()
    class Failure(msg : String) : Response<Nothing>()
    class Loading() : Response<Nothing>()
}