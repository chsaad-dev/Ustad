package com.ustad.di

import com.ustad.data.repository.AdminRepositoryImpl
import com.ustad.data.repository.AuthRepositoryImpl
import com.ustad.data.repository.JobRepositoryImpl
import com.ustad.data.repository.WorkerRepositoryImpl
import com.ustad.domain.repository.AdminRepository
import com.ustad.domain.repository.AuthRepository
import com.ustad.domain.repository.JobRepository
import com.ustad.domain.repository.WorkerRepository
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
    abstract fun bindAuthRepository(
        impl: AuthRepositoryImpl
    ): AuthRepository

    @Binds
    @Singleton
    abstract fun bindWorkerRepository(
        impl: WorkerRepositoryImpl
    ): WorkerRepository

    @Binds
    @Singleton
    abstract fun bindJobRepository(
        impl: JobRepositoryImpl
    ): JobRepository

    @Binds
    @Singleton
    abstract fun bindAdminRepository(
        impl: AdminRepositoryImpl
    ): AdminRepository
}
