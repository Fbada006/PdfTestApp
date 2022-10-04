package com.ferdinand.pdftestapp.ui.main

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
import androidx.compose.ui.unit.dp
import com.ferdinand.pdftestapp.models.PdfDestination
import com.ferdinand.pdftestapp.models.PdfEvent
import com.ferdinand.pdftestapp.models.PdfFile
import com.ferdinand.pdftestapp.models.state.PdfQueryState
import com.ferdinand.pdftestapp.utils.errorToString

@Composable
fun PdfList(
    pdfQueryState: PdfQueryState,
    onPdfClick: (pdf: PdfFile) -> Unit,
    destination: PdfDestination,
    handleEvent: (event: PdfEvent) -> Unit,
    modifier: Modifier
) {
    val pdfList = pdfQueryState.data
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
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    state = state
                ) {
                    pdfList?.let {
                        items(items = it) { pdf ->
                            PdfItem(
                                pdfFile = pdf,
                                onFavouriteClick = { file ->
                                    handleEvent(PdfEvent.OnFavouriteEvent(file, destination))
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        onPdfClick(pdf)
                                    }
                            )
                        }

                        item {
                            Spacer(modifier = Modifier.padding(2.dp))
                        }
                    }
                }

                pdfQueryState.error?.let {
                    ErrorDialog(
                        error = it.errorToString(context),
                        dismissError = {
                            handleEvent(PdfEvent.ErrorDismissed)
                        }
                    )
                }
            }
        }
    }
}
