package com.ferdinand.pdftestapp.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.ferdinand.pdftestapp.ui.theme.PdfTestAppTheme
import com.ferdinand.pdftestapp.viewmodel.PdfViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PdfListFragment : Fragment() {

    private val viewModel: PdfViewModel by viewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return ComposeView(requireContext()).apply {
            setContent {
                val text by viewModel.placeholder.collectAsState()
                PdfTestAppTheme {
                    // A surface container using the 'background' color from the theme
                    Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colors.background) {
                        Text(text = text)
                    }
                }
            }
        }
    }
}