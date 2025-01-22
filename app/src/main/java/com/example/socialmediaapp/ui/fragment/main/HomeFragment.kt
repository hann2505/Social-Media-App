package com.example.socialmediaapp.ui.fragment.main

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.viewModels
import androidx.fragment.app.viewModels
import com.example.socialmediaapp.R
import com.example.socialmediaapp.data.entity.User
import com.example.socialmediaapp.viewmodel.UserViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeFragment : Fragment() {

    private val mUserViewModel: UserViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val user = User(
            userId = "1",
            email = "1",
            bio = "1",
            profilePictureUrl = "1"
        )

        mUserViewModel.addUser(user)

        return inflater.inflate(R.layout.fragment_home, container, false)
    }
}