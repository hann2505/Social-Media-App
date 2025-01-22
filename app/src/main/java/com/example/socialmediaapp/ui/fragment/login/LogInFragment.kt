package com.example.socialmediaapp.ui.fragment.login

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.socialmediaapp.R
import com.example.socialmediaapp.databinding.FragmentLogInBinding
import com.example.socialmediaapp.data.firebase.authentication.UserAuthentication
import com.example.socialmediaapp.ui.acitivity.MainActivity
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class LogInFragment : Fragment() {
    //binding
    private var _binding: FragmentLogInBinding? = null
    private val binding get() = _binding!!

    //User Authentication
    @Inject
    lateinit var userAuthentication: UserAuthentication

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLogInBinding.inflate(inflater, container, false)

        binding.signInBtn.setOnClickListener {
            logIn()
        }

        binding.signUpBtn.setOnClickListener {
            findNavController().navigate(R.id.action_logInFragment_to_signUpFragment)
        }

        binding.signInBtn.setOnClickListener {
            logIn()
        }

        binding.forgetPwdTv.setOnClickListener {
            findNavController().navigate(R.id.action_logInFragment_to_forgetPasswordFragment)
        }

        return binding.root
    }

    private fun logIn() {
        val email = binding.emailEdt.text.toString()
        val password = binding.pwdEdt.text.toString()
        userAuthentication.login(email, password) {
            Log.d("new_user", "createNewUser: $it")
            if (it) {
                startActivity(Intent(requireContext(), MainActivity::class.java))
                requireActivity().finish()
            } else {
                binding.pwdEdt.error = "Wrong email or password"
            }
        }
    }

}