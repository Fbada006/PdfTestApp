package com.ferdinand.pdftestapp.ui.search

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Scaffold
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.res.dimensionResource
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import com.ferdinand.pdftestapp.R
import com.ferdinand.pdftestapp.models.PdfDestination
import com.ferdinand.pdftestapp.models.PdfEvent
import com.ferdinand.pdftestapp.ui.composables.PdfList
import com.ferdinand.pdftestapp.ui.composables.SearchAppBar
import com.ferdinand.pdftestapp.ui.theme.PdfTestAppTheme
import com.ferdinand.pdftestapp.viewmodel.PdfViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
@ExperimentalComposeUiApi
@ExperimentalFoundationApi
class SearchFragment : Fragment() {

    private val viewModel: PdfViewModel by viewModels()
    private var didUserNavigateToDetails = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.onDestinationChanged(PdfDestination.SearchScreen)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return ComposeView(requireContext()).apply {
            setContent {
                PdfTestAppTheme {
                    val pdfQueryState by viewModel.filteredPdfState.collectAsState()
                    val query = viewModel.query.value

                    Scaffold(
                        topBar = {
                            SearchAppBar(query = query,
                                onQueryChanged = viewModel::onQueryChanged,
                                onBackClicked = {
                                    findNavController().navigateUp()
                                },
                                handleEvent = { event -> viewModel.handleEvent(event) })
                        }
                    ) {
                        Box(
                            modifier = Modifier
                                .padding(it)
                                .fillMaxSize()
                        ) {
                            PdfList(
                                pdfQueryState = pdfQueryState,
                                onPdfClick = { pdfFile ->
                                    didUserNavigateToDetails = true
                                    findNavController().navigate(
                                        SearchFragmentDirections.actionSearchFragmentToPdfViewActivity(pdfFile.id)
                                    )
                                },
                                handleEvent = { event ->
                                    viewModel.handleEvent(event)
                                },
                                modifier = Modifier
                                    .padding(dimensionResource(id = R.dimen.size_4))
                                    .fillMaxSize()
                            )
                        }
                    }
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        if (didUserNavigateToDetails) {
            viewModel.handleEvent(PdfEvent.SearchEvent)
            didUserNavigateToDetails = false
        }
    }
}
