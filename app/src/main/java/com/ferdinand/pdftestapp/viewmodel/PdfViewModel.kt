package com.ferdinand.pdftestapp.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ferdinand.pdftestapp.models.PdfEvent
import com.ferdinand.pdftestapp.models.PdfFile
import com.ferdinand.pdftestapp.models.state.PdfQueryState
import com.ferdinand.pdftestapp.repo.PdfRepo
import com.ferdinand.pdftestapp.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
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

    val query = mutableStateOf("")

    val arePermissionsGranted = mutableStateOf(false)

    init {
        getAllPdfFiles()
    }

    fun getAllPdfFiles() {
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
                        data = pdfResource.data
                    )
                }
            }
        }
    }

    fun handleEvent(event: PdfEvent) {
        when (event) {
            PdfEvent.ErrorDismissed -> {
                dismissError()
            }
            is PdfEvent.OnFavouriteEvent -> {
                favouritePdf(event.pdfFile)
            }
            is PdfEvent.SearchEvent -> {
                searchFiles(event.searchTerm)
            }
        }
    }

    private fun favouritePdf(pdfFile: PdfFile) {
        TODO("Not yet implemented")
    }

    private fun searchFiles(searchTerm: String) {
        mutableFilteredPdfState.value = mutableFilteredPdfState.value.copy(isLoading = true, error = null, data = null)
        viewModelScope.launch {
            val pdfFiles = pdfQueryState.value.data

            when (val pdfResource = pdfRepo.getPdfListBasedOnQuery(pdfFiles, searchTerm)) {
                is Resource.Error -> {
                    mutableFilteredPdfState.value = mutableFilteredPdfState.value.copy(
                        isLoading = false,
                        error = pdfResource.error
                    )
                }
                is Resource.Success -> {
                    mutableFilteredPdfState.value = mutableFilteredPdfState.value.copy(
                        isLoading = false,
                        data = pdfResource.data
                    )
                }
            }
        }
    }

    fun onQueryChanged(query: String) {
        this.query.value = query
    }

    fun onPermissionsStateChanged(value: Boolean) {
        this.arePermissionsGranted.value = value
    }

    private fun dismissError() {
        mutablePdfQueryState.value = mutablePdfQueryState.value.copy(
            error = null
        )

        mutableFilteredPdfState.value = mutableFilteredPdfState.value.copy(
            error = null
        )
    }
}