package com.ferdinand.pdftestapp.di

import android.content.Context
import com.ferdinand.pdftestapp.repo.PdfRepo
import com.ferdinand.pdftestapp.repo.PdfRepoImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class RepositoryModule {

    @Singleton
    @Provides
    fun providePdfRepository(@ApplicationContext context: Context): PdfRepo =
        PdfRepoImpl(context)
}