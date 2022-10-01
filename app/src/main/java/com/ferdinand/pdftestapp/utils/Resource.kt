package com.ferdinand.pdftestapp.utils

/*
* This is a helper class we use to wrap around the result that we get back from
* querying for all the pdf files from the device
*
*/
sealed class Resource<T> {
    data class Success<T>(val data: T) : Resource<T>()
    data class Error<T>(val error: Throwable) : Resource<T>()
}