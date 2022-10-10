package com.ferdinand.pdftestapp.models

import android.net.Uri

/*
* This is the presentation/UI layer representation of the pdf doc from the file system
* */
data class PdfPresentationFile(
    val id: String,
    val pdfName: String,
    val uri: Uri,
    var isFavourite: Boolean
)
