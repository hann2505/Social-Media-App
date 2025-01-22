package com.example.socialmediaapp.ui.acitivity

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.socialmediaapp.R
import com.example.socialmediaapp.databinding.ActivitySettingBinding
import com.example.socialmediaapp.data.firebase.authentication.UserAuthentication
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class SettingActivity : AppCompatActivity() {
    //binding
    private var _binding: ActivitySettingBinding? = null
    private val binding get() = _binding!!

    //authentication
    @Inject
    lateinit var userAuthentication: UserAuthentication
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        _binding = ActivitySettingBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        setSupportActionBar(binding.settingToolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_back)

        binding.backToHome.setOnClickListener {
            backToProfile()
        }

        binding.logOut.setOnClickListener {
            logOut()
        }
    }

    private fun backToProfile() {
        onBackPressedDispatcher.onBackPressed()
    }

    private fun logOut() {
        userAuthentication.signOut()
        val intent = Intent(this, SignInActivity::class.java)
        startActivity(intent)
        finish()
    }

}