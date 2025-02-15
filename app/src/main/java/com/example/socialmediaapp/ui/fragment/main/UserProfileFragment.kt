package com.example.socialmediaapp.ui.fragment.main

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.socialmediaapp.R
import com.example.socialmediaapp.adapter.PostAdapter
import com.example.socialmediaapp.data.entity.User
import com.example.socialmediaapp.data.firebase.authentication.UserAuthentication
import com.example.socialmediaapp.databinding.FragmentUserProfileBinding
import com.example.socialmediaapp.ui.fragment.main.FollowState.FOLLOWING
import com.example.socialmediaapp.viewmodel.FollowerViewModel
import com.example.socialmediaapp.viewmodel.PostViewModel
import com.example.socialmediaapp.viewmodel.UserViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class UserProfileFragment : Fragment() {

    private var _binding: FragmentUserProfileBinding? = null
    private val binding get() = _binding!!

    private lateinit var followState: FollowState

    private val mUserViewModel: UserViewModel by viewModels()
    private val mFollowerViewModel: FollowerViewModel by viewModels()
    private val mPostViewModel: PostViewModel by viewModels()
    @Inject
    lateinit var userAuthentication: UserAuthentication

    private val args: UserProfileFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentUserProfileBinding.inflate(inflater, container, false)
        showUserInfo(args.user)

        binding.backBtn.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.followBtn.setOnClickListener {
            followUser()
        }

        subscribeToRecyclerView()

        return binding.root
    }

    private fun showUserInfo(user: User) {
        binding.userName.text = user.username
        binding.name.text = user.name
        binding.userBio.text = user.bio
        Glide.with(binding.userPfp).load(user.profilePictureUrl).into(binding.userPfp)

        getFollowInfo(user.userId)

    }

    private fun followUser() {
        val isFollowing = followState == FOLLOWING
        if (isFollowing) {
            mFollowerViewModel.unfollowUser(userAuthentication.getCurrentUser()!!.uid, args.user.userId)
        }
        else {
            mFollowerViewModel.followUser(userAuthentication.getCurrentUser()!!.uid, args.user.userId)
        }
    }

    private fun getFollowInfo(userId: String) {
        mFollowerViewModel.getFollowersOfAnUser(userId).observe(viewLifecycleOwner) {
            binding.followersNumber.text = it.size.toString()
        }
        mFollowerViewModel.getFollowingOfAnUser(userId).observe(viewLifecycleOwner) {
            binding.followingNumber.text = it.size.toString()
        }
        mPostViewModel.getPostWithUserByUserId(userId).observe(viewLifecycleOwner) {
            binding.postNumber.text = it.size.toString()
        }
        mFollowerViewModel.checkIfFollowing(userAuthentication.getCurrentUser()!!.uid, userId)
            .observe(viewLifecycleOwner) {
                if (it > 0) {
                    binding.followBtn.text = getString(R.string.followed)
                    followState = FOLLOWING
                } else {
                    binding.followBtn.text = getString(R.string.follow)
                    followState = FollowState.NOT_FOLLOWING
                }
            }
    }

    private fun subscribeToRecyclerView() {
        val adapter = PostAdapter()
        binding.recyclerView.adapter = adapter

        binding.recyclerView.layoutManager = LinearLayoutManager(
            requireContext(),
            LinearLayoutManager.VERTICAL,
            false
        )

        mPostViewModel.getPostWithUserByUserId(args.user.userId).observe(viewLifecycleOwner) {
            adapter.setData(it)
        }
    }
}

enum class FollowState {
    FOLLOWING,
    NOT_FOLLOWING,
    NOT_DETECTED
}