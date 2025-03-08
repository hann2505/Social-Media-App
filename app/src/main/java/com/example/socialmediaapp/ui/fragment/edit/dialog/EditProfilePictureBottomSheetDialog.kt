package com.example.socialmediaapp.ui.fragment.edit.dialog

import android.app.Activity
import android.app.Activity.RESULT_OK
import android.app.Dialog
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.socialmediaapp.R
import com.example.socialmediaapp.data.firebase.authentication.UserAuthentication
import com.example.socialmediaapp.databinding.FragmentEditProfilePictureBottomSheetDialogBinding
import com.example.socialmediaapp.extensions.LiveDataExtensions.observeOnce
import com.example.socialmediaapp.viewmodel.UserViewModel
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import java.io.File
import javax.inject.Inject

@AndroidEntryPoint
class EditProfilePictureBottomSheetDialog : Fragment() {

    private var _binding: FragmentEditProfilePictureBottomSheetDialogBinding? = null
    private val binding get() = _binding!!

    private val mUserViewModel: UserViewModel by viewModels()

    @Inject
    lateinit var userAuthentication: UserAuthentication

    private lateinit var userPfp: Uri
    private lateinit var userPfpBitmap: Bitmap

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEditProfilePictureBottomSheetDialogBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mUserViewModel.getUserInfoById(userAuthentication.getCurrentUser()!!.uid).observeOnce(viewLifecycleOwner) {
            Glide.with(this).load(it.profilePictureUrl).into(binding.userPfp)
        }

        binding.navigationView.setNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.library -> {
                    pickImage()
                    true
                }
                R.id.camera -> {
                    openCamera()
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
            Log.d("final userPfp Uri", "onViewCreated: $userPfp")
            findNavController().popBackStack()
        }

        binding.cancel.setOnClickListener {
            findNavController().popBackStack()
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

    private val cameraLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == RESULT_OK) {
            val imageBitmap = result.data!!.extras!!.get("data") as Bitmap
            binding.userPfp.setImageBitmap(imageBitmap) // Set the captured image to an ImageView
            userPfp = createImageUri(imageBitmap)
        }
    }


    private fun openCamera() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        cameraLauncher.launch(intent)
    }

    private fun createImageUri(bitmap: Bitmap): Uri {
        val file = File(requireContext().cacheDir, "profile_picture.jpg")
        file.outputStream().use { outputStream ->
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream) // Save bitmap to file
        }
        return FileProvider.getUriForFile(requireContext(), "${requireContext().packageName}.provider", file)
    }
}

