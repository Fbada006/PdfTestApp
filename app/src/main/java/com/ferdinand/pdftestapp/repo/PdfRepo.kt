package com.ferdinand.pdftestapp.repo

import com.ferdinand.pdftestapp.models.PdfFile
import com.ferdinand.pdftestapp.utils.Resource

interface PdfRepo {

    suspend fun getPdfList(): Resource<List<PdfFile>>
    suspend fun getPdfListBasedOnQuery(pdfFiles: List<PdfFile>?, searchTerm: String): Resource<List<PdfFile>>
}