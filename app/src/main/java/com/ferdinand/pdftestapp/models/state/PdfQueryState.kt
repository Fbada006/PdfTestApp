package com.ferdinand.pdftestapp.models.state

import com.ferdinand.pdftestapp.models.PdfFile

/*
* This class will handle the pdf querying state to determine what is going to be shown on screen
* on either success or failure to query the data from the device file system. The viewmodel will update this
* state depending on the data received from [com.ferdinand.pdftestapp.viewmodel.PdfViewModel]
*/
data class PdfQueryState(
    val isLoading: Boolean = false,
    val error: Throwable? = null,
    val listData: List<PdfFile>? = null,
    val singlePdfData: PdfFile? = null
)
