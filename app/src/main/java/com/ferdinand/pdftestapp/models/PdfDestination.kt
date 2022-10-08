package com.ferdinand.pdftestapp.models

sealed class PdfDestination {
    object MainScreen : PdfDestination()
    object SearchScreen : PdfDestination()
}