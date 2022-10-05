package com.ferdinand.pdftestapp.models

sealed class PdfEvent {
    object GetAllFilesEvent : PdfEvent()
    object ErrorDismissedEvent : PdfEvent()
    object SearchEvent : PdfEvent()
    data class OnFavouriteEvent(val pdfFile: PdfFile?) : PdfEvent()
    data class DisplayFileDetailsEvent(val fileId: Long) : PdfEvent()
}

