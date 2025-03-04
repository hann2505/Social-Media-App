package com.example.socialmediaapp.ui.fragment.main

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.socialmediaapp.R
import com.example.socialmediaapp.adapter.NotificationAdapter
import com.example.socialmediaapp.data.firebase.authentication.UserAuthentication
import com.example.socialmediaapp.databinding.FragmentNotificationBinding
import com.example.socialmediaapp.viewmodel.PostViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class NotificationFragment : Fragment() {

    private var _binding: FragmentNotificationBinding? = null
    private val binding get() = _binding!!

    @Inject
    lateinit var userAuthentication: UserAuthentication

    private val notificationAdapter = NotificationAdapter()

    private val nPostViewModel: PostViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNotificationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val earlyRecyclerView = binding.earlyNotificationRecyclerview
        val newRecyclerView = binding.newNotificationRecyclerview

        earlyRecyclerView.layoutManager = LinearLayoutManager(
            requireContext(),
            LinearLayoutManager.VERTICAL,
            false
        )

        nPostViewModel.getNotificationByUserId(userAuthentication.getCurrentUser()!!.uid).observe(viewLifecycleOwner) {
            notificationAdapter.updateList(it)
        }

        earlyRecyclerView.adapter = notificationAdapter
//        newRecyclerView.adapter = notificationAdapter
    }
}