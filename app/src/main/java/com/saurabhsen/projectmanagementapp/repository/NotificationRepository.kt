package com.trello.clone.repository

import com.trello.clone.api.RetrofitInstance
import com.trello.clone.models.PushNotification

class NotificationRepository {
    suspend fun postNotification(notification: PushNotification) = RetrofitInstance.api.postNotification(notification)
}