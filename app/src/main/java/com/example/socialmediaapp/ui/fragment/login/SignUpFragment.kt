package com.example.socialmediaapp.ui.fragment.login

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.socialmediaapp.R
import com.example.socialmediaapp.data.entity.User
import com.example.socialmediaapp.databinding.FragmentSignUpBinding
import com.example.socialmediaapp.data.firebase.authentication.UserAuthentication
import com.example.socialmediaapp.viewmodel.UserViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class SignUpFragment : Fragment() {

    //binding
    private var _binding: FragmentSignUpBinding? = null
    private val binding get() = _binding!!

    //User authentication
    @Inject
    lateinit var userAuthentication: UserAuthentication

    private val mUserViewModel: UserViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSignUpBinding.inflate(inflater, container, false)

        binding.signUpBtn.setOnClickListener {
            createNewUser()
        }

        binding.loginTv.setOnClickListener {
            findNavController().navigate(R.id.action_signUpFragment_to_logInFragment)
        }

        return binding.root
    }

    private fun createNewUser() {
        val email = binding.emailEdt.text.toString()
        val password = binding.pwdEdt.text.toString()
        val confirmPassword = binding.cfPwdEdt.text.toString()

        if (password == confirmPassword) {
            userAuthentication.register(email, password) {isSuccessful, uid ->
                Log.d("Create New User", "Success: $isSuccessful")
                if (isSuccessful) {
                    val user = User(
                        userId = uid,
                        email = email,
                        bio = "",
                        profilePictureUrl = ""

                    )
                    mUserViewModel.upsertUser(user)
                    findNavController().navigate(R.id.action_signUpFragment_to_logInFragment)
                }
                else {
                    binding.emailEdt.error = uid
                }
            }
        } else {
            binding.cfPwdEdt.error = getString(R.string.password_not_match)
            binding.cfPwdEdt.requestFocus()
        }
    }
}