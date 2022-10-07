package com.ferdinand.pdftestapp.data

import android.net.Uri

data class PdfFile(
    val id: Long,
    val pdfName: String,
    val uri: Uri,
    val isFavourite: Boolean
)