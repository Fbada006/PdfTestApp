package com.ferdinand.pdftestapp.repo

import android.content.Context
import android.database.Cursor
import android.os.Build
import android.provider.MediaStore
import android.webkit.MimeTypeMap
import androidx.core.database.getStringOrNull
import androidx.core.net.toUri
import com.ferdinand.pdftestapp.data.PdfDatabase
import com.ferdinand.pdftestapp.data.models.DbFavoritePdfFile
import com.ferdinand.pdftestapp.models.PdfFile
import com.ferdinand.pdftestapp.utils.EmptyListException
import com.ferdinand.pdftestapp.utils.Resource
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import javax.inject.Inject

class PdfRepoImpl @Inject constructor(
    private val context: Context,
    private val database: PdfDatabase,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) : PdfRepo {

    private val projection = arrayOf(
        MediaStore.Files.FileColumns._ID,
        MediaStore.Files.FileColumns.DISPLAY_NAME,
        MediaStore.Files.FileColumns.DATE_ADDED,
        MediaStore.Files.FileColumns.DATA,
        MediaStore.Files.FileColumns.MIME_TYPE
    )

    private val sortOrder = MediaStore.Files.FileColumns.DATE_ADDED + " DESC"
    private val selection = MediaStore.Files.FileColumns.MIME_TYPE + " = ?"
    private val mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(EXTENSION_PDF)
    private val selectionArgs = arrayOf(mimeType)

    private val collection = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        MediaStore.Downloads.getContentUri(MediaStore.VOLUME_EXTERNAL)
    } else {
        MediaStore.Files.getContentUri(VOLUME_EXTERNAL)
    }

    /*
    * Since we are not sure how long this query will take, it makes sense to wrap it around a coroutine and take
    * this functionality off the main thread to the IO one instead
    * */
    override suspend fun getPdfList(): Resource<List<PdfFile>> {
        return withContext(dispatcher) {
            try {
                val pdfList = getPdfListFromFile()

                if (pdfList.isNotEmpty()) {
                    Resource.Success(pdfList)
                } else {
                    Resource.Error(EmptyListException())
                }
            } catch (exception: Exception) {
                Resource.Error(exception)
            }
        }
    }

    private suspend fun getPdfListFromFile(): List<PdfFile> {
        val pdfList = mutableListOf<PdfFile>()

        context.contentResolver.query(collection, projection, selection, selectionArgs, sortOrder).use { cursor ->
            cursor?.let {
                if (it.moveToFirst()) {

                    val columnData = cursor.getColumnIndex(MediaStore.Files.FileColumns.DATA)
                    val columnName = cursor.getColumnIndex(MediaStore.Files.FileColumns.DISPLAY_NAME)
                    val columnId = cursor.getColumnIndex(MediaStore.Files.FileColumns._ID)

                    while (it.moveToNext()) {
                        val pathData = cursor.getString(columnData)
                        val isDownloadData = isDownloadData(pathData)
                        if (isDownloadData) {
                            addPdfFileToList(cursor, columnName, columnId, pathData, pdfList)
                        }
                    }
                }
            }
        }
        return pdfList
    }

    override suspend fun getPdfFileBasedOnId(id: Long): Resource<PdfFile?> {
        return withContext(dispatcher) {
            try {
                var pdf: PdfFile? = null
                val selection = MediaStore.Files.FileColumns._ID + " = ?"
                val selectionArgs = arrayOf(id.toString())

                context.contentResolver.query(
                    collection,
                    projection,
                    selection,
                    selectionArgs,
                    null
                )
                    .use { cursor ->
                        cursor?.let {
                            if (cursor.moveToFirst()) {
                                val columnData = cursor.getColumnIndex(MediaStore.Files.FileColumns.DATA)
                                val columnName = cursor.getColumnIndex(MediaStore.Files.FileColumns.DISPLAY_NAME)

                                val pathData = cursor.getString(columnData)
                                val isDownloadData = isDownloadData(pathData)
                                if (isDownloadData) {
                                    val displayName = cursor.getStringOrNull(columnName)
                                    val pdfUri = File(pathData).toUri()
                                    val pdfName = getPdfDisplayName(displayName, pathData)

                                    pdf = if (isFileFavourite(id)) {
                                        PdfFile(id = id, pdfName = pdfName, uri = pdfUri, isFavourite = true)
                                    } else {
                                        PdfFile(id = id, pdfName = pdfName, uri = pdfUri, isFavourite = false)
                                    }

                                    Resource.Success(pdf!!)
                                }
                            }
                        }
                    }
                Resource.Success(pdf)
            } catch (exception: Exception) {
                Resource.Error(exception)
            }
        }
    }

    private suspend fun addPdfFileToList(
        cursor: Cursor,
        columnName: Int,
        columnId: Int,
        pathData: String,
        pdfList: MutableList<PdfFile>
    ) {
        val displayName = cursor.getStringOrNull(columnName)
        val fileId = cursor.getLong(columnId)
        val pdfUri = File(pathData).toUri()
        val pdfName = getPdfDisplayName(displayName, pathData)

        val pdfFile = if (isFileFavourite(fileId)) {
            PdfFile(id = fileId, pdfName = pdfName, uri = pdfUri, isFavourite = true)
        } else {
            PdfFile(id = fileId, pdfName = pdfName, uri = pdfUri, isFavourite = false)
        }

        pdfList += pdfFile
    }

    /*
    * Just like getting all the items above, we make use of coroutines to run the filter operation. For extra safety,
    * a try catch is used just in case something goes wrong although it is unlikely
    * */
    override suspend fun getPdfListBasedOnQuery(searchTerm: String): Resource<List<PdfFile>> {
        return withContext(dispatcher) {
            try {
                val pdfFiles = getPdfListFromFile()
                val filteredList = pdfFiles.filter { it.pdfName.contains(searchTerm, true) }

                if (filteredList.isEmpty()) {
                    Resource.Error(EmptyListException())
                } else {
                    Resource.Success(filteredList)
                }
            } catch (exception: Exception) {
                Resource.Error(exception)
            }
        }
    }

    override suspend fun addOrRemoveFileFromFav(pdfFile: DbFavoritePdfFile) {
        val dbFile = database.pdfDao().getFileById(pdfFile.id)

        if (dbFile != null) {
            // This is a favourite, remove it from the favourites
            database.pdfDao().removeFromFav(pdfFile)
        } else {
            // Otherwise add it to the db
            database.pdfDao().addToFav(pdfFile)
        }
    }

    private suspend fun isFileFavourite(id: Long): Boolean {
        val dbFile = database.pdfDao().getFileById(id)

        return dbFile != null
    }

    /*
    * Some older versions of android may return a null display name and even perhaps newer versions
    * as well, which is why this extra check is so important. The path data has the name of the file as well
    * */
    private fun getPdfDisplayName(displayName: String?, pathData: String): String {
        return displayName ?: pathData.substringAfterLast(DELIMITER_FORWARD_SLASH_CHAR)
    }

    /*
    * This is an extra layer when getting the files to ensure that only those whose path without the file name
    * ends with the word download, ignoring the case, end up in the list. This works because a user can never name a file
    * to include the forward slash as it is illegal therefore the string after the last forward slash will always be the file name
    * Ignore the case just in case the user has renamed the download folder.
    *
    * The method checks that the path contains the word download just in case there are folders inside the download
    * folder itself so this makes the app a bit more robust
    *
    * */
    private fun isDownloadData(data: String?): Boolean {
        return if (data != null) {
            val pathStringWithoutFileName = data.substringBeforeLast(DELIMITER_FORWARD_SLASH_CHAR)
            pathStringWithoutFileName.contains(SUFFIX_DOWNLOAD, ignoreCase = true)
        } else {
            false
        }
    }

    companion object {
        private const val DELIMITER_FORWARD_SLASH_CHAR = '/'
        private const val SUFFIX_DOWNLOAD = "download"
        const val VOLUME_EXTERNAL = "external"
        private const val EXTENSION_PDF = "pdf"
    }
}