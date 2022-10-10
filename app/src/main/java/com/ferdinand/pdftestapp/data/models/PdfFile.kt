package com.ferdinand.pdftestapp.data.models

import android.net.Uri

/*
* This is the data layer representation of the pdf doc from the file system
* */
data class PdfFile(
    val id: String,
    val pdfName: String,
    val uri: Uri,
    val isFavourite: Boolean
)