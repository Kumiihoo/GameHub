package com.mariomedina.gamehub.model

data class MessageModel(
    val senderId: String? = "",
    val message: String? = "",
    val currentTime: String? = "",
    val currentDate: String? = ""
)
