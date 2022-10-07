package com.ferdinand.pdftestapp.utils

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config

@Config(sdk = [32])
@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
class ThrowableExtKtTest {

    private val context = ApplicationProvider.getApplicationContext<Context>()

    @Test
    fun `empty list error to string extension maps error correctly`() {
        val errorString = EmptyListException().errorToString(context)

        assertThat(errorString).isEqualTo("There is no data to display at the moment. Confirm that you have PDF files in your download folder and try again. Thank you.")
    }

    @Test
    fun `genera error to string extension maps error correctly`() {
        val errorString = java.lang.Exception().errorToString(context)

        assertThat(errorString).isEqualTo("Something went wrong in accessing your PDF files. Please try again. Thank you.")
    }
}