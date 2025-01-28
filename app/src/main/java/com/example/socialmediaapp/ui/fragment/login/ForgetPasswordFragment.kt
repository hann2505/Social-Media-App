package com.example.socialmediaapp.ui.fragment.login

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.socialmediaapp.R
import com.example.socialmediaapp.databinding.FragmentForgetPasswordBinding
import com.example.socialmediaapp.data.firebase.authentication.UserAuthentication
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class ForgetPasswordFragment : Fragment() {

    private var _binding: FragmentForgetPasswordBinding? = null
    private val binding get() = _binding!!

    //User authentication
    @Inject
    lateinit var userAuthentication: UserAuthentication

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentForgetPasswordBinding.inflate(inflater, container, false)

        binding.backToLogInTv.setOnClickListener {
            findNavController().navigate(R.id.action_forgetPasswordFragment_to_logInFragment)
        }

        binding.resetPwdBtn.setOnClickListener {
            resetPassword()
        }

        return binding.root
    }

    private fun resetPassword() {
        val email = binding.emailEdt.text.toString()
        userAuthentication.resetPassword(email) {
            if (it) {
                findNavController().navigate(R.id.action_forgetPasswordFragment_to_logInFragment)
            }
        }
    }
}