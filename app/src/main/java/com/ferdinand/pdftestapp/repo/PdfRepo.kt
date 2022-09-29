package com.ferdinand.pdftestapp.repo

import com.ferdinand.pdftestapp.models.PdfFile

interface PdfRepo {

    suspend fun getPdfList(): List<PdfFile>
}