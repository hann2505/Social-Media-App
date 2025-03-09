package com.example.socialmediaapp.ui.fragment.main

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.socialmediaapp.adapter.PostAdapter
import com.example.socialmediaapp.data.firebase.authentication.UserAuthentication
import com.example.socialmediaapp.databinding.FragmentHomeBinding
import com.example.socialmediaapp.extensions.LiveDataExtensions.observeOnce
import com.example.socialmediaapp.viewmodel.FollowerViewModel
import com.example.socialmediaapp.viewmodel.LikeViewModel
import com.example.socialmediaapp.viewmodel.PostViewModel
import com.example.socialmediaapp.viewmodel.UserViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class HomeFragment : Fragment() {


    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val mUserViewModel: UserViewModel by viewModels()
    private val mPostViewModel: PostViewModel by viewModels()
    private val mFollowerViewModel: FollowerViewModel by viewModels()
    private val mLikeViewModel: LikeViewModel by viewModels()

    @Inject
    lateinit var userAuthentication: UserAuthentication

    private val postAdapter = PostAdapter()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        println("HomeFragment: OnCreate")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        println("HomeFragment: OnViewCreated")
        setUpRecyclerView()
        onClickListener()
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        println("HomeFragment: OnViewStateRestored")
    }

    override fun onStart() {
        super.onStart()
        println("HomeFragment: OnStart")
    }

    override fun onResume() {
        super.onResume()
        println("HomeFragment: OnResume")
    }

    override fun onPause() {
        super.onPause()
        println("HomeFragment: OnPause")
    }

    override fun onStop() {
        super.onStop()
        println("HomeFragment: OnStop")
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        println("HomeFragment: OnSaveInstanceState")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        println("HomeFragment: OnDestroyView")
    }

    override fun onDestroy() {
        super.onDestroy()
        println("HomeFragment: OnDestroy")
    }

    override fun onDetach() {
        super.onDetach()
        println("HomeFragment: OnDetach")
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        println("HomeFragment: OnAttach")
    }

    private fun setUpRecyclerView() {
        binding.postRecyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        }

        mPostViewModel.getPostWithUserAndImageOnNewFeed().observe(viewLifecycleOwner) {
            postAdapter.setData(it)
            binding.postRecyclerView.adapter = postAdapter
        }

        mLikeViewModel.getPostIdByUserId(userAuthentication.getCurrentUser()!!.uid).observe(viewLifecycleOwner) {
            postAdapter.setLikedList(it)
        }
    }

    private fun onClickListener() {
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
            val action = HomeFragmentDirections.actionHomeFragmentToCommentListBottomSheetDialog(it.post.postId)
            findNavController().navigate(action)
        }
    }
}