package com.ferdinand.pdftestapp.repo

import android.content.Context
import android.os.Environment
import androidx.core.net.toUri
import com.ferdinand.pdftestapp.data.PdfDatabase
import com.ferdinand.pdftestapp.data.models.DbFavoritePdfFile
import com.ferdinand.pdftestapp.data.models.PdfFile
import com.ferdinand.pdftestapp.utils.EmptyListException
import com.ferdinand.pdftestapp.utils.Resource
import com.pspdfkit.document.PdfDocumentLoader
import com.pspdfkit.document.processor.PdfProcessor
import com.pspdfkit.document.processor.PdfProcessorTask
import io.reactivex.Flowable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
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
        val dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
        val fileList = dir.listFiles()

        fileList?.let {
            fileList.forEach { file ->
                if (file.isDirectory) {
                    getPdfListFromFile()
                } else {
                    if (file.name.endsWith(PATH_PDF) && isDownloadData(file.path)) {
                        val canonicalPath = file.canonicalPath
                        val pdf = PdfFile(
                            id = canonicalPath,
                            pdfName = file.name,
                            uri = file.toUri(),
                            isFavourite = isFileFavourite(canonicalPath)
                        )
                        pdfList.add(pdf)
                    }
                }
            }
        }

        return pdfList
    }

    override suspend fun getPdfFileBasedOnId(id: String): Resource<PdfFile?> {
        return withContext(dispatcher) {
            try {
                val pdfFiles = getPdfListFromFile()
                val file = pdfFiles.singleOrNull { it.id == id }

                Resource.Success(file)
            } catch (exception: Exception) {
                Resource.Error(exception)
            }
        }
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

    override fun exportCurrentPageToPdf(pdfFile: PdfFile, currentPage: Int): Flowable<PdfProcessor.ProcessorProgress> {
        val fileName = "${pdfFile.pdfName.replace(".pdf", "")} page ${currentPage.plus(1)}.$EXTENSION_PDF"
        val document = PdfDocumentLoader.openDocument(context, pdfFile.uri)
        val processorTask = PdfProcessorTask.fromDocument(document).keepPages(setOf(currentPage))

        val outputFile = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), fileName)

        return PdfProcessor.processDocumentAsync(processorTask, outputFile)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }

    private suspend fun isFileFavourite(id: String): Boolean {
        val dbFile = database.pdfDao().getFileById(id)

        return dbFile != null
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
        private const val EXTENSION_PDF = "pdf"
        private const val PATH_PDF = ".pdf"
    }
}