package com.ferdinand.pdftestapp.models

sealed class PdfEvent {
    object GetAllFiles : PdfEvent()
    object ErrorDismissed : PdfEvent()
    object SearchEvent : PdfEvent()
    data class OnFavouriteEvent(val pdfFile: PdfFile, val destination: PdfDestination?) : PdfEvent()
}

sealed class PdfDestination {
    object SearchScreen : PdfDestination()
    object MainScreen : PdfDestination()
}
