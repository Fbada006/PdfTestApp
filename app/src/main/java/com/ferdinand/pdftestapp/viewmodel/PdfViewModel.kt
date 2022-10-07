package com.ferdinand.pdftestapp.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ferdinand.pdftestapp.mappers.toDataModel
import com.ferdinand.pdftestapp.mappers.toDbModel
import com.ferdinand.pdftestapp.mappers.toPresentationModel
import com.ferdinand.pdftestapp.models.PdfEvent
import com.ferdinand.pdftestapp.models.PdfPresentationFile
import com.ferdinand.pdftestapp.models.state.PdfQueryState
import com.ferdinand.pdftestapp.repo.PdfRepo
import com.ferdinand.pdftestapp.utils.FileNotFoundException
import com.ferdinand.pdftestapp.utils.Resource
import com.pspdfkit.document.processor.PdfProcessor
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.Flowable
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PdfViewModel @Inject constructor(private val pdfRepo: PdfRepo) : ViewModel() {

    private val mutablePdfQueryState = MutableStateFlow(PdfQueryState())
    val pdfQueryState = mutablePdfQueryState.asStateFlow()

    private val mutableFilteredPdfState = MutableStateFlow(PdfQueryState())
    val filteredPdfState = mutableFilteredPdfState.asStateFlow()

    private val mutableSinglePdfState = MutableStateFlow(PdfQueryState())
    val singlePdfState = mutableSinglePdfState.asStateFlow()

    private val currentPage = mutableStateOf(0)
    val query = mutableStateOf("")
    val areReadPermissionsGranted = mutableStateOf(false)
    val areWritePermissionsGranted = mutableStateOf(false)
    lateinit var exportPageFlowable: Flowable<PdfProcessor.ProcessorProgress>

    init {
        getAllPdfFiles()
    }

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
                addOrRemoveFileFromFavorites(event.pdfFile)
            }
            is PdfEvent.DisplayFileDetailsEvent -> {
                displayFileDetailsBasedOnId(event.fileId)
            }
        }
    }

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

    private fun exportCurrentPageToPdf() {
        val pdfFile = singlePdfState.value.singlePdfData?.toDataModel()
        pdfFile?.let {
            exportPageFlowable = pdfRepo.exportCurrentPageToPdf(it, currentPage.value)
        }
    }

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

    private fun addOrRemoveFileFromFavorites(pdfFile: PdfPresentationFile?) {
        viewModelScope.launch {
            pdfFile?.let {
                pdfRepo.addOrRemoveFileFromFav(it.toDataModel().toDbModel())
            }
        }
    }

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

    fun onQueryChanged(newQuery: String) {
        this.query.value = newQuery
    }

    fun onReadPermissionsStateChanged(newValue: Boolean) {
        this.areReadPermissionsGranted.value = newValue
    }

    fun onWritePermissionsStateChanged(newValue: Boolean) {
        this.areWritePermissionsGranted.value = newValue
    }

    fun onCurrentPageChanged(currentPage: Int) {
        this.currentPage.value = currentPage
    }

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