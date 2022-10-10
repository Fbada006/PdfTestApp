package com.ferdinand.pdftestapp.ui.composables

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import com.ferdinand.pdftestapp.R

/**
 * This function draws a UI requesting the user to grant permissions and explains why they are needed. The user has the option of
 * closing the application and closing it completely
 *
 * @param onRequestPermissionsClicked is the functions to trigger logic to request permissions
 * @param onCloseClicked is the function to trigger logic when the user wants to close the app
 * @param modifier is the modifier to be applied to the layout
 * */
@Composable
fun RequestPermission(
    onRequestPermissionsClicked: () -> Unit,
    onCloseClicked: () -> Unit,
    modifier: Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text(text = stringResource(id = R.string.storage_permission_needed))

        Spacer(modifier = Modifier.padding(dimensionResource(id = R.dimen.size_4)))

        Button(onClick = onRequestPermissionsClicked) {
            Text(text = stringResource(id = R.string.button_request_permission))
        }

        Button(onClick = onCloseClicked) {
            Text(text = stringResource(id = R.string.button_close))
        }
    }
}