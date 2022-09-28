package com.ferdinand.pdftestapp.viewmodel

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class PdfViewModel @Inject constructor() : ViewModel() {

    val placeholder = MutableStateFlow("Hello Android").asStateFlow()
}