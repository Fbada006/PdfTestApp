package com.ferdinand.pdftestapp.models

/**
 *This class is for convenience to use in the [com.ferdinand.pdftestapp.viewmodel.PdfViewModel] to determine which operation is to be done.
 * */
sealed class PdfEvent {
    object GetAllFilesEvent : PdfEvent()
    object ErrorDismissedEvent : PdfEvent()
    object SearchEvent : PdfEvent()
    object ExportCurrentPageEvent : PdfEvent()
    data class OnFavouriteEvent(val pdfFile: PdfPresentationFile?) : PdfEvent()
    data class DisplayFileDetailsEvent(val fileId: String) : PdfEvent()
}

