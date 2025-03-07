package com.example.socialmediaapp.ui.fragment.edit

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.socialmediaapp.R
import com.example.socialmediaapp.data.firebase.authentication.UserAuthentication
import com.example.socialmediaapp.databinding.FragmentEditProfileBinding
import com.example.socialmediaapp.viewmodel.UserViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class EditProfileFragment : Fragment() {

    private var _binding : FragmentEditProfileBinding? = null
    private val binding get() = _binding!!

    @Inject
    lateinit var userAuthentication: UserAuthentication

    private val mUserViewModel: UserViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEditProfileBinding.inflate(inflater, container, false)
//        mUserViewModel.fetchDataFromFirebase()
        showCurrentUserInfo()

        binding.name.setOnClickListener {
            findNavController().navigate(R.id.action_editProfile_to_editNameFragment)
        }

        binding.userName.setOnClickListener {
            findNavController().navigate(R.id.action_editProfile_to_editUserNameFragment)
        }

        binding.gender.setOnClickListener {
            findNavController().navigate(R.id.action_editProfile_to_editGenderFragment)
        }

        binding.bio.setOnClickListener {
            findNavController().navigate(R.id.action_editProfile_to_editBioFragment)
        }

        binding.toolBarEdPf.backToHome.setOnClickListener {
            requireActivity().finish()
        }

        mUserViewModel.getUserInfoById(userAuthentication.getCurrentUser()!!.uid).observe(viewLifecycleOwner) { user ->
            binding.editPfp.setOnClickListener {
                val action = EditProfileFragmentDirections.actionEditProfileToEditProfilePictureBottomSheetDialog(user)
                findNavController().navigate(action)
            }
        }



        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.toolBarEdPf.title.text = getString(R.string.edit_profile)

        mUserViewModel.getUserInfoById(userAuthentication.getCurrentUser()!!.uid).observe(viewLifecycleOwner) { user ->
            Glide.with(binding.userPfp).load(user.profilePictureUrl).into(binding.userPfp)
        }

    }

    private fun showCurrentUserInfo() {
        mUserViewModel.getUserInfoById(userAuthentication.getCurrentUser()!!.uid).observe(viewLifecycleOwner) {
            binding.nameTv.text = it.name
            binding.usernameTv.text = it.username
            binding.bioTv.text = it.bio

            if (it.gender) {
                binding.genderTv.text = getString(R.string.male)
            } else {
                binding.genderTv.text = getString(R.string.female)
            }
        }
    }

}