package com.example.socialmediaapp.ui.fragment.main

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.socialmediaapp.adapter.SearchAdapter
import com.example.socialmediaapp.adapter.UserAdapter
import com.example.socialmediaapp.data.firebase.authentication.UserAuthentication
import com.example.socialmediaapp.databinding.FragmentSearchBinding
import com.example.socialmediaapp.extensions.LiveDataExtensions.observeOnce
import com.example.socialmediaapp.viewmodel.FollowerViewModel
import com.example.socialmediaapp.viewmodel.LikeViewModel
import com.example.socialmediaapp.viewmodel.PostViewModel
import com.example.socialmediaapp.viewmodel.UserViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class SearchFragment : Fragment(), SearchView.OnQueryTextListener,
    androidx.appcompat.widget.SearchView.OnQueryTextListener {
    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!

    private val searchAdapter = SearchAdapter()
    private val mUserViewModel: UserViewModel by viewModels()
    private val mPostViewModel: PostViewModel by viewModels()
    private val mFollowerViewModel: FollowerViewModel by viewModels()
    private val mLikeViewModel: LikeViewModel by viewModels()

    @Inject
    lateinit var userAuthentication: UserAuthentication

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        onItemClickListener()

        binding.recyclerView.layoutManager = LinearLayoutManager(
            requireContext(),
            LinearLayoutManager.VERTICAL,
            false
        )

        binding.recyclerView.adapter = searchAdapter

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        subscribeSearchView()
    }

    override fun onStart() {
        super.onStart()
        subscribeToObserve()
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        if (query.isNullOrEmpty()) {
            searchAdapter.setData(emptyList())
        }
        else {
            searchData(query)
        }
        return true
    }

    override fun onQueryTextChange(newText: String?): Boolean {
        if (newText.isNullOrEmpty()) {
            searchAdapter.setData(emptyList())
        }
        else {
            searchData(newText)
        }
        return true
    }

    private fun searchData(query: String) {
//        mUserViewModel.(query).observe(viewLifecycleOwner) { userList ->
//
//            Log.d("search user", "$userList")
//            mPostViewModel.searchPostFromFirebase(query).observe(viewLifecycleOwner) { postList ->
//                val combinedList = mutableListOf<Any>()
//                combinedList.addAll(userList)
//                combinedList.addAll(postList)
//                searchAdapter.setData(combinedList)
//            }
//
//        }

    }

    private fun subscribeSearchView() {
        val searchView = binding.searchView
        searchView.setOnQueryTextListener(this)
    }

    private fun subscribeToObserve() {
//        mLikeViewModel.(userAuthentication.getCurrentUser()!!.uid).observe(viewLifecycleOwner) {
//            searchAdapter.setLikedList(it)
//        }
    }

    private fun onItemClickListener() {
        searchAdapter.setOnUserItemClickListener {
            if (userAuthentication.getCurrentUser()!!.uid == it.userId){
                val action = SearchFragmentDirections.actionSearchFragmentToProfileFragment(true)
                findNavController().navigate(action)
            }
            else {
                val action = SearchFragmentDirections.actionSearchFragmentToUserProfileFragment(it)
                findNavController().navigate(action)
            }
        }

        searchAdapter.setOnLikeClickListener { post ->
//            mLikeViewModel.checkIfLiked(userAuthentication.getCurrentUser()!!.uid, post.post.postId).observeOnce(viewLifecycleOwner) {
//                if (it)
//                    mLikeViewModel.unlikePost(
//                        userAuthentication.getCurrentUser()!!.uid,
//                        post.post.postId
//                    )
//                else
//                    mLikeViewModel.likePost(
//                        userAuthentication.getCurrentUser()!!.uid,
//                        post.post.postId
//                    )
//            }
        }

        searchAdapter.setOnCommentClickListener {
            val action = SearchFragmentDirections.actionSearchFragmentToCommentListBottomSheetDialog(it.post.postId)
            findNavController().navigate(action)
        }

    }

}