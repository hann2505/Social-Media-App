package com.example.socialmediaapp.ui.fragment.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.socialmediaapp.adapter.PostAdapter
import com.example.socialmediaapp.data.firebase.authentication.UserAuthentication
import com.example.socialmediaapp.databinding.FragmentPostBinding
import com.example.socialmediaapp.extensions.LiveDataExtensions.observeOnce
import com.example.socialmediaapp.viewmodel.LikeViewModel
import com.example.socialmediaapp.viewmodel.PostViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class PostFragment: Fragment() {

    private var _binding: FragmentPostBinding? = null
    private val binding get() = _binding!!

    private val args: PostFragmentArgs by navArgs()

    private val mPostViewModel: PostViewModel by viewModels()
    private val mLikeViewModel: LikeViewModel by viewModels()

    @Inject
    lateinit var userAuthentication: UserAuthentication

    private val postAdapter = PostAdapter()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPostBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (args.isNavigatedByNotification) {
            postAdapter.setIsNavigatedByNotification()
        }
        subscribeToRecyclerView()
        onClickListeners()
    }

    private fun subscribeToRecyclerView() {
        val recyclerView = binding.recyclerView
        recyclerView.layoutManager = LinearLayoutManager(
            requireContext(),
            LinearLayoutManager.VERTICAL,
            false
        )
        mPostViewModel.getPostWithUserAndImageByPostId(args.postId).observe(viewLifecycleOwner) {
            postAdapter.setData(it)
        }

        mLikeViewModel.getPostIdByUserId(userAuthentication.getCurrentUser()!!.uid).observe(viewLifecycleOwner) {
            postAdapter.setLikedList(it)
        }

        recyclerView.adapter = postAdapter
    }

    private fun onClickListeners() {
        postAdapter.setOnLikeClickListener { post ->
            mLikeViewModel.checkIfLiked(userAuthentication.getCurrentUser()!!.uid, post.post.postId).observeOnce(viewLifecycleOwner) {
                if (it)
                    mLikeViewModel.unlikePost(
                        userAuthentication.getCurrentUser()!!.uid,
                        post.post.postId
                    )
                else
                    mLikeViewModel.likePost(
                        userAuthentication.getCurrentUser()!!.uid,
                        post.post.postId
                    )
            }

        }

        postAdapter.setOnCommentClickListener {
            val action = PostFragmentDirections.actionPostFragmentToCommentListBottomSheetDialog(it.post.postId)
            findNavController().navigate(action)

        }

        postAdapter.setOnBackClickListener {
            findNavController().popBackStack()
        }
    }

}