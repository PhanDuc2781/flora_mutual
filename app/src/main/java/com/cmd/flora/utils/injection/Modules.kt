package com.cmd.flora.utils.injection

import com.cmd.flora.data.repository.CallRepository
import com.cmd.flora.data.repository.CallRepositoryImpl
import com.cmd.flora.data.repository.FacilityInformationRepository
import com.cmd.flora.data.repository.FacilityInformationRepositoryImpl
import com.cmd.flora.data.repository.HomeRepository
import com.cmd.flora.data.repository.HomeRepositoryImpl
import com.cmd.flora.data.repository.InquiryRepository
import com.cmd.flora.data.repository.InquiryRepositoryImpl
import com.cmd.flora.data.repository.LoginRepository
import com.cmd.flora.data.repository.LoginRepositoryImpl
import com.cmd.flora.data.repository.MyPageRepository
import com.cmd.flora.data.repository.MyPageRepositoryImpl
import com.cmd.flora.data.repository.NewsLetterRepository
import com.cmd.flora.data.repository.NewsLetterRepositoryImpl
import com.cmd.flora.data.repository.NotificationRepository
import com.cmd.flora.data.repository.NotificationRepositoryImpl
import com.cmd.flora.data.repository.QRRepository
import com.cmd.flora.data.repository.QRRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface Modules {

    @Binds
    @Singleton
    fun provideLoginRepository(loginRepositoryImpl: LoginRepositoryImpl): LoginRepository

    @Binds
    @Singleton
    fun provideHomeRepository(homeRepositoryImpl: HomeRepositoryImpl): HomeRepository

    @Binds
    @Singleton
    fun provideQRRepository(qRRepositoryImpl: QRRepositoryImpl): QRRepository

    @Binds
    @Singleton
    fun provideMyPageRepositoryImpl(myPageRepositoryImpl: MyPageRepositoryImpl): MyPageRepository

    @Binds
    @Singleton
    fun provideFuneralRepositoryImpl(facilityInformationRepositoryImpl: FacilityInformationRepositoryImpl): FacilityInformationRepository

    @Binds
    @Singleton
    fun provideNotificationRepository(notificationRepositoryImpl: NotificationRepositoryImpl): NotificationRepository

    @Binds
    @Singleton
    fun provideCallRepository(callRepositoryImpl: CallRepositoryImpl): CallRepository

    @Binds
    @Singleton
    fun provideInquiryRepository(inquiryRepositoryImpl: InquiryRepositoryImpl): InquiryRepository
    
    @Binds
    @Singleton
    fun provideNewsLetterRepository( newsLetterRepositoryImpl: NewsLetterRepositoryImpl): NewsLetterRepository
}