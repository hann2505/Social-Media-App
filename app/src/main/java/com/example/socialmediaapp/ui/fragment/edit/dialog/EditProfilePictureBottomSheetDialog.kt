package com.example.socialmediaapp.ui.fragment.edit.dialog


import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.socialmediaapp.R
import com.example.socialmediaapp.data.firebase.authentication.UserAuthentication
import com.example.socialmediaapp.databinding.FragmentEditProfilePictureBottomSheetDialogBinding
import com.example.socialmediaapp.extensions.LiveDataExtensions.observeOnce
import com.example.socialmediaapp.viewmodel.UserViewModel
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import java.io.File
import javax.inject.Inject

@AndroidEntryPoint
class EditProfilePictureBottomSheetDialog : BottomSheetDialogFragment() {

    private var _binding: FragmentEditProfilePictureBottomSheetDialogBinding? = null
    private val binding get() = _binding!!

    private val mUserViewModel: UserViewModel by viewModels()

    @Inject
    lateinit var userAuthentication: UserAuthentication

    private var userPfp: Uri? = null
    private var setAsDefault: Boolean = false

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

        binding.navigationView.setNavigationItemSelectedListener { it ->
            when (it.itemId) {
                R.id.library -> {
                    pickImage()
                    true
                }
                R.id.camera -> {
                    openCamera()
                    true
                }
                R.id.set_default -> {
                    mUserViewModel.getUserInfoById(userAuthentication.getCurrentUser()!!.uid).observeOnce(viewLifecycleOwner) {
                        binding.userPfp.setImageResource(
                            if (it.gender)
                                R.drawable.man
                            else
                                R.drawable.woman
                        )

                    }

                    setAsDefault = true
                    true
                }
                else -> false
            }
        }

        binding.done.setOnClickListener {
            mUserViewModel.getUserInfoById(userAuthentication.getCurrentUser()!!.uid).observeOnce(viewLifecycleOwner) {
                mUserViewModel.updateProfilePicture(it.userId, it.gender, userPfp, setAsDefault)
            }
            Log.d("final userPfp Uri", "onViewCreated: $userPfp")
            dismiss()
        }

        binding.cancel.setOnClickListener {
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

    private val cameraLauncher = registerForActivityResult(ActivityResultContracts.TakePicture()) { success ->
        if (success) {
            userPfp.let {
                binding.userPfp.setImageURI(it)
            }
        }
    }


    private fun openCamera() {
        userPfp = createImageUri()
        userPfp.let { uri ->
            cameraLauncher.launch(uri)
        }
    }

    private fun createImageUri(): Uri {
        val file = File(requireContext().cacheDir, "profile_picture.jpg")
        return FileProvider.getUriForFile(requireContext(), "${requireContext().packageName}.provider", file)
    }
}

