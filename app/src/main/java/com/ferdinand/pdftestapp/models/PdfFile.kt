package com.ferdinand.pdftestapp.models

import android.net.Uri
import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class PdfFile(
    val pdfName: String,
    val uri: Uri,
    val isFavourite: Boolean = false
): Parcelable