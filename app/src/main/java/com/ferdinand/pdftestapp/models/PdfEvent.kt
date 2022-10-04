package com.ferdinand.pdftestapp.models

sealed class PdfEvent {
    object GetAllFiles : PdfEvent()
    object ErrorDismissed : PdfEvent()
    object SearchEvent : PdfEvent()
    data class OnFavouriteEvent(val pdfFile: PdfFile) : PdfEvent()
}

