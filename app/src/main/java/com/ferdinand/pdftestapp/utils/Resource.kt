package com.ferdinand.pdftestapp.utils

/*
* This is a helper class we use to wrap around the result that we get back from
* doing operations such as querying for files
*
*/
sealed class Resource<T> {
    data class Success<T>(val data: T) : Resource<T>()
    data class Error<T>(val error: Throwable) : Resource<T>()
}