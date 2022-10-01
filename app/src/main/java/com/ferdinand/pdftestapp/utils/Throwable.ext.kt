package com.ferdinand.pdftestapp.utils

import android.content.Context
import com.ferdinand.pdftestapp.R

fun Throwable.errorToString(context: Context) =
    when (this) {
        is EmptyListException -> {
            context.getString(R.string.empty_list)
        }
        else -> {
            context.getString(R.string.something_went_wrong)

        }
    }