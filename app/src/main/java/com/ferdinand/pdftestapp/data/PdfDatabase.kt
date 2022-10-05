package com.ferdinand.pdftestapp.data

import androidx.room.Database
import androidx.room.RoomDatabase
import com.ferdinand.pdftestapp.data.models.DbFavoritePdfFile

@Database(
    entities = [DbFavoritePdfFile::class],
    version = 1,
    exportSchema = false
)
abstract class PdfDatabase : RoomDatabase() {

    abstract fun pdfDao(): PdfDao
}