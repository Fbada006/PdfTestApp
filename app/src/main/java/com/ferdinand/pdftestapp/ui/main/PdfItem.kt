package com.ferdinand.pdftestapp.ui.main

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.ferdinand.pdftestapp.R
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
        Row(modifier = Modifier.padding(8.dp)) {
            Text(
                text = pdfFile.pdfName,
                style = MaterialTheme.typography.h6,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .weight(1f)
            )

            Icon(
                imageVector = if (pdfFile.isFavourite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                contentDescription = stringResource(id = R.string.favourite),
                modifier = Modifier.clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = rememberRipple(bounded = false),
                    onClick = { onFavouriteClick(pdfFile) }
                )
            )
        }
    }
}