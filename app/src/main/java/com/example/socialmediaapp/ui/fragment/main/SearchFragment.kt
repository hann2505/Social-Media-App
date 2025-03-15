package com.example.socialmediaapp.ui.fragment.main

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.socialmediaapp.adapter.PostAdapter
import com.example.socialmediaapp.adapter.UserAdapter
import com.example.socialmediaapp.data.firebase.authentication.UserAuthentication
import com.example.socialmediaapp.databinding.FragmentSearchBinding
import com.example.socialmediaapp.viewmodel.FollowerViewModel
import com.example.socialmediaapp.viewmodel.PostViewModel
import com.example.socialmediaapp.viewmodel.UserViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class SearchFragment : Fragment(), SearchView.OnQueryTextListener {
    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!

    private val userAdapter = UserAdapter()

    @Inject
    lateinit var postAdapter: PostAdapter

    private val mUserViewModel: UserViewModel by viewModels()
    private val mPostViewModel: PostViewModel by viewModels()
    private val mFollowerViewModel: FollowerViewModel by viewModels()

    @Inject
    lateinit var userAuthentication: UserAuthentication

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        onItemClickListener()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        subscribeSearchView()

    }

    override fun onStart() {
        super.onStart()
        subscribeToRecyclerView()
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        if (query.isNullOrEmpty()) {
            userAdapter.setData(emptyList())
            postAdapter.setData(emptyList())
        }
        else {
            searchData(query)
        }
        return true
    }

    override fun onQueryTextChange(newText: String?): Boolean {
        if (newText.isNullOrEmpty()) {
            userAdapter.setData(emptyList())
            postAdapter.setData(emptyList())
        }
        else {
            searchData(newText)
        }
        return true
    }

    private fun searchData(query: String) {

        mUserViewModel.searchUser(query).observe(viewLifecycleOwner) { userList ->
            userAdapter.setData(userList)
        }

        mPostViewModel.searchPostFromFirebaseByContentOrUsername(query).observe(viewLifecycleOwner) { postList ->
            Log.d("SearchFragment", "Post List: $postList")
            postAdapter.setData(postList)
        }

    }

    private fun subscribeSearchView() {
        val searchView = binding.searchView
        searchView.setOnQueryTextListener(this)
    }

    private fun subscribeToRecyclerView() {
        binding.userRecyclerView.layoutManager = LinearLayoutManager(
            requireContext(),
            LinearLayoutManager.VERTICAL,
            false
        )

        binding.postRecyclerView.layoutManager = LinearLayoutManager(
            requireContext(),
            LinearLayoutManager.VERTICAL,
            false
        )

        binding.userRecyclerView.adapter = userAdapter
        binding.postRecyclerView.adapter = postAdapter

    }

    private fun onItemClickListener() {
        userAdapter.setOnItemClickListener {
            if (userAuthentication.getCurrentUser()!!.uid == it.userId){
                val action = SearchFragmentDirections.actionSearchFragmentToProfileFragment(true)
                findNavController().navigate(action)
            }
            else {
                val action = SearchFragmentDirections.actionSearchFragmentToUserProfileFragment(it)
                findNavController().navigate(action)
            }
        }

        postAdapter.setOnItemClickListener {
//            val action = SearchFragmentDirections.actionS(it)
//            findNavController().navigate(action)
        }


    }

}