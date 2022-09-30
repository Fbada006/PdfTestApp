package com.ferdinand.pdftestapp.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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

    val query = mutableStateOf("")

    init {
        getAllPdfFiles()
    }

    private fun getAllPdfFiles() {
        mutablePdfQueryState.value = mutablePdfQueryState.value.copy(isLoading = true)
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

    fun onExecuteSearch() {
        TODO("Not yet implemented")
    }

    fun onQueryChanged(query: String) {
        this.query.value = query
    }

    fun onFavouriteClicked(pdf: PdfFile) {
        TODO("Not yet implemented")
    }
}