package com.example.nasaphotos.model

sealed class RemoteResponse<out R : Any, out E : ErrorModel> {
    class Data<out R : Any>(val data: List<PhotosItem?>?) : RemoteResponse<R, Nothing>()
    class Error<out E : ErrorModel>(val error: E) : RemoteResponse<Nothing, E>()
}