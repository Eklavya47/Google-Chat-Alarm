package com.betanooblabs.chatalarm.notification

import android.content.Context
import com.betanooblabs.chatalarm.settings.AlarmContactRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import jakarta.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NotificationModule {

    @Provides
    fun provideChatNotificationParser(): ChatNotificationParser {
        return ChatNotificationParser()
    }

    @Provides
    @Singleton
    fun provideAlarmContactRepository(
        @ApplicationContext context: Context
    ): AlarmContactRepository {
        return AlarmContactRepository(context)
    }
}