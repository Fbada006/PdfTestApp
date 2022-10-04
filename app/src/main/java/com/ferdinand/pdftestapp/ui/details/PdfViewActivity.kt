package com.ferdinand.pdftestapp.ui.details

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
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
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Output
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.navArgs
import com.ferdinand.pdftestapp.R
import com.ferdinand.pdftestapp.models.PdfEvent
import com.ferdinand.pdftestapp.ui.composables.LikeToggleButton
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

        setContent {
            val pdfFile = args.pdfFile

            Scaffold(
                topBar = {
                    TopAppBar(
                        title = {
                            Text(
                                text = pdfFile.pdfName,
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
                            LikeToggleButton(initialCheckedValue = args.pdfFile.isFavourite, onFavorite = {
                                viewModel.handleEvent(PdfEvent.OnFavouriteEvent(pdfFile, null))
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

                val documentUri = remember { args.pdfFile.uri }

                val pdfActivityConfiguration = PdfActivityConfiguration
                    .Builder(this)
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
