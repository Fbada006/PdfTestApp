package com.ferdinand.pdftestapp.mappers

import com.ferdinand.pdftestapp.data.models.DbFavoritePdfFile
import com.ferdinand.pdftestapp.data.models.PdfFile
import com.ferdinand.pdftestapp.models.PdfPresentationFile

/**
 * The helper extension functions in this class help with mapping between models needed for the data and the
 * presentation layer. In this case, we do not really need the optional domain model as explained in the
 * documentation as explained below
 *
 * @see <a href="https://developer.android.com/topic/architecture#domain-layer">Guide to app architecture</a>
 * */
fun PdfFile.toDbModel() = DbFavoritePdfFile(this.id)

fun PdfFile.toPresentationModel() =
    PdfPresentationFile(id = this.id, pdfName = this.pdfName, uri = this.uri, isFavourite = this.isFavourite)

fun PdfPresentationFile.toDataModel() =
    PdfFile(id = this.id, pdfName = this.pdfName, uri = this.uri, isFavourite = this.isFavourite)