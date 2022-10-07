package com.ferdinand.pdftestapp.models

sealed class PdfEvent {
    object GetAllFilesEvent : PdfEvent()
    object ErrorDismissedEvent : PdfEvent()
    object SearchEvent : PdfEvent()
    object ExportCurrentPageEvent : PdfEvent()
    data class OnFavouriteEvent(val pdfFile: PdfPresentationFile?) : PdfEvent()
    data class DisplayFileDetailsEvent(val fileId: Long) : PdfEvent()
}

