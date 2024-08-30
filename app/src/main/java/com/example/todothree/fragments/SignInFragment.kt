package com.example.todothree.fragments

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.example.todothree.R
import com.example.todothree.googleauth.googleAuth
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

class SigninFragment : Fragment() {

    private lateinit var navController: NavController
    private lateinit var googleAuth: googleAuth
    private lateinit var signInClient: SignInClient

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_signin, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        navController = Navigation.findNavController(view)
        signInClient = Identity.getSignInClient(requireContext())
        googleAuth = googleAuth(requireContext(), signInClient)

        view.findViewById<View>(R.id.signupalready).setOnClickListener {
            startSignIn()
        }
    }

    private fun startSignIn() {
        lifecycleScope.launch {
            val intentSender = googleAuth.signingIn()
            intentSender?.let {
                startIntentSenderForResult(it, SIGN_IN_REQUEST_CODE, null, 0, 0, 0, null)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == SIGN_IN_REQUEST_CODE) {
            lifecycleScope.launch {
                val result = googleAuth.signingInWithIntent(data!!)
                if (result.data != null) {
                    navController.navigate(R.id.action_signinFragment_to_homeFragment)
                } else {
                    Toast.makeText(context, result.errorMessage, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    companion object {
        const val SIGN_IN_REQUEST_CODE = 1001
    }
}
