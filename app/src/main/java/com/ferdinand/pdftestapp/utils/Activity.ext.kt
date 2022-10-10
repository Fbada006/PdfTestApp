package com.ferdinand.pdftestapp.utils

import android.app.Activity
import android.widget.Toast

/*
* Helper extension function for Activities
* */
fun Activity.toast(message: String) {
    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
}