package com.ferdinand.pdftestapp.viewmodel

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import app.cash.turbine.test
import com.ferdinand.pdftestapp.data.PdfDao
import com.ferdinand.pdftestapp.data.PdfDatabase
import com.ferdinand.pdftestapp.mappers.toPresentationModel
import com.ferdinand.pdftestapp.models.PdfEvent
import com.ferdinand.pdftestapp.models.state.PdfQueryState
import com.ferdinand.pdftestapp.repo.PdfRepo
import com.ferdinand.pdftestapp.utils.EmptyListException
import com.ferdinand.pdftestapp.utils.Resource
import com.ferdinand.pdftestapp.utils.pdfFiles
import com.google.common.truth.Truth.assertThat
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config
import java.io.IOException

@Config(sdk = [32])
@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
class PdfViewModelTest {

    private lateinit var db: PdfDatabase
    private lateinit var pdfDao: PdfDao
    private val context = ApplicationProvider.getApplicationContext<Context>()

    private val pdfRepo = mockk<PdfRepo>(relaxed = true)
    private val pdfViewModel = PdfViewModel(pdfRepo)

    @Before
    fun createDb() {
        db = Room.inMemoryDatabaseBuilder(
            context, PdfDatabase::class.java
        ).build()
        pdfDao = db.pdfDao()
    }

    @Test
    fun `get all pdf files returns valid list`() = runTest {
        // Given
        coEvery { pdfRepo.getPdfList() } returns Resource.Success(pdfFiles)

        // When
        pdfViewModel.handleEvent(PdfEvent.GetAllFilesEvent)

        // Assert flow has correct data
        pdfViewModel.pdfQueryState.test {
            assertThat(awaitItem()).isEqualTo(PdfQueryState(isLoading = false, listData = pdfFiles.map {
                it.toPresentationModel()
            }))
        }
    }

    @Test
    fun `get all pdf files returns empty list exception`() = runTest {
        val exception = EmptyListException()
        // Given
        coEvery { pdfRepo.getPdfList() } returns Resource.Error(exception)

        // When
        pdfViewModel.handleEvent(PdfEvent.GetAllFilesEvent)

        // Assert flow has correct data
        pdfViewModel.pdfQueryState.test {
            assertThat(awaitItem()).isEqualTo(PdfQueryState(isLoading = false, error = exception))
        }
    }

    @Test
    fun `get all pdf files returns general exception`() = runTest {
        val exception = NullPointerException()
        // Given
        coEvery { pdfRepo.getPdfList() } returns Resource.Error(exception)

        // When
        pdfViewModel.handleEvent(PdfEvent.GetAllFilesEvent)

        // Assert flow has correct data
        pdfViewModel.pdfQueryState.test {
            assertThat(awaitItem()).isEqualTo(PdfQueryState(isLoading = false, error = exception))
        }
    }

    @Test
    fun `adding file to fav works properly with a non fav pdf`() {

    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }
}