package com.ferdinand.pdftestapp.data

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.ferdinand.pdftestapp.utils.dbFavFiles
import com.google.common.truth.Truth.assertThat
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
class PdfDatabaseTest {

    private lateinit var pdfDao: PdfDao
    private lateinit var db: PdfDatabase

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(
            context, PdfDatabase::class.java
        ).build()
        pdfDao = db.pdfDao()
    }

    @Test
    fun `adding file to fav and checking for fav returns valid file`() = runTest {
        pdfDao.addToFav(dbFavFiles.first())

        assertThat(pdfDao.getFileById(dbFavFiles.first().id)).isNotNull()
        assertThat(pdfDao.getFileById(dbFavFiles.first().id)?.id).isEqualTo(dbFavFiles.first().id)
    }

    @Test
    fun `checking non fav file for fav returns null file`() = runTest {
        assertThat(pdfDao.getFileById(23)).isNull()
    }

    @Test
    fun `adding file in db and deleting it returns null as a favorite and valid file for remaining data`() = runTest {
        dbFavFiles.forEach {
            pdfDao.addToFav(it)
        }

        pdfDao.removeFromFav(dbFavFiles[1])

        assertThat(pdfDao.getFileById(dbFavFiles.first().id)?.id).isEqualTo(dbFavFiles.first().id)
        assertThat(pdfDao.getFileById(dbFavFiles[1].id)).isNull()
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }
}