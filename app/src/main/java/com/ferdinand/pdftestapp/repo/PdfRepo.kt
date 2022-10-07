package com.ferdinand.pdftestapp.repo

import com.ferdinand.pdftestapp.data.models.DbFavoritePdfFile
import com.ferdinand.pdftestapp.data.models.PdfFile
import com.ferdinand.pdftestapp.utils.Resource
import com.pspdfkit.document.processor.PdfProcessor
import io.reactivex.Flowable

interface PdfRepo {

    suspend fun getPdfList(): Resource<List<PdfFile>>
    suspend fun getPdfListBasedOnQuery(searchTerm: String): Resource<List<PdfFile>>
    suspend fun addOrRemoveFileFromFav(pdfFile: DbFavoritePdfFile)
    suspend fun getPdfFileBasedOnId(id: Long): Resource<PdfFile?>
    fun exportCurrentPageToPdf(pdfFile: PdfFile, currentPage: Int): Flowable<PdfProcessor.ProcessorProgress>
}