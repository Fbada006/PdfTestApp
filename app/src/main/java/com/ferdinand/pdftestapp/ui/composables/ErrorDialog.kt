package com.ferdinand.pdftestapp.ui.composables

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.AlertDialog
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.sp
import com.ferdinand.pdftestapp.R

/**
 * This is used to show a dialog box in case of any errors when getting data. It has a title, message, and a button to dismiss
 *
 * @param error is the error message to show on screen
 * @param dismissError is a function to handle dismissing the dialog
 * @param modifier is the modifier to be applied to the layout
 * */
@Composable
fun ErrorDialog(
    modifier: Modifier = Modifier,
    error: String?,
    dismissError: () -> Unit
) {
    error?.let {
        AlertDialog(
            modifier = modifier,
            onDismissRequest = dismissError,
            buttons = {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(),
                    contentAlignment = Alignment.CenterEnd
                ) {
                    TextButton(onClick = dismissError) {
                        Text(
                            text = stringResource(id = R.string.error_action)
                        )
                    }
                }
            },
            title = {
                Text(
                    text = stringResource(id = R.string.error_title),
                    fontSize = dimensionResource(id = R.dimen.font_size_18).value.sp
                )
            },
            text = {
                Text(text = it)
            }
        )
    }
}