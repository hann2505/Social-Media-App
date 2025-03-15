package com.example.socialmediaapp.ui.fragment.chat

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.socialmediaapp.adapter.UserChatAdapter
import com.example.socialmediaapp.databinding.FragmentChatBinding
import com.example.socialmediaapp.viewmodel.UserViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ChatFragment : Fragment(), SearchView.OnQueryTextListener {

    private var _binding: FragmentChatBinding? = null
    private val binding get() = _binding!!

    private val userChatAdapter = UserChatAdapter()

    private val mUserViewModel: UserViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentChatBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.searchView.setOnQueryTextListener(this)
        setUpRecyclerView()

        userChatAdapter.setOnItemClickListener {
            val action = ChatFragmentDirections.actionChatFragmentToChatRoomFragment(it)
            findNavController().navigate(action)
        }
    }

    private fun setUpRecyclerView() {
        binding.recyclerView.layoutManager = LinearLayoutManager(
            requireContext(),
            LinearLayoutManager.VERTICAL,
            false
        )
        binding.recyclerView.adapter = userChatAdapter
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        if (query.isNullOrEmpty()) {
            userChatAdapter.setData(emptyList())
        } else {
            searchData(query)
        }
        return true
    }

    override fun onQueryTextChange(newText: String?): Boolean {
        if (newText.isNullOrEmpty()) {
            userChatAdapter.setData(emptyList())
        } else {
            searchData(newText)

        }
        return true
    }

    private fun searchData(query: String) {
        mUserViewModel.searchUser(query).observe(viewLifecycleOwner) { userList ->
            userChatAdapter.setData(userList)
        }

    }

}