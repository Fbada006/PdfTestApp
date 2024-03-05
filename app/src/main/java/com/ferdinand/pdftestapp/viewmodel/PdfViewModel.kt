package com.ferdinand.pdftestapp.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ferdinand.pdftestapp.mappers.toDataModel
import com.ferdinand.pdftestapp.mappers.toDbModel
import com.ferdinand.pdftestapp.mappers.toPresentationModel
import com.ferdinand.pdftestapp.models.PdfDestination
import com.ferdinand.pdftestapp.models.PdfEvent
import com.ferdinand.pdftestapp.models.PdfPresentationFile
import com.ferdinand.pdftestapp.models.state.PdfQueryState
import com.ferdinand.pdftestapp.repo.PdfRepo
import com.ferdinand.pdftestapp.utils.FileNotFoundException
import com.ferdinand.pdftestapp.utils.Resource
import com.pspdfkit.document.processor.PdfProcessor
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.rxjava3.core.Flowable
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

/**
* This class is responsible for storing and managing UI related data in a lifecycle-conscious way. It is the middle man between the data and
 * the UI layer.
 *
 * @see <a href="https://developer.android.com/topic/libraries/architecture/viewmodel">ViewModel overview </a>
* */
@HiltViewModel
class PdfViewModel @Inject constructor(private val pdfRepo: PdfRepo) : ViewModel() {

    private val mutablePdfQueryState = MutableStateFlow(PdfQueryState())
    val pdfQueryState = mutablePdfQueryState.asStateFlow()

    private val mutableFilteredPdfState = MutableStateFlow(PdfQueryState())
    val filteredPdfState = mutableFilteredPdfState.asStateFlow()

    private val mutableSinglePdfState = MutableStateFlow(PdfQueryState())
    val singlePdfState = mutableSinglePdfState.asStateFlow()

    private val currentPage = mutableStateOf(0)
    private val destination = mutableStateOf<PdfDestination>(PdfDestination.MainScreen)
    val query = mutableStateOf("")
    val areReadPermissionsGranted = mutableStateOf(false)
    val areWritePermissionsGranted = mutableStateOf(false)
    lateinit var exportPageFlowable: Flowable<PdfProcessor.ProcessorProgress>

    init {
        // Immediately the app is launched, we query the file system to show all the files the user has
        getAllPdfFiles()
    }

    /*
    * Handle the different UI events accordingly. Using a sealed class is beneficial because they are faster than enums
    * */
    fun handleEvent(event: PdfEvent) {
        when (event) {
            PdfEvent.GetAllFilesEvent -> {
                getAllPdfFiles()
            }
            PdfEvent.ErrorDismissedEvent -> {
                dismissError()
            }
            PdfEvent.SearchEvent -> {
                searchFiles()
            }
            PdfEvent.ExportCurrentPageEvent -> {
                exportCurrentPageToPdf()
            }
            is PdfEvent.OnFavouriteEvent -> {
                addOrRemoveFileFromFavoritesAndRefreshList(event.pdfFile)
            }
            is PdfEvent.DisplayFileDetailsEvent -> {
                displayFileDetailsBasedOnId(event.fileId)
            }
        }
    }

    /*
    * Query the file system to get all the files. The viewModelScope is used to launch the coroutine because it will be
    * automatically cleared once this viewModel is destroyed
    * */
    private fun getAllPdfFiles() {
        mutablePdfQueryState.value = mutablePdfQueryState.value.copy(isLoading = true, error = null)
        viewModelScope.launch {
            when (val pdfResource = pdfRepo.getPdfList()) {
                is Resource.Error -> {
                    mutablePdfQueryState.value = mutablePdfQueryState.value.copy(
                        isLoading = false,
                        error = pdfResource.error
                    )
                }
                is Resource.Success -> {
                    mutablePdfQueryState.value = mutablePdfQueryState.value.copy(
                        isLoading = false,
                        listData = pdfResource.data.map {
                            it.toPresentationModel()
                        }.sortedByDescending { it.isFavourite }
                    )
                }
            }
        }
    }

    /*
    * Save the current page as a single page pdf file. The viewModelScope is used to launch the coroutine because it will be
    * automatically cleared once this viewModel is destroyed
    * */
    private fun exportCurrentPageToPdf() {
        val pdfFile = singlePdfState.value.singlePdfData?.toDataModel()
        pdfFile?.let {
            exportPageFlowable = pdfRepo.exportCurrentPageToPdf(it, currentPage.value)
        }
    }

    /*
    * Get the file based on the id. The viewModelScope is used to launch the coroutine because it will be
    * automatically cleared once this viewModel is destroyed
    * */
    private fun displayFileDetailsBasedOnId(fileId: String) {
        mutableSinglePdfState.value = mutableSinglePdfState.value.copy(isLoading = true, error = null)
        viewModelScope.launch {
            when (val pdfResource = pdfRepo.getPdfFileBasedOnId(fileId)) {
                is Resource.Error -> {
                    mutableSinglePdfState.value = mutableSinglePdfState.value.copy(
                        isLoading = false,
                        error = pdfResource.error
                    )
                }
                is Resource.Success -> {
                    if (pdfResource.data != null) {
                        mutableSinglePdfState.value = mutableSinglePdfState.value.copy(
                            isLoading = false,
                            singlePdfData = pdfResource.data.toPresentationModel()
                        )
                    } else {
                        mutableSinglePdfState.value = mutableSinglePdfState.value.copy(
                            isLoading = false,
                            error = FileNotFoundException()
                        )
                    }
                }
            }
        }
    }

    /*
    * Add or remove a file from db and update the list for the UI. The viewModelScope is used to launch the coroutine because it will be
    * automatically cleared once this viewModel is destroyed
    * */
    private fun addOrRemoveFileFromFavoritesAndRefreshList(pdfFile: PdfPresentationFile?) {
        viewModelScope.launch {
            pdfFile?.let {
                try {
                    pdfRepo.addOrRemoveFileFromFav(it.toDataModel().toDbModel())
                    // At this point, the item has been saved to the fav db so the UI should be updated
                    when (destination.value) {
                        is PdfDestination.MainScreen -> {
                            val files = pdfQueryState.value.listData?.toMutableList()
                            refreshList(files, pdfFile, mutablePdfQueryState)
                        }
                        is PdfDestination.SearchScreen -> {
                            val files = filteredPdfState.value.listData?.toMutableList()
                            refreshList(files, pdfFile, mutableFilteredPdfState)
                        }
                    }
                } catch (exception: Exception) {
                    // We do not care about the error except to log it and the user will not get an updated UI
                    Timber.e("Error saving favourite $exception")
                }
            }
        }
    }

    /**
     * This is a helper function to quickly modify the list and update the UI. Since it runs inside the try block of the
     * method to add to favorites, we can know for sure that the file has already been saved without an exception to the db when this runs.
     * Instead of querying the entire list, which makes for a bad jumping UI, we can go ahead and update the isFavourite property of the item
     * added to favorites here, which creates a better animation effect on the UI.
     *
     * @param files is the list displayed on screen
     * @param pdfFile is the file clicked on the UI
     * @param stateFlow is the current list to be updated depending on the search or main screen
     * */
    private fun refreshList(
        files: MutableList<PdfPresentationFile>?,
        pdfFile: PdfPresentationFile,
        stateFlow: MutableStateFlow<PdfQueryState>
    ) {
        val index = files?.indexOf(pdfFile)

        index?.let { idx ->
            files[idx] = pdfFile.copy(isFavourite = !pdfFile.isFavourite)

            stateFlow.value = PdfQueryState(
                isLoading = false,
                listData = files.sortedByDescending { file -> file.isFavourite }
            )
        }
    }

    /*
    * Conduct a search based on the query by the user. The viewModelScope is used to launch the coroutine because it will be
    * automatically cleared once this viewModel is destroyed
    * */
    private fun searchFiles() {
        mutableFilteredPdfState.value = mutableFilteredPdfState.value.copy(isLoading = true, error = null, listData = null)
        viewModelScope.launch {

            when (val pdfResource = pdfRepo.getPdfListBasedOnQuery(query.value)) {
                is Resource.Error -> {
                    mutableFilteredPdfState.value = mutableFilteredPdfState.value.copy(
                        isLoading = false,
                        error = pdfResource.error
                    )
                }
                is Resource.Success -> {
                    mutableFilteredPdfState.value = mutableFilteredPdfState.value.copy(
                        isLoading = false,
                        listData = pdfResource.data
                            .map {
                                it.toPresentationModel()
                            }.sortedByDescending { it.isFavourite }
                    )
                }
            }
        }
    }

    /*
    * Update the user's query
    * */
    fun onQueryChanged(newQuery: String) {
        this.query.value = newQuery
    }

    /*
    * Keep track of the read permission state
    * */
    fun onReadPermissionsStateChanged(newValue: Boolean) {
        this.areReadPermissionsGranted.value = newValue
    }

    /*
    * Keep track of the write permission state
    * */
    fun onWritePermissionsStateChanged(newValue: Boolean) {
        this.areWritePermissionsGranted.value = newValue
    }

    /*
    * Keep track of the current page the user is viewing
    * */
    fun onCurrentPageChanged(currentPage: Int) {
        this.currentPage.value = currentPage
    }

    /*
    * Keep track of the current screen
    * */
    fun onDestinationChanged(destination: PdfDestination) {
        this.destination.value = destination
    }

    /*
    * Set all error properties in all the states as null to dismiss a dialog
    * */
    private fun dismissError() {
        mutablePdfQueryState.value = mutablePdfQueryState.value.copy(
            error = null
        )

        mutableFilteredPdfState.value = mutableFilteredPdfState.value.copy(
            error = null
        )

        mutableSinglePdfState.value = mutableSinglePdfState.value.copy(
            error = null
        )
    }
}