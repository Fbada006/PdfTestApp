package com.ferdinand.pdftestapp.ui.composables

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import com.ferdinand.pdftestapp.R
import com.ferdinand.pdftestapp.models.PdfPresentationFile

/**
 * This is the item card used to display a list of pdf files in both the [com.ferdinand.pdftestapp.viewmodel.PdfViewModel] and
 * [com.ferdinand.pdftestapp.ui.main.PdfListFragment].
 *
 * @param pdfFile is the current item in the row to be bound to the UI
 * @param onFavouriteClick is a function to handle the favourite feature
 * @param modifier is the modifier to be applied to the layout
 * */
@Composable
fun PdfItem(
    pdfFile: PdfPresentationFile,
    onFavouriteClick: (pdf: PdfPresentationFile) -> Unit,
    modifier: Modifier
) {
    Card(
        modifier = modifier,
        elevation = dimensionResource(id = R.dimen.size_8)
    ) {
        Row(
            modifier = Modifier.padding(dimensionResource(id = R.dimen.size_8)),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Icon(
                imageVector = Icons.Filled.PictureAsPdf,
                tint = MaterialTheme.colors.secondary,
                contentDescription = stringResource(id = R.string.cd_pdf_icon),
                modifier = Modifier.padding(end = dimensionResource(id = R.dimen.size_8))
            )

            Text(
                text = pdfFile.pdfName,
                style = MaterialTheme.typography.h6,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .weight(1f)
            )

            LikeToggleButton(isFav = pdfFile.isFavourite, onFavorite = { onFavouriteClick(pdfFile) })

        }
    }
}