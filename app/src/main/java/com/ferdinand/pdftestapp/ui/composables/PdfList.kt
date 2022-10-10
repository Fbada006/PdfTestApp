package com.ferdinand.pdftestapp.ui.composables

import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import com.ferdinand.pdftestapp.R
import com.ferdinand.pdftestapp.models.PdfEvent
import com.ferdinand.pdftestapp.models.PdfPresentationFile
import com.ferdinand.pdftestapp.models.state.PdfQueryState
import com.ferdinand.pdftestapp.utils.errorToString

/**
 * This is the list composable to be used in both the [com.ferdinand.pdftestapp.viewmodel.PdfViewModel] and
 * [com.ferdinand.pdftestapp.ui.main.PdfListFragment]
 *
 * @param pdfQueryState is the state of the UI
 * @param onPdfClick is a function to handle clicking items on screen
 * @param handleEvent is a function to pass Ui events to the viewModel
 * @param modifier is the modifier to be applied to the layout
 * */
@ExperimentalFoundationApi
@Composable
fun PdfList(
    pdfQueryState: PdfQueryState,
    onPdfClick: (pdf: PdfPresentationFile) -> Unit,
    handleEvent: (event: PdfEvent) -> Unit,
    modifier: Modifier
) {
    val pdfList = pdfQueryState.listData
    val state = rememberLazyListState()
    val context = LocalContext.current

    MaterialTheme {
        Box(
            modifier = modifier
        ) {
            if (pdfQueryState.isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center)
                )
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.size_8)),
                    state = state
                ) {
                    pdfList?.let {
                        items(items = it) { pdf ->
                            PdfItem(
                                pdfFile = pdf,
                                onFavouriteClick = { file ->
                                    handleEvent(PdfEvent.OnFavouriteEvent(file))
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .animateItemPlacement(animationSpec = tween(durationMillis = 600))
                                    .clickable {
                                        onPdfClick(pdf)
                                    }
                            )
                        }

                        item {
                            Spacer(modifier = Modifier.padding(dimensionResource(id = R.dimen.size_2)))
                        }
                    }
                }

                pdfQueryState.error?.let {
                    ErrorDialog(
                        error = it.errorToString(context),
                        dismissError = {
                            handleEvent(PdfEvent.ErrorDismissedEvent)
                        }
                    )
                }
            }
        }
    }
}
