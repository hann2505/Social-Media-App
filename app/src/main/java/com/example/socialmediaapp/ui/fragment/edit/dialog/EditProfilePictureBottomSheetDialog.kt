package com.example.socialmediaapp.ui.fragment.edit.dialog

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.viewModels
import com.example.socialmediaapp.R
import com.example.socialmediaapp.data.firebase.authentication.UserAuthentication
import com.example.socialmediaapp.databinding.FragmentEditProfilePictureBottomSheetDialogBinding
import com.example.socialmediaapp.viewmodel.UserViewModel
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class EditProfilePictureBottomSheetDialog : BottomSheetDialogFragment() {

    private var _binding: FragmentEditProfilePictureBottomSheetDialogBinding? = null
    private val binding get() = _binding!!

    private val mUserViewModel: UserViewModel by viewModels()

    @Inject
    lateinit var userAuthentication: UserAuthentication

    private lateinit var userPfp: Uri

    private lateinit var imagePickerLauncher: ActivityResultLauncher<Intent>


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEditProfilePictureBottomSheetDialogBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.navigationView.setNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.library -> {
                    pickImage()
                    true
                }
                R.id.camera -> {
                    true
                }
                R.id.bookmarkFragment -> {
                    true
                }
                else -> false
            }
        }

        binding.done.setOnClickListener {
            mUserViewModel.updateProfilePicture(userAuthentication.getCurrentUser()!!.uid, userPfp)
            dismiss()
        }
    }

    private val pickImageLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            userPfp = uri
            binding.userPfp.setImageURI(it)
        }
    }

    private fun pickImage() {
        pickImageLauncher.launch("image/*")
    }
}