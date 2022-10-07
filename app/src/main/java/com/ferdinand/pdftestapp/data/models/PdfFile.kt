package com.ferdinand.pdftestapp.data.models

import android.net.Uri

data class PdfFile(
    val id: Long,
    val pdfName: String,
    val uri: Uri,
    val isFavourite: Boolean
)