package com.ferdinand.pdftestapp.ui.composables

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.ferdinand.pdftestapp.models.PdfFile

@Composable
fun PdfItem(
    pdfFile: PdfFile,
    onFavouriteClick: (pdf: PdfFile) -> Unit,
    modifier: Modifier
) {
    Card(
        modifier = modifier,
        elevation = 8.dp
    ) {
        Row(modifier = Modifier.padding(8.dp), verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = pdfFile.pdfName,
                style = MaterialTheme.typography.h6,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .weight(1f)
            )

            LikeToggleButton(initialCheckedValue = pdfFile.isFavourite, onFavorite = { onFavouriteClick(pdfFile) })

        }
    }
}