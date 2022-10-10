package com.ferdinand.pdftestapp.models.state

import com.ferdinand.pdftestapp.models.PdfPresentationFile

/**
 * This class will handle the pdf querying state to determine what is going to be shown on screen
 * on either success or failure to query the data from the device file system. The [com.ferdinand.pdftestapp.viewmodel.PdfViewModel] will update this
 * state depending on the data received from [com.ferdinand.pdftestapp.viewmodel.PdfViewModel].
 * For the purposes of re-usability, both the single and list data items are included here then they can be updated and queried as needed
 */
data class PdfQueryState(
    val isLoading: Boolean = false,
    val error: Throwable? = null,
    val listData: List<PdfPresentationFile>? = null,
    val singlePdfData: PdfPresentationFile? = null
)
