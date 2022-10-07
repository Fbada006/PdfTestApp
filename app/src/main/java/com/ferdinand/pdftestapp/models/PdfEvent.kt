package com.ferdinand.pdftestapp.models

import com.ferdinand.pdftestapp.data.PdfFile

sealed class PdfEvent {
    object GetAllFilesEvent : PdfEvent()
    object ErrorDismissedEvent : PdfEvent()
    object SearchEvent : PdfEvent()
    object ExportCurrentPageEvent : PdfEvent()
    data class OnFavouriteEvent(val pdfFile: PdfFile?) : PdfEvent()
    data class DisplayFileDetailsEvent(val fileId: Long) : PdfEvent()
}

