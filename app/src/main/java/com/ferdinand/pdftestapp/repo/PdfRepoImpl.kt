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

    private val downloadDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)

    /**
     * This function gets all the files in the download directory
     * Since this method involves querying the file system, it should never run on the main thread. For extra safety,
     * a try catch is used just in case something goes wrong although it is unlikely
     *
     * @return a [com.ferdinand.pdftestapp.utils.Resource] wrapper with the outcome of the operation
     * */
    override suspend fun getPdfList(): Resource<List<PdfFile>> {
        return withContext(dispatcher) {
            try {
                val pdfList = getPdfListFromFile(downloadDirectory)

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

    /**
     * This is the main logic for getting the list from the device. This should never run on the main thread as it may take time especially
     * if the user has many documents and plenty of sub folders inside the download folder. Since it uses recursion, we can be sure that we
     * will get all the files in the Download folder as long as the permissions are granted.
     *
     * Note that the canonical path of the file is used as the id of the file and subsequently as the primary key of
     * [com.ferdinand.pdftestapp.data.models.DbFavoritePdfFile]. This is made possible because the canonical pathname of a file
     * is both absolute and unique as explained in the docs, which makes it suitable for use as an id
     * @see <a href="https://developer.android.com/reference/java/io/File#getCanonicalPath()">File Documentation</a>
     *
     * @param dir is the current directory to query, which is the download one to start with
     *
     * @return the list of all the pdf files in the download directory
     * */
    private suspend fun getPdfListFromFile(dir: File): List<PdfFile> {
        val pdfList = mutableListOf<PdfFile>()
        val fileList = dir.listFiles()

        fileList?.let {
            fileList.forEach { file ->
                if (file.isDirectory) {
                    pdfList.addAll(getPdfListFromFile(file))
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

    /**
     * This function filters the files based on the id of the file, which is actually the canonical path.
     * Since this method involves querying the file system, it should never run on the main thread. For extra safety,
     * a try catch is used just in case something goes wrong although it is unlikely
     *
     * @return a [com.ferdinand.pdftestapp.utils.Resource] wrapper with the outcome of the operation
     * */
    override suspend fun getPdfFileBasedOnId(id: String): Resource<PdfFile?> {
        return withContext(dispatcher) {
            try {
                val pdfFiles = getPdfListFromFile(downloadDirectory)
                val file = pdfFiles.singleOrNull { it.id == id }

                Resource.Success(file)
            } catch (exception: Exception) {
                Resource.Error(exception)
            }
        }
    }

    /**
     * This function filters the files based on the search term typed by the user.
     * Since this method involves querying the file system, it should never run on the main thread. For extra safety,
     * a try catch is used just in case something goes wrong although it is unlikely
     *
     * @return a [com.ferdinand.pdftestapp.utils.Resource] wrapper with the outcome of the operation
     * */
    override suspend fun getPdfListBasedOnQuery(searchTerm: String): Resource<List<PdfFile>> {
        return withContext(dispatcher) {
            try {
                val pdfFiles = getPdfListFromFile(downloadDirectory)
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

    /**
     * This function checks if the pdf file clicked on is already in the favorites db. If it is, then remove it otherwise
     * proceed with adding it to the favorites. This should never run on the main thread hence the use of coroutines
     *
     * @param pdfFile is the item the user clicked on
     * */
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


    /**
     * This function exports the current page as a single page pdf with the naming pattern being “[original-document-name] page x.pdf”
     * This is a method that runs off the main thread as designed in the PSPDFKIT library so there is no need to wrap it around a
     * coroutine
     *
     * @param pdfFile is the file that is currently opened whose page needs exporting
     * @param currentPage is the current page the user is viewing of the open file
     *
     * @return a flowable with the progress and outcome of the operation
     * */
    override fun exportCurrentPageToPdf(pdfFile: PdfFile, currentPage: Int): Flowable<PdfProcessor.ProcessorProgress> {
        val fileName = "${pdfFile.pdfName.replace(".pdf", "")} page ${currentPage.plus(1)}.$EXTENSION_PDF"
        val document = PdfDocumentLoader.openDocument(context, pdfFile.uri)
        val processorTask = PdfProcessorTask.fromDocument(document).keepPages(setOf(currentPage))

        val outputFile = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), fileName)

        return PdfProcessor.processDocumentAsync(processorTask, outputFile)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }

    /**
     * This is a helper to check if the id of the file exists in the db or not.
     *
     * @param id is the id of the file
     * @return true if file is not null, otherwise false
     * */
    private suspend fun isFileFavourite(id: String): Boolean {
        val dbFile = database.pdfDao().getFileById(id)

        return dbFile != null
    }

    /*
    * This function is an extra layer when getting the files to ensure that only those whose path without the file name
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