package com.ferdinand.pdftestapp.ui.composables

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
import com.ferdinand.pdftestapp.data.PdfFile
import com.ferdinand.pdftestapp.models.state.PdfQueryState
import com.ferdinand.pdftestapp.utils.errorToString

@Composable
fun PdfList(
    pdfQueryState: PdfQueryState,
    onPdfClick: (pdf: PdfFile) -> Unit,
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
