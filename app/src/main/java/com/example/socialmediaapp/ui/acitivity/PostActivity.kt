package com.example.socialmediaapp.ui.acitivity

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.socialmediaapp.R
import com.example.socialmediaapp.data.entity.MediaType
import com.example.socialmediaapp.data.entity.Post
import com.example.socialmediaapp.data.firebase.authentication.UserAuthentication
import com.example.socialmediaapp.databinding.ActivityPostBinding
import com.example.socialmediaapp.viewmodel.PostViewModel
import com.example.socialmediaapp.viewmodel.UserViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class PostActivity : AppCompatActivity() {

    private var _binding: ActivityPostBinding?= null
    private val binding get() = _binding!!

    private val mUserViewModel: UserViewModel by viewModels()
    private val mPostViewModel: PostViewModel by viewModels()

    @Inject
    lateinit var userAuthentication: UserAuthentication

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        _binding = ActivityPostBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.cancel.setOnClickListener {
            finish()
        }

        binding.post.setOnClickListener {
            uploadPost()
            finish()
        }
    }

    private fun uploadPost() {
        val userId = userAuthentication.getCurrentUser()!!.uid
        val content = binding.content.text.toString()
        val imageUrl = ""
        val mediaUrl = ""
        val postState = true
        val timestamp = System.currentTimeMillis()

        mPostViewModel.uploadPost(userId, content, imageUrl, mediaUrl, postState, timestamp)

    }

}