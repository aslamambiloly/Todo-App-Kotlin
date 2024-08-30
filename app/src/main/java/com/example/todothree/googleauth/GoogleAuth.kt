package com.example.todothree.googleauth

import android.content.Context
import android.content.Intent
import android.content.IntentSender
import com.example.todothree.R
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.BeginSignInRequest.GoogleIdTokenRequestOptions
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.firebase.Firebase
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.auth
import kotlinx.coroutines.tasks.await
import java.util.concurrent.CancellationException

class googleAuth(
    private val context: Context,
    private val oneTapClient: SignInClient
){
    private val auth= Firebase.auth
    suspend fun signingIn():IntentSender?{
        val result=try {
            oneTapClient.beginSignIn(
                buildSigningInRequest()
            ).await()
        }catch (e: Exception){
            e.printStackTrace()
            if (e is CancellationException) throw e
            null
        }
        return result?.pendingIntent?.intentSender
    }

    suspend fun signingInWithIntent(intent: Intent): SigningInResult {
        val credential=oneTapClient.getSignInCredentialFromIntent(intent)
        val googleIdToken=credential.googleIdToken
        val googleCredentials= GoogleAuthProvider.getCredential(googleIdToken,null)
        return try {
            val user=auth.signInWithCredential(googleCredentials).await().user
            SigningInResult(
                data=user?.run{
                    UserData(
                        userId = uid,
                        userName = displayName,
                        profilePictureURL = photoUrl?.toString()
                    )
                },
                errorMessage = null
            )
        }catch (e: Exception){
            e.printStackTrace()
            if (e is CancellationException) throw e
            SigningInResult(
                data=null,
                errorMessage = e.message
            )
        }
    }
    suspend fun signOut(){
        try {
            oneTapClient.signOut().await()
            auth.signOut()
        }catch (e:Exception){
            e.printStackTrace()
            if (e is CancellationException) throw e
        }
    }

    fun getSignedInUser(): UserData?=auth.currentUser?.run {
        UserData(
            userId = uid,
            userName = displayName,
            profilePictureURL = photoUrl?.toString()
        )
    }

    private fun buildSigningInRequest():BeginSignInRequest{
        return BeginSignInRequest.Builder()
            .setGoogleIdTokenRequestOptions(
                GoogleIdTokenRequestOptions.builder()
                    .setSupported(true)
                    .setFilterByAuthorizedAccounts(false)
                    .setServerClientId(context.getString(R.string.default_web_Id))
                    .build()
            )
            .setAutoSelectEnabled(true)
            .build()
    }

}