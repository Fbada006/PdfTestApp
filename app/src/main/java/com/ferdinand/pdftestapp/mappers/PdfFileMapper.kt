package com.ferdinand.pdftestapp.mappers

import com.ferdinand.pdftestapp.data.models.DbFavoritePdfFile
import com.ferdinand.pdftestapp.models.PdfFile

fun PdfFile.toDbModel() = DbFavoritePdfFile(this.id)