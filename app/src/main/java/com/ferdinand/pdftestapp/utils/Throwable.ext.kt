package com.ferdinand.pdftestapp.utils

fun Throwable.errorToString() =
    when (this) {
        is EmptyListException -> {
            ""
        }
        else -> {
            ""
        }
    }