package com.ferdinand.pdftestapp.ui.main

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.primarySurface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.ferdinand.pdftestapp.R
import com.ferdinand.pdftestapp.models.PdfEvent

@ExperimentalComposeUiApi
@Composable
fun SearchAppBar(
    query: String,
    onBackClicked: () -> Unit,
    onQueryChanged: (String) -> Unit,
    handleEvent: (event: PdfEvent) -> Unit
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    Surface(
        modifier = Modifier
            .fillMaxWidth(),
        elevation = 8.dp,
        color = MaterialTheme.colors.primarySurface
    ) {
        Column {
            Row(modifier = Modifier.fillMaxWidth()) {

                IconButton(
                    onClick = onBackClicked,
                    modifier = Modifier
                        .align(Alignment.CenterVertically)
                        .padding(end = 8.dp)
                ) {
                    Icon(Icons.Rounded.ArrowBack, stringResource(id = R.string.cd_back_button))
                }

                TextField(
                    modifier = Modifier
                        .weight(1f)
                        .padding(4.dp),
                    value = query,
                    onValueChange = { onQueryChanged(it) },
                    label = { Text(text = stringResource(id = R.string.search)) },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Text,
                        imeAction = ImeAction.Done,
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = {
                            handleEvent(PdfEvent.SearchEvent(query))
                            keyboardController?.hide()
                        },
                    ),
                    leadingIcon = { Icon(Icons.Filled.Search, contentDescription = stringResource(id = R.string.search_icon)) },
                    textStyle = TextStyle(color = MaterialTheme.colors.onSurface),
                    colors = TextFieldDefaults.textFieldColors(backgroundColor = MaterialTheme.colors.surface),
                )
            }
        }
    }
}