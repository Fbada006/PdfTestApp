package com.ferdinand.pdftestapp.models

sealed class PdfEvent {
    object ErrorDismissed : PdfEvent()
    data class SearchEvent(val searchTerm: String) : PdfEvent()
    data class OnFavouriteEvent(val pdfFile: PdfFile) : PdfEvent()
}
