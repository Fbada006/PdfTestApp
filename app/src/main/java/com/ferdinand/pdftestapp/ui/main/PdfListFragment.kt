package com.ferdinand.pdftestapp.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Scaffold
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.ferdinand.pdftestapp.ui.theme.PdfTestAppTheme
import com.ferdinand.pdftestapp.viewmodel.PdfViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
@ExperimentalComposeUiApi
class PdfListFragment : Fragment() {

    private val viewModel: PdfViewModel by viewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return ComposeView(requireContext()).apply {
            setContent {
                val pdfQueryState by viewModel.pdfQueryState.collectAsState()
                val query = viewModel.query.value

                PdfTestAppTheme {
                    // A surface container using the 'background' color from the theme
                    Scaffold(
                        topBar = {
                            SearchAppBar(query = query,
                                onQueryChanged = viewModel::onQueryChanged,
                                handleEvent = { event -> viewModel.handleEvent(event) })
                        }
                    ) {
                        Box(
                            modifier = Modifier
                                .padding(it)
                        )
                        PdfList(
                            pdfQueryState = pdfQueryState,
                            onPdfClick = {
                                // Trigger Navigation
                            },
                            handleEvent = { event ->
                                viewModel.handleEvent(event)
                            },
                            modifier = Modifier
                                .padding(4.dp)
                                .fillMaxSize()
                        )
                    }
                }
            }
        }
    }
}