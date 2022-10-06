package com.ferdinand.pdftestapp.ui.main

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Build.VERSION.SDK_INT
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import com.ferdinand.pdftestapp.R
import com.ferdinand.pdftestapp.models.PdfEvent
import com.ferdinand.pdftestapp.ui.composables.PdfList
import com.ferdinand.pdftestapp.ui.composables.RequestPermission
import com.ferdinand.pdftestapp.ui.theme.PdfTestAppTheme
import com.ferdinand.pdftestapp.utils.toast
import com.ferdinand.pdftestapp.viewmodel.PdfViewModel
import com.pspdfkit.jetpack.compose.ExperimentalPSPDFKitApi
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
@ExperimentalComposeUiApi
@ExperimentalPSPDFKitApi
class PdfListFragment : Fragment() {

    private val viewModel: PdfViewModel by viewModels()
    private var didUserNavigateToDetails = false

    private val storagePermissions =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->

            val isAnyPermissionDenied = permissions.entries.any { !it.value }

            if (!isAnyPermissionDenied) {
                viewModel.handleEvent(PdfEvent.GetAllFilesEvent)
                viewModel.onReadPermissionsStateChanged(true)
            } else {
                toast(getString(R.string.toast_storage_permissions))
            }
        }

    private val manageStoragePermission = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        if (SDK_INT >= Build.VERSION_CODES.R) {
            if (Environment.isExternalStorageManager()) {
                viewModel.handleEvent(PdfEvent.GetAllFilesEvent)
                viewModel.onReadPermissionsStateChanged(true)
            } else {
                toast(getString(R.string.toast_storage_permissions))
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.onReadPermissionsStateChanged(checkStoragePermission())
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return ComposeView(requireContext()).apply {
            setContent {
                val pdfQueryState by viewModel.pdfQueryState.collectAsState()
                val arePermissionsGranted = viewModel.areReadPermissionsGranted.value

                PdfTestAppTheme {
                    Scaffold(
                        topBar = {
                            TopAppBar(
                                title = {
                                    Text(
                                        text = stringResource(id = R.string.label_all_pdf_files),
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                },
                                actions = {
                                    IconButton(onClick = {
                                        findNavController().navigate(R.id.action_pdfListFragment_to_searchFragment)
                                    }) {
                                        Icon(Icons.Default.Search, stringResource(id = R.string.search))
                                    }
                                }
                            )
                        }
                    ) {
                        Box(
                            modifier = Modifier
                                .padding(it)
                                .fillMaxSize()
                        ) {
                            if (arePermissionsGranted) {
                                PdfList(
                                    pdfQueryState = pdfQueryState,
                                    onPdfClick = { pdfFile ->
                                        didUserNavigateToDetails = true
                                        findNavController().navigate(
                                            PdfListFragmentDirections.actionPdfListFragmentToPdfViewActivity(pdfFile.id)
                                        )
                                    },
                                    handleEvent = { event ->
                                        viewModel.handleEvent(event)
                                    },
                                    modifier = Modifier
                                        .padding(dimensionResource(id = R.dimen.size_4))
                                        .fillMaxSize()
                                )
                            } else {
                                RequestPermission(
                                    onRequestPermissionsClicked = { requestStoragePermission() },
                                    onCloseClicked = { activity?.finish() },
                                    modifier = Modifier
                                        .padding(dimensionResource(id = R.dimen.size_4))
                                        .align(Alignment.Center)
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    private fun checkStoragePermission(): Boolean {
        return if (SDK_INT >= Build.VERSION_CODES.R) {
            Environment.isExternalStorageManager()
        } else {
            val readExternalStoragePerm =
                ContextCompat.checkSelfPermission(requireContext(), android.Manifest.permission.READ_EXTERNAL_STORAGE)
            readExternalStoragePerm == PackageManager.PERMISSION_GRANTED
        }
    }

    private fun requestStoragePermission() {
        if (SDK_INT >= Build.VERSION_CODES.R) {
            try {
                val intent = Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION)
                intent.addCategory("android.intent.category.DEFAULT")
                intent.data = Uri.parse(String.format("package:%s", context?.applicationContext?.packageName))
                manageStoragePermission.launch(intent)
            } catch (e: Exception) {
                val intent = Intent()
                intent.action = Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION
                manageStoragePermission.launch(intent)
            }
        } else {
            storagePermissions.launch(arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE))
        }
    }

    override fun onResume() {
        super.onResume()
        if (didUserNavigateToDetails) {
            viewModel.handleEvent(PdfEvent.GetAllFilesEvent)
            didUserNavigateToDetails = false
        }
    }
}