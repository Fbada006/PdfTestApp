package com.ferdinand.pdftestapp.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.ferdinand.pdftestapp.data.models.DbFavoritePdfFile

@Dao
interface PdfDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addToFav(pdfFile: DbFavoritePdfFile)

    @Query("SELECT * FROM favouritePdfs WHERE id LIKE :fileId")
    suspend fun getFileById(fileId: String): DbFavoritePdfFile?

    @Delete
    suspend fun removeFromFav(pdfFile: DbFavoritePdfFile)
}