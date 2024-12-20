package com.trello.clone.utils

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.provider.MediaStore
import android.webkit.MimeTypeMap



object Constants {
    const val USERS = "users"
    const val IMAGE: String = "image"
    const val NAME: String = "name"
    const val MOBILE: String = "mobile"
    const val PICK_IMAGE_REQUEST_CODE = 2
    const val READ_STORAGE_PERMISSION_CODE = 1
    const val BOARDS = "boards"
    const val ASSIGNED_TO = "assignedTo"
    const val DOCUMENT_ID = "documentId"
    const val TASK_LIST = "taskList"
    const val BOARD_DETAIL = "board_detail"
    const val ID = "id"
    const val EMAIL = "email"
    const val TASK_LIST_ITEM_POSITION = "task_list_item_position"
    const val CARD_LIST_ITEM_POSITION = "card_list_item_position"
    const val BOARD_MEMBERS_LIST = "board_members_list"
    const val SELECT = "Select"
    const val UN_SELECT = "UnSelect"
    const val SHARED_PREFERENCES = "SharedPref"
    const val FCM_TOKEN_UPDATED = "fcmTokenUpdated"
    const val FCM_TOKEN = "fcmToken"
    const val FCM_BASE_URL:String = "https://fcm.googleapis.com/fcm/"
    const val CONTENT_TYPE = "application/json"
    const val EDIT_BOARD = "edit_board"
    const val FCM_SERVER_KEY:String = "AAAAea5QcZ0:APA91bEIgqSIXBEgNRu-Fe9ucKtlttuus5ogyDhUs2gBxeiMGNxwAf8WFpmY2DpG5AYqy-dNdzS2H4kHna_4KutvwuXWoAlA3hBIfkEYmLKD338Nr2HlXppW8VQAW-32pWIlklqss-Nc"

    fun getFileExtension(activity: Activity,uri: Uri?): String? {
        return MimeTypeMap.getSingleton().getExtensionFromMimeType(activity.contentResolver.getType(uri!!))
    }

    fun showImageChooser(activity: Activity) {
        val galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        activity.startActivityForResult(galleryIntent, PICK_IMAGE_REQUEST_CODE)
    }

}