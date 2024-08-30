package com.example.todothree.googleauth

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class SigninViewModel : ViewModel() {
    private val _state = MutableStateFlow(SignedInState())
    val state = _state.asStateFlow()

    fun onSignInResult(result: SigningInResult) {
        _state.update {
            it.copy(
                isSigningInSuffessful = result.data != null,
                isSigningInError = result.errorMessage
            )
        }
    }

    fun resetState() {
        _state.update { SignedInState() }
    }
}
