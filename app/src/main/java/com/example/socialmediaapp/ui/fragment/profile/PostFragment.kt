package com.example.socialmediaapp.ui.fragment.profile

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.socialmediaapp.R
import com.example.socialmediaapp.adapter.PostAdapter
import com.example.socialmediaapp.data.firebase.authentication.UserAuthentication
import com.example.socialmediaapp.databinding.FragmentPostBinding
import com.example.socialmediaapp.extensions.LiveDataExtensions.observeOnce
import com.example.socialmediaapp.ui.fragment.main.ProfileFragmentDirections
import com.example.socialmediaapp.viewmodel.LikeViewModel
import com.example.socialmediaapp.viewmodel.PostViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class PostFragment : Fragment() {

    private var _binding: FragmentPostBinding? = null
    private val binding get() = _binding!!

    private val postAdapter = PostAdapter()

    @Inject
    lateinit var userAuthentication: UserAuthentication

    private val mPostViewModel: PostViewModel by viewModels()
    private val mLikeViewModel: LikeViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPostBinding.inflate(inflater, container, false)

        mPostViewModel.fetchDataFromFirebase()
        subscribeToObservers()
        onLikeClickListener()

        postAdapter.setOnCommentClickListener {
            val action = ProfileFragmentDirections.actionProfileFragmentToCommentListBottomSheetDialog(it.post.postId)
            requireActivity().findNavController(R.id.nav_host_fragment).navigate(action)
        }

        return binding.root
    }

    private fun onLikeClickListener() {
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
    }

    private fun subscribeToObservers() {
        binding.recyclerView.adapter = postAdapter

        binding.recyclerView.layoutManager = LinearLayoutManager(
            requireContext(),
            LinearLayoutManager.VERTICAL,
            false
        )

        binding.recyclerView.isNestedScrollingEnabled = false

        mPostViewModel.getPostWithUserAndImage(userAuthentication.getCurrentUser()!!.uid).observe(viewLifecycleOwner) { posts ->
            posts.forEach {
                Log.d("profile fragment", "postId: ${it.post.postId} and likes: ${it.postLike.size}")
            }
            postAdapter.setData(posts)
        }

        mLikeViewModel.getPostIdByUserId(userAuthentication.getCurrentUser()!!.uid).observe(viewLifecycleOwner) {
            postAdapter.setLikedList(it)
        }

    }

}