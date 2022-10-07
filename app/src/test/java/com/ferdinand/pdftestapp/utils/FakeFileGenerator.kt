package com.ferdinand.pdftestapp.utils

import android.net.Uri
import com.ferdinand.pdftestapp.data.models.PdfFile
import com.ferdinand.pdftestapp.data.models.DbFavoritePdfFile

val dbFavFiles = listOf(
    DbFavoritePdfFile(2),
    DbFavoritePdfFile(3),
    DbFavoritePdfFile(4)
)

val pdfFiles = listOf(
    PdfFile(id = 0, pdfName = "Name 0", uri = Uri.parse(""), isFavourite = false),
    PdfFile(id = 1, pdfName = "Name 1", uri = Uri.parse(""), isFavourite = false),
    PdfFile(id = 2, pdfName = "Name 2", uri = Uri.parse(""), isFavourite = false),
    PdfFile(id = 3, pdfName = "Name 3", uri = Uri.parse(""), isFavourite = false)
)