package com.example.socialmediaapp.ui.acitivity

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.socialmediaapp.R
import com.example.socialmediaapp.databinding.ActivityEditProfileBinding
import com.example.socialmediaapp.viewmodel.UserViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class EditProfileActivity : AppCompatActivity() {

    private var _binding: ActivityEditProfileBinding? = null
    private val binding get() = _binding!!

    private val mUserViewModel: UserViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        _binding = ActivityEditProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        println("EditProfileActivity: OnCreated")

    }

    override fun onStart() {
        super.onStart()
        println("EditProfileActivity: OnStart")

    }

    override fun onResume() {
        super.onResume()
        println("EditProfileActivity: OnResume")
        mUserViewModel.checkIfUserChanges()
    }

    override fun onPause() {
        super.onPause()
        println("EditProfileActivity: OnPause")
    }

    override fun onStop() {
        super.onStop()
        println("EditProfileActivity: OnStop")
    }

    override fun onDestroy() {
        super.onDestroy()
        println("EditProfileActivity: OnDestroy")
        _binding = null
    }

}