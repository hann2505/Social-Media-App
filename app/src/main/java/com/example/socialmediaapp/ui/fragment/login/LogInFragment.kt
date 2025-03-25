package com.example.socialmediaapp.ui.fragment.login

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.socialmediaapp.R
import com.example.socialmediaapp.data.firebase.authentication.GoogleSignInListener
import com.example.socialmediaapp.data.firebase.authentication.GoogleSignInHelper
import com.example.socialmediaapp.databinding.FragmentLogInBinding
import com.example.socialmediaapp.data.firebase.authentication.UserAuthentication
import com.example.socialmediaapp.ui.acitivity.MainActivity
import com.example.socialmediaapp.viewmodel.UserViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class LogInFragment : Fragment(), GoogleSignInListener {
    //binding
    private var _binding: FragmentLogInBinding? = null
    private val binding get() = _binding!!

    //User Authentication
    @Inject
    lateinit var userAuthentication: UserAuthentication

    @Inject
    lateinit var firebaseAuth: FirebaseAuth

    private val mUserViewModel: UserViewModel by viewModels()

    @Inject
    lateinit var googleSignInHelper: GoogleSignInHelper

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLogInBinding.inflate(inflater, container, false)

        googleSignInHelper.init(this, this)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.signInBtn.setOnClickListener {
            logIn()
        }

        binding.signUpBtn.setOnClickListener {
            findNavController().navigate(R.id.action_logInFragment_to_signUpFragment)
        }

        binding.forgetPwdTv.setOnClickListener {
            findNavController().navigate(R.id.action_logInFragment_to_forgetPasswordFragment)
        }

        binding.googleSignInBtn.setOnClickListener {
            googleSignInHelper.signIn(this)
//            signInWithGoogle()
        }

    }

    private fun logIn() {
        val email = binding.emailEdt.text.toString()
        val password = binding.pwdEdt.text.toString()
        userAuthentication.login(email, password) {
            Log.d("new_user", "createNewUser: $it")
            if (it) {
                mUserViewModel.saveFcmTokenToFirebase()
                startActivity(Intent(requireContext(), MainActivity::class.java))
                requireActivity().finish()
            } else {
                binding.pwdEdt.error = "Wrong email or password"
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        googleSignInHelper.handleSignInResult(requestCode, data)
    }

    override fun onSignInSuccess(user: FirebaseUser?) {
        Log.d("new_user", "createNewUser: $user")
        Toast.makeText(requireContext(), "Welcome ${user?.displayName}", Toast.LENGTH_SHORT).show()
        startActivity(Intent(requireContext(), MainActivity::class.java))
        requireActivity().finish()
    }

    override fun onSignInFailure(errorMessage: String) {
        Log.d("new_user", "createNewUser: $errorMessage")
        Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_SHORT).show()
    }

//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data)
//        if (requestCode == RC_SIGN_IN) {
//            Log.d("activity result", "$data")
//            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
//            try {
//                // Google Sign-In was successful, authenticate with Firebase
//                val account = task.getResult(ApiException::class.java)
//                Log.d("activity result", "${account.id}")
//                firebaseAuthWithGoogle(account.idToken!!)
//            } catch (e: ApiException) {
//                // Google Sign-In failed
//                Log.w("login", "Google sign in failed: ${e.message}", e)
//            }
//        }
//    }
//
//    private fun signInWithGoogle() {
//        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
//            .requestIdToken(getString(R.string.default_web_client_id))
//            .requestEmail()
//            .build()
//
//        googleSignInClient = GoogleSignIn.getClient(requireActivity(), gso)
//        // Sign out the user before signing in
//        //If a user is already signed in, Google may automatically use the last signed-in account.
//        // To ensure the account chooser appears, sign out the user before you start the sign-in process:
//
//        googleSignInClient.signOut().addOnCompleteListener {
//            val signInIntent = googleSignInClient.signInIntent
//            startActivityForResult(signInIntent, RC_SIGN_IN)
//        }
//    }
//
//    private fun firebaseAuthWithGoogle(idToken: String) {
//        val credential = GoogleAuthProvider.getCredential(idToken, null)
//        Log.d("activity result", "${credential}")
//        firebaseAuth.signInWithCredential(credential)
//            .addOnCompleteListener { task ->
//                if (task.isSuccessful) {
//                    Log.w("login", "Authentication successed")
//                    startActivity(Intent(requireContext(), MainActivity::class.java))
//                    requireActivity().finish()
//
//                } else {
//                    Log.w("login", "Authentication failed")
//                }
//            }
//    }
//
//    companion object {
//        private const val RC_SIGN_IN = 9001
//    }
//
//    private lateinit var googleSignInClient: GoogleSignInClient


}