package com.ferdinand.pdftestapp.ui.details

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Output
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.core.content.ContextCompat
import androidx.navigation.navArgs
import com.ferdinand.pdftestapp.R
import com.ferdinand.pdftestapp.models.PdfEvent
import com.ferdinand.pdftestapp.ui.composables.DetailsLikeToggleButton
import com.ferdinand.pdftestapp.ui.composables.ErrorDialog
import com.ferdinand.pdftestapp.ui.theme.PdfTestAppTheme
import com.ferdinand.pdftestapp.utils.toast
import com.ferdinand.pdftestapp.viewmodel.PdfViewModel
import com.pspdfkit.configuration.activity.PdfActivityConfiguration
import com.pspdfkit.configuration.page.PageScrollDirection
import com.pspdfkit.jetpack.compose.DocumentView
import com.pspdfkit.jetpack.compose.ExperimentalPSPDFKitApi
import com.pspdfkit.jetpack.compose.rememberDocumentState
import dagger.hilt.android.AndroidEntryPoint

/*
* This is the screen that shows the pdf file using the PSPDFKIT library. The screen is actually an activity because PSPDFKIT expects
* an activity and not a fragment. Using a fragment will throw exceptions occasionally
* */
@ExperimentalPSPDFKitApi
@AndroidEntryPoint
class PdfViewActivity : AppCompatActivity() {

    private val args by navArgs<PdfViewActivityArgs>()
    private val viewModel by viewModels<PdfViewModel>()

    private val writePermissions =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->

            val isAnyPermissionDenied = permissions.entries.any { !it.value }

            if (!isAnyPermissionDenied) {
                saveCurrentPageToFile()
            } else {
                toast(getString(R.string.toast_write_permissions))
            }
        }

    private val manageStoragePermission = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if (Environment.isExternalStorageManager()) {
                saveCurrentPageToFile()
            } else {
                toast(getString(R.string.toast_write_permissions))
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.onWritePermissionsStateChanged(checkWritePermission())
        viewModel.handleEvent(PdfEvent.DisplayFileDetailsEvent(args.fileId))

        setContent {
            val pdfQueryState by viewModel.singlePdfState.collectAsState()
            val pdfFile = pdfQueryState.singlePdfData
            val areWritePermissionsGranted = viewModel.areWritePermissionsGranted.value

            PdfTestAppTheme {
                Scaffold(
                    topBar = {
                        TopAppBar(
                            title = {
                                Text(
                                    text = pdfFile?.pdfName ?: stringResource(id = R.string.app_name),
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            },
                            navigationIcon = {
                                IconButton(onClick = {
                                    finish()
                                }) {
                                    Icon(Icons.Rounded.ArrowBack, contentDescription = stringResource(id = R.string.cd_back_button))
                                }
                            },
                            actions = {
                                pdfFile?.let { file ->
                                    // Only display the fav state once there is a valid file
                                    DetailsLikeToggleButton(initialCheckedValue = file.isFavourite, onFavorite = {
                                        viewModel.handleEvent(PdfEvent.OnFavouriteEvent(file))
                                    })
                                }
                            }
                        )
                    },
                    floatingActionButton = {
                        pdfFile?.let {
                            // No point showing the fab if the file is non existent
                            FloatingActionButton(onClick = {
                                if (areWritePermissionsGranted) {
                                    saveCurrentPageToFile()
                                } else {
                                    requestWritePermissions()
                                }
                            }) {
                                Icon(Icons.Rounded.Output, contentDescription = stringResource(id = R.string.cd_export_page_button))
                            }
                        }
                    }
                ) {

                    Box(
                        modifier = Modifier
                            .padding(it)
                            .fillMaxSize()
                    ) {
                        if (pdfQueryState.isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.align(Alignment.Center)
                            )
                        } else {
                            pdfFile?.let { file ->
                                val documentUri = remember { file.uri }

                                val pdfActivityConfiguration = remember {
                                    PdfActivityConfiguration
                                        .Builder(this@PdfViewActivity)
                                        .scrollDirection(PageScrollDirection.VERTICAL)
                                        .build()
                                }

                                val documentState = rememberDocumentState(documentUri, pdfActivityConfiguration)
                                val currentPage by remember(documentState.currentPage) { mutableStateOf(documentState.currentPage) }
                                viewModel.onCurrentPageChanged(currentPage)

                                DocumentView(
                                    documentState = documentState,
                                    modifier = Modifier
                                        .padding(dimensionResource(id = R.dimen.size_4))
                                )

                                LaunchedEffect(currentPage) {
                                    documentState.scrollToPage(currentPage)
                                }
                            }

                            pdfQueryState.error?.let {
                                ErrorDialog(
                                    error = stringResource(id = R.string.something_went_wrong),
                                    dismissError = {
                                        viewModel.handleEvent(PdfEvent.ErrorDismissedEvent)
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    @SuppressLint("CheckResult")
    private fun saveCurrentPageToFile() {
        viewModel.handleEvent(PdfEvent.ExportCurrentPageEvent)
        viewModel.exportPageFlowable
            .subscribe(
                {
                    toast(getString(R.string.file_export_ongoing))
                },
                { error ->
                    toast(getString(R.string.file_exported_fail, error.message))
                },
                {
                    toast(getString(R.string.file_exported_success))
                }
            )
    }

    private fun requestWritePermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            try {
                val intent = Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION)
                intent.addCategory("android.intent.category.DEFAULT")
                intent.data = Uri.parse(String.format("package:%s", applicationContext?.packageName))
                manageStoragePermission.launch(intent)
            } catch (e: Exception) {
                val intent = Intent()
                intent.action = Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION
                manageStoragePermission.launch(intent)
            }
        } else {
            writePermissions.launch(arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE))
        }
    }

    private fun checkWritePermission(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            Environment.isExternalStorageManager()
        } else {
            val readExternalStoragePerm =
                ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
            readExternalStoragePerm == PackageManager.PERMISSION_GRANTED
        }
    }
}
