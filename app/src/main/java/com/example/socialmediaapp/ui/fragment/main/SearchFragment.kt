package com.example.socialmediaapp.ui.fragment.main

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.socialmediaapp.adapter.UserAdapter
import com.example.socialmediaapp.data.firebase.authentication.UserAuthentication
import com.example.socialmediaapp.databinding.FragmentSearchBinding
import com.example.socialmediaapp.viewmodel.UserViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class SearchFragment : Fragment(), SearchView.OnQueryTextListener,
    androidx.appcompat.widget.SearchView.OnQueryTextListener {
    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!

//    private lateinit var userAdapter: UserAdapter
    private val userAdapter = UserAdapter()
    private val mUserViewModel: UserViewModel by viewModels()

    @Inject
    lateinit var userAuthentication: UserAuthentication

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        subscribeSearchView()

        binding.recyclerView.layoutManager = LinearLayoutManager(
            requireContext(),
            LinearLayoutManager.VERTICAL,
            false
        )
//        userAdapter = UserAdapter()

        userAdapter.setOnItemClickListener {
            val action = SearchFragmentDirections.actionSearchFragmentToUserProfileFragment(it)
            findNavController().navigate(action)
        }

        binding.recyclerView.adapter = userAdapter

        return binding.root
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        if (query != null) {
            println("Text changed: $query")
        }
        return true
    }

    override fun onQueryTextChange(newText: String?): Boolean {
        if (newText.isNullOrEmpty()) {
            userAdapter.setData(emptyList())
        }
        else {
            searchData(newText)
        }
        return true
    }

    private fun searchData(query: String) {
        mUserViewModel.getUserByUsername(query).observe(viewLifecycleOwner) { userList ->
            Log.d("search user", "$userList")
            userAdapter.setData(userList)
        }

    }

    private fun subscribeSearchView() {
        val searchView = binding.searchView
        searchView.setOnQueryTextListener(this)
    }
}