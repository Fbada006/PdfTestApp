package com.ferdinand.pdftestapp.ui.details

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Output
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.navArgs
import com.ferdinand.pdftestapp.R
import com.ferdinand.pdftestapp.models.PdfEvent
import com.ferdinand.pdftestapp.ui.composables.LikeToggleButton
import com.ferdinand.pdftestapp.ui.composables.ErrorDialog
import com.ferdinand.pdftestapp.ui.theme.PdfTestAppTheme
import com.ferdinand.pdftestapp.viewmodel.PdfViewModel
import com.pspdfkit.configuration.activity.PdfActivityConfiguration
import com.pspdfkit.configuration.page.PageScrollDirection
import com.pspdfkit.jetpack.compose.DocumentView
import com.pspdfkit.jetpack.compose.ExperimentalPSPDFKitApi
import com.pspdfkit.jetpack.compose.rememberDocumentState
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

@ExperimentalPSPDFKitApi
@AndroidEntryPoint
class PdfViewActivity : AppCompatActivity() {

    private val args by navArgs<PdfViewActivityArgs>()
    private val viewModel by viewModels<PdfViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.handleEvent(PdfEvent.DisplayFileDetailsEvent(args.fileId))

        setContent {
            val pdfQueryState by viewModel.singlePdfState.collectAsState()
            val pdfFile = pdfQueryState.singlePdfData

            PdfTestAppTheme {
                Scaffold(
                    topBar = {
                        TopAppBar(
                            title = {
                                Text(
                                    text = pdfFile?.pdfName ?: stringResource(id = R.string.app_name),
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            },
                            navigationIcon = {
                                IconButton(onClick = {
                                    finish()
                                }) {
                                    Icon(Icons.Rounded.ArrowBack, stringResource(id = R.string.cd_back_button))
                                }
                            },
                            actions = {
                                LikeToggleButton(initialCheckedValue = pdfFile?.isFavourite ?: false, onFavorite = {
                                    viewModel.handleEvent(PdfEvent.OnFavouriteEvent(pdfFile))
                                })
                            }
                        )
                    },
                    floatingActionButton = {
                        FloatingActionButton(onClick = { /*do something*/ }) {
                            Icon(Icons.Rounded.Output, stringResource(id = R.string.cd_back_button))
                        }
                    }
                ) {

                    Box(
                        modifier = Modifier
                            .padding(it)
                            .fillMaxSize()
                    ) {
                        if (pdfQueryState.isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.align(Alignment.Center)
                            )
                        } else {
                            pdfFile?.let { file ->
                                val documentUri = remember { file.uri }

                                val pdfActivityConfiguration = remember {
                                    PdfActivityConfiguration
                                        .Builder(this@PdfViewActivity)
                                        .scrollDirection(PageScrollDirection.VERTICAL)
                                        .build()
                                }

                                val documentState = rememberDocumentState(documentUri, pdfActivityConfiguration)
                                val currentPage by remember(documentState.currentPage) { mutableStateOf(documentState.currentPage) }

                                DocumentView(
                                    documentState = documentState,
                                    modifier = Modifier
                                        .padding(4.dp)
                                )

                                LaunchedEffect(currentPage) {
                                    documentState.scrollToPage(currentPage)
                                }

                                Timber.d("Current page is $currentPage")
                            }

                            pdfQueryState.error?.let {
                                ErrorDialog(
                                    error = stringResource(id = R.string.something_went_wrong),
                                    dismissError = {
                                        viewModel.handleEvent(PdfEvent.ErrorDismissedEvent)
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

