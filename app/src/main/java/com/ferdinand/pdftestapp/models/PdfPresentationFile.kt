package com.ferdinand.pdftestapp.models

import android.net.Uri

data class PdfPresentationFile(
    val id: String,
    val pdfName: String,
    val uri: Uri,
    val isFavourite: Boolean
)
