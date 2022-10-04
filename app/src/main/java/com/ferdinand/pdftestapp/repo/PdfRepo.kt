package com.ferdinand.pdftestapp.repo

import com.ferdinand.pdftestapp.data.models.DbFavoritePdfFile
import com.ferdinand.pdftestapp.models.PdfFile
import com.ferdinand.pdftestapp.utils.Resource

interface PdfRepo {

    suspend fun getPdfList(): Resource<List<PdfFile>>
    suspend fun getPdfListBasedOnQuery(searchTerm: String): Resource<List<PdfFile>>
    suspend fun addOrRemoveFileFromFav(pdfFile: DbFavoritePdfFile)
}