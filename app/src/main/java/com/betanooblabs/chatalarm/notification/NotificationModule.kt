package com.betanooblabs.chatalarm.notification

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object NotificationModule {

    @Provides
    fun provideChatNotificationParser(): ChatNotificationParser {
        return ChatNotificationParser()
    }
}