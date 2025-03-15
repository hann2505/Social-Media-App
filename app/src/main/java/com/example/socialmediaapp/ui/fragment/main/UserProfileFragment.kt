package com.example.socialmediaapp.ui.fragment.main

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.socialmediaapp.R
import com.example.socialmediaapp.adapter.PostAdapter
import com.example.socialmediaapp.data.entity.User
import com.example.socialmediaapp.data.firebase.authentication.UserAuthentication
import com.example.socialmediaapp.databinding.FragmentUserProfileBinding
import com.example.socialmediaapp.viewmodel.FollowerViewModel
import com.example.socialmediaapp.viewmodel.PostViewModel
import com.example.socialmediaapp.viewmodel.UserViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class UserProfileFragment : Fragment() {

    private var _binding: FragmentUserProfileBinding? = null
    private val binding get() = _binding!!

    private val mUserViewModel: UserViewModel by viewModels()
    private val mFollowerViewModel: FollowerViewModel by viewModels()
    private val mPostViewModel: PostViewModel by viewModels()

    @Inject
    lateinit var adapter: PostAdapter

    @Inject
    lateinit var userAuthentication: UserAuthentication

    private val args: UserProfileFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentUserProfileBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        showUserInfo(args.user)

        mPostViewModel.getPostCountUpdate(args.user.userId)
        mFollowerViewModel.getFollowingCount(args.user.userId)
        mFollowerViewModel.getFollowerCount(args.user.userId)
        mFollowerViewModel.checkIfFollowing(userAuthentication.getCurrentUser()!!.uid, args.user.userId)


        binding.swipeRefreshLayout.setOnRefreshListener {

            binding.swipeRefreshLayout.isRefreshing = false
        }

        mPostViewModel.postCount.observe(viewLifecycleOwner) {
            binding.postNumber.text = it.toString()
        }

        lifecycleScope.launch {
            mFollowerViewModel.followState.collect { isFollowing ->
                if (isFollowing) {
                    binding.followBtn.text = getString(R.string.following)
                    binding.followBtn.setTextColor(resources.getColor(R.color.black))
                    binding.followBtn.setBackgroundResource(R.drawable.custom_followed_button)
                } else {
                    binding.followBtn.text = getString(R.string.follow)
                    binding.followBtn.setTextColor(resources.getColor(R.color.white))
                    binding.followBtn.setBackgroundResource(R.drawable.custom_follow_button)

                }
                binding.followBtn.setOnClickListener {
                    followUser(isFollowing)
                }
            }
        }

        mFollowerViewModel.followingCount.observe(viewLifecycleOwner) {
            binding.followingNumber.text = it.toString()
        }

        mFollowerViewModel.followerCount.observe(viewLifecycleOwner) {
            binding.followersNumber.text = it.toString()
        }

        adapter.setOnCommentClickListener {
            val action = UserProfileFragmentDirections.actionUserProfileFragmentToCommentListBottomSheetDialog(it.userId, it.postId)
            findNavController().navigate(action)
        }

        onLikeClickListener()
        subscribeToRecyclerView()

        binding.backBtn.setOnClickListener {
            findNavController().popBackStack()
        }

    }



    private fun onLikeClickListener() {

    }

    private fun showUserInfo(user: User) {
        binding.userName.text = user.username
        binding.name.text = user.name
        binding.userBio.text = user.bio
        Glide.with(binding.userPfp).load(user.profilePictureUrl).into(binding.userPfp)

        getFollowInfo(user.userId)

    }

    private fun followUser(isFollowing: Boolean) {
        if (isFollowing) {
            mFollowerViewModel.unfollowUser(userAuthentication.getCurrentUser()!!.uid, args.user.userId)
        }
        else {
            mFollowerViewModel.followUser(userAuthentication.getCurrentUser()!!.uid, args.user.userId)

        }
    }

    private fun getFollowInfo(userId: String) {
    }

    private fun subscribeToRecyclerView() {
        binding.recyclerView.adapter = adapter

        binding.recyclerView.layoutManager = LinearLayoutManager(
            requireContext(),
            LinearLayoutManager.VERTICAL,
            false
        )

        mPostViewModel.fetchPostByUserId(args.user.userId).observe(viewLifecycleOwner) { postsList ->
            adapter.setData(postsList)
        }

//        mLikeViewModel.fe(userAuthentication.getCurrentUser()!!.uid).observe(viewLifecycleOwner) {
//            adapter.setLikedList(it)
//        }
    }
}