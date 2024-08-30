package com.example.todothree.googleauth

data class SignedInState(
    val isSigningInSuffessful:Boolean=false,
    val isSigningInError:String?=null
)
