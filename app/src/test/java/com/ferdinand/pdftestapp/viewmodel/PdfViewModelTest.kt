package com.ferdinand.pdftestapp.viewmodel

import androidx.test.ext.junit.runners.AndroidJUnit4
import app.cash.turbine.test
import app.cash.turbine.testIn
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
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config

@Config(sdk = [32])
@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
class PdfViewModelTest {

    private val pdfRepo = mockk<PdfRepo>(relaxed = true)
    private val pdfViewModel = PdfViewModel(pdfRepo)

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
    fun `get search pdf files returns valid list`() = runTest {
        // Given
        coEvery { pdfRepo.getPdfListBasedOnQuery(any()) } returns Resource.Success(pdfFiles)

        // When
        pdfViewModel.handleEvent(PdfEvent.SearchEvent)

        // Assert flow has correct data
        pdfViewModel.filteredPdfState.test {
            assertThat(awaitItem()).isEqualTo(PdfQueryState(isLoading = false, listData = pdfFiles.map {
                it.toPresentationModel()
            }))
        }
    }

    @Test
    fun `get search pdf files returns empty list exception`() = runTest {
        val exception = EmptyListException()
        // Given
        coEvery { pdfRepo.getPdfListBasedOnQuery(any()) } returns Resource.Error(exception)

        // When
        pdfViewModel.handleEvent(PdfEvent.SearchEvent)

        // Assert flow has correct data
        pdfViewModel.filteredPdfState.test {
            assertThat(awaitItem()).isEqualTo(PdfQueryState(isLoading = false, error = exception))
        }
    }

    @Test
    fun `get search pdf files returns general exception`() = runTest {
        val exception = NullPointerException()
        // Given
        coEvery { pdfRepo.getPdfListBasedOnQuery(any()) } returns Resource.Error(exception)

        // When
        pdfViewModel.handleEvent(PdfEvent.SearchEvent)

        // Assert flow has correct data
        pdfViewModel.filteredPdfState.test {
            assertThat(awaitItem()).isEqualTo(PdfQueryState(isLoading = false, error = exception))
        }
    }

    @Test
    fun `get file based on id returns valid file`() = runTest {
        // Given
        coEvery { pdfRepo.getPdfFileBasedOnId(pdfFiles.first().id) } returns Resource.Success(pdfFiles.first())

        // When
        pdfViewModel.handleEvent(PdfEvent.DisplayFileDetailsEvent(pdfFiles.first().id))

        // Assert flow has correct data
        pdfViewModel.singlePdfState.test {
            assertThat(awaitItem()).isEqualTo(PdfQueryState(isLoading = false, singlePdfData = pdfFiles.first().toPresentationModel()))
        }
    }

    @Test
    fun `get file based on id returns general exception`() = runTest {
        val exception = NullPointerException()
        // Given
        coEvery { pdfRepo.getPdfFileBasedOnId(pdfFiles.first().id) } returns Resource.Error(exception)

        // When
        pdfViewModel.handleEvent(PdfEvent.DisplayFileDetailsEvent(pdfFiles.first().id))

        // Assert flow has correct data
        pdfViewModel.singlePdfState.test {
            assertThat(awaitItem()).isEqualTo(PdfQueryState(isLoading = false, error = exception))
        }
    }

    @Test
    fun `dismissing error sets error flows to null correctly`() = runTest {
        pdfViewModel.handleEvent(PdfEvent.ErrorDismissedEvent)

        val singleTurbine = pdfViewModel.singlePdfState.testIn(this)
        val filteredTurbine = pdfViewModel.filteredPdfState.testIn(this)
        val allTurbine = pdfViewModel.pdfQueryState.testIn(this)

        assertThat(singleTurbine.awaitItem().error).isNull()
        assertThat(filteredTurbine.awaitItem().error).isNull()
        assertThat(allTurbine.awaitItem().error).isNull()

        singleTurbine.cancel()
        filteredTurbine.cancel()
        allTurbine.cancel()
    }
}