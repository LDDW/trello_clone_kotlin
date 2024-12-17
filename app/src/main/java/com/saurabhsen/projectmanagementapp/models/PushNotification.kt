package com.trello.clone.models

data class PushNotification(
    val data: NotificationData,
    val to: String
)