package com.ferdinand.pdftestapp.utils

import android.widget.Toast
import androidx.fragment.app.Fragment

/*
* Helper toast extension function for Fragments
* */
fun Fragment.toast(message: String) {
    Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
}