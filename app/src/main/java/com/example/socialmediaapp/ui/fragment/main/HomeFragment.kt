package com.example.socialmediaapp.ui.fragment.main

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.viewModels
import androidx.fragment.app.viewModels
import com.example.socialmediaapp.R
import com.example.socialmediaapp.data.entity.User
import com.example.socialmediaapp.viewmodel.FollowerViewModel
import com.example.socialmediaapp.viewmodel.PostViewModel
import com.example.socialmediaapp.viewmodel.UserViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeFragment : Fragment() {

    private val mUserViewModel: UserViewModel by viewModels()
    private val mPostViewModel: PostViewModel by viewModels()
    private val mFollowerViewModel: FollowerViewModel by viewModels()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        println("HomeFragment: OnCreate")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        println("HomeFragment: OnViewCreated")
        mUserViewModel.fetchDataFromFirebase()
        mPostViewModel.fetchDataFromFirebase()
        mFollowerViewModel.fetchDataFromFirebase()
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
}