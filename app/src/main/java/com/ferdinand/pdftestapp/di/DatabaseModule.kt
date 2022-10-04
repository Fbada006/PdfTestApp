package com.ferdinand.pdftestapp.di

import android.content.Context
import androidx.room.Room
import com.ferdinand.pdftestapp.data.PdfDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class DatabaseModule {

    @Singleton
    @Provides
    fun provideDataBase(@ApplicationContext context: Context): PdfDatabase {
        return Room.databaseBuilder(
            context.applicationContext,
            PdfDatabase::class.java,
            "pdffavorites.db"
        )
            .build()
    }
}