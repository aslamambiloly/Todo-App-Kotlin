package com.example.todothree.googleauth

data class SigningInResult(
    val data: UserData?,
    val errorMessage:String?
)
data class UserData(
    val userId:String,
    val userName:String?,
    val profilePictureURL:String?
)
