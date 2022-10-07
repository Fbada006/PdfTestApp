package com.ferdinand.pdftestapp.mappers

import com.ferdinand.pdftestapp.data.models.PdfFile
import com.ferdinand.pdftestapp.data.models.DbFavoritePdfFile
import com.ferdinand.pdftestapp.models.PdfPresentationFile

fun PdfFile.toDbModel() = DbFavoritePdfFile(this.id)

fun PdfFile.toPresentationModel() =
    PdfPresentationFile(id = this.id, pdfName = this.pdfName, uri = this.uri, isFavourite = this.isFavourite)

fun PdfPresentationFile.toDataModel() =
    PdfFile(id = this.id, pdfName = this.pdfName, uri = this.uri, isFavourite = this.isFavourite)