package com.example.socialmediaapp.ui.acitivity

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.example.socialmediaapp.R
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

    private lateinit var imagePickerLauncher: ActivityResultLauncher<Intent>

    private val mUserViewModel: UserViewModel by viewModels()
    private val mPostViewModel: PostViewModel by viewModels()

    private var selectedImageUri: Uri? = null

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
        setSupportActionBar(binding.bottomBar.toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        imagePicker()

        binding.cancel.setOnClickListener {
            finish()
        }

    }

    private fun imagePicker() {
        imagePickerLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val imageUri: Uri? = result.data?.data
                if (imageUri != null) {
                    Glide.with(this).load(imageUri).into(binding.image)
                    selectedImageUri = imageUri
                }
            }
        }

        binding.post.setOnClickListener {
            selectedImageUri?.let { uri ->
                uploadPost(uri) // Pass Uri instead of String
                finish()
            } ?: Log.e("PostActivity", "No image selected")
        }
    }

    private fun uploadPost(imageUrl: Uri) {
        val content = binding.content.text.toString()
        val userId = userAuthentication.getCurrentUser()!!.uid
        val postState = true
        mPostViewModel.uploadPost(userId, content, imageUrl, postState)

    }



    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.post_bottom_bar, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.image -> {
                pickImage()
                return true
            }
            R.id.text -> {
                return true
            }
            R.id.add -> {
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun pickImage() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        imagePickerLauncher.launch(intent)
    }

}