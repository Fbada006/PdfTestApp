package com.ferdinand.pdftestapp.ui.details

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Output
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import com.ferdinand.pdftestapp.R
import com.pspdfkit.configuration.activity.PdfActivityConfiguration
import com.pspdfkit.configuration.page.PageScrollDirection
import com.pspdfkit.jetpack.compose.DocumentView
import com.pspdfkit.jetpack.compose.ExperimentalPSPDFKitApi
import com.pspdfkit.jetpack.compose.rememberDocumentState
import timber.log.Timber

@ExperimentalPSPDFKitApi
class PdfViewFragment : Fragment() {

    private val args by navArgs<PdfViewFragmentArgs>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return ComposeView(requireContext()).apply {

            val pdfFile = args.pdfFile

            setContent {
                Scaffold(
                    topBar = {
                        TopAppBar(
                            title = {
                                Text(
                                    text = args.pdfFile.pdfName,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            },
                            navigationIcon = {
                                IconButton(onClick = {
                                    findNavController().navigateUp()
                                }) {
                                    Icon(Icons.Rounded.ArrowBack, stringResource(id = R.string.cd_back_button))
                                }
                            },
                            actions = {
                                IconButton(onClick = {
                                    TODO()
                                }) {
                                    Icon(
                                        if (pdfFile.isFavourite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                        stringResource(id = R.string.favourite)
                                    )
                                }
                            }
                        )
                    },
                    floatingActionButton = {
                        FloatingActionButton(onClick = { /*do something*/ }) {
                            Icon(Icons.Rounded.Output, stringResource(id = R.string.cd_back_button))
                        }
                    }
                ) {

                    val documentUri = remember { args.pdfFile.uri }

                    val pdfActivityConfiguration = PdfActivityConfiguration
                        .Builder(requireContext())
                        .scrollDirection(PageScrollDirection.VERTICAL)
                        .build()

                    val documentState = rememberDocumentState(documentUri, pdfActivityConfiguration)
                    val currentPage by remember(documentState.currentPage) { mutableStateOf(documentState.currentPage) }

                    Box(
                        modifier = Modifier
                            .padding(it)
                            .fillMaxSize()
                    ) {
                        DocumentView(
                            documentState = documentState,
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(4.dp)
                        )
                    }

                    LaunchedEffect(currentPage) {
                        documentState.scrollToPage(currentPage)
                    }

                    Timber.d("Current page is $currentPage")
                }
            }
        }
    }
}