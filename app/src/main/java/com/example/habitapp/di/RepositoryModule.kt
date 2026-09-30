package com.example.habitapp.di

import com.example.habitapp.data.repository.HabitRepositoryImpl
import com.example.habitapp.domain.repository.HabitRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindHabitRepository(
        repositoryImpl: HabitRepositoryImpl
    ): HabitRepository
}