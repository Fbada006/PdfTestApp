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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.ferdinand.pdftestapp.R

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

        Spacer(modifier = Modifier.padding(4.dp))

        Button(onClick = onRequestPermissionsClicked) {
            Text(text = stringResource(id = R.string.button_request_permission))
        }

        Button(onClick = onCloseClicked) {
            Text(text = stringResource(id = R.string.button_close))
        }
    }
}