package com.example.socialmediaapp.ui.fragment.main

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.viewModels
import com.bumptech.glide.Glide
import com.example.socialmediaapp.R
import com.example.socialmediaapp.data.firebase.authentication.UserAuthentication
import com.example.socialmediaapp.databinding.FragmentAddBinding
import com.example.socialmediaapp.viewmodel.UserViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class AddFragment : Fragment() {

    private var _binding: FragmentAddBinding? = null
    private val binding get() = _binding!!

    private val mUserViewModel: UserViewModel by viewModels()

    @Inject
    lateinit var userAuthentication: UserAuthentication

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddBinding.inflate(inflater, container, false)
        setupToolbar()


        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.toolBar.title.text = ""
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.post_toolbar_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.post -> {
                post()
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
        return super.onOptionsItemSelected(item)
    }

    private fun post() {
        TODO("Not yet implemented")
    }

    private fun setupToolbar() {
        (activity as AppCompatActivity).setSupportActionBar(binding.toolBar.toolBar)
        setHasOptionsMenu(true)

    }

    private fun showCurrentUserPfp() {
        Glide.with(binding.pfp).load(userAuthentication.getCurrentUser()!!.photoUrl).into(binding.pfp)
    }

}