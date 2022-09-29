package com.ferdinand.pdftestapp.repo

import android.content.Context
import android.os.Build
import android.provider.MediaStore
import android.webkit.MimeTypeMap
import androidx.core.net.toUri
import com.ferdinand.pdftestapp.models.PdfFile
import com.ferdinand.pdftestapp.utils.Resource
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import javax.inject.Inject

class PdfRepoImpl @Inject constructor(
    private val context: Context,
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
                val pdfList = mutableListOf<PdfFile>()

                context.contentResolver.query(collection, projection, selection, selectionArgs, sortOrder).use { cursor ->
                    cursor?.let {
                        if (it.moveToFirst()) {

                            val columnData = cursor.getColumnIndex(MediaStore.Files.FileColumns.DATA)
                            val columnName = cursor.getColumnIndex(MediaStore.Files.FileColumns.DISPLAY_NAME)

                            while (it.moveToNext()) {
                                val pathData = cursor.getString(columnData)
                                val isDownloadData = isDownloadData(pathData)
                                if (isDownloadData) {
                                    val pdfName = cursor.getString(columnName)
                                    val pdfUri = File(pathData).toUri()
                                    pdfList += PdfFile(pdfName = pdfName, uri = pdfUri)
                                }
                            }
                        }
                    }
                }

                Resource.Success(pdfList)
            } catch (exception: Exception) {
                Resource.Error(exception)
            }
        }
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
        private const val VOLUME_EXTERNAL = "external"
        private const val EXTENSION_PDF = "pdf"
    }
}