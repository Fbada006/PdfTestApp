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
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.ferdinand.pdftestapp.models.PdfFile
import com.ferdinand.pdftestapp.models.state.PdfQueryState

@Composable
fun PdfList(
    pdfQueryState: PdfQueryState,
    onPdfClick: (pdf: PdfFile) -> Unit,
    onFavouriteClick: (pdf: PdfFile) -> Unit,
    modifier: Modifier
) {
    val pdfList = pdfQueryState.data
    val state = rememberLazyListState()

    MaterialTheme {
        Box(
            modifier = modifier
        ) {
            if (pdfQueryState.isLoading) {
                CircularProgressIndicator()
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    state = state
                ) {
                    pdfList?.let {
                        item {
                            Spacer(modifier = Modifier.padding(2.dp))
                        }

                        items(items = it) { pdf ->
                            PdfItem(
                                pdfFile = pdf,
                                onFavouriteClick = onFavouriteClick,
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
            }
        }
    }
}
