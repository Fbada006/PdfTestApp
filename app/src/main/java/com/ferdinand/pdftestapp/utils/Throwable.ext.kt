package com.ferdinand.pdftestapp.utils

import android.content.Context
import com.ferdinand.pdftestapp.R

/**
* Helper extension function to determine the error message display to the user
 *
 * @param context is the app context
 *
 * @return a string representation of the error
* */
fun Throwable.errorToString(context: Context) =
    when (this) {
        is EmptyListException -> {
            context.getString(R.string.empty_list)
        }
        else -> {
            context.getString(R.string.something_went_wrong)

        }
    }