package com.mariomedina.gamehub.model

data class FormModel(
    val formSenderId: String? = "",
    val formEmail: String? = "",
    val title: String? = "",
    val message: String? = "",
    val formName: String? = "",
    val snapshot: String? = ""
)
