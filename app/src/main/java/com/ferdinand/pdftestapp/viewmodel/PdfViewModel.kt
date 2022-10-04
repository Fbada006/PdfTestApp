package com.ferdinand.pdftestapp.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ferdinand.pdftestapp.mappers.toDbModel
import com.ferdinand.pdftestapp.models.PdfDestination
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
                        data = pdfResource.data.sortedByDescending { it.isFavourite }
                    )
                }
            }
        }
    }

    fun handleEvent(event: PdfEvent) {
        when (event) {
            PdfEvent.GetAllFiles -> {
                getAllPdfFiles()
            }
            PdfEvent.ErrorDismissed -> {
                dismissError()
            }
            is PdfEvent.OnFavouriteEvent -> {
                addOrRemoveFileFromFavorites(event.pdfFile, event.destination)
            }
            is PdfEvent.SearchEvent -> {
                searchFiles()
            }
        }
    }

    private fun addOrRemoveFileFromFavorites(pdfFile: PdfFile, destination: PdfDestination?) {
        viewModelScope.launch {
            pdfRepo.addOrRemoveFileFromFav(pdfFile.toDbModel())
            // Only refresh the necessary list that has been refreshed depending on the current screen
            destination?.let {
                when (destination) {
                    PdfDestination.MainScreen -> {
                        getAllPdfFiles()
                    }
                    PdfDestination.SearchScreen -> {
                        searchFiles()
                    }
                }
            }
        }
    }

    private fun searchFiles() {
        mutableFilteredPdfState.value = mutableFilteredPdfState.value.copy(isLoading = true, error = null, data = null)
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
                        data = pdfResource.data.sortedByDescending { it.isFavourite }
                    )
                }
            }
        }
    }

    fun onQueryChanged(newQuery: String) {
        this.query.value = newQuery
    }

    fun onPermissionsStateChanged(newValue: Boolean) {
        this.arePermissionsGranted.value = newValue
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