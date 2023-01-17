package com.bookfriends.di

import com.bookfriends.data.repositories.PlacesRepositoryImpl
import com.bookfriends.domain.repositories.PlaceRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
abstract class RepositoriesModule {

    @Binds
    abstract fun bindCharacterRepository(impl: PlacesRepositoryImpl): PlaceRepository
}