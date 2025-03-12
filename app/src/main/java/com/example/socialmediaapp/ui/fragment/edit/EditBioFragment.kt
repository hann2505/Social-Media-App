package com.example.socialmediaapp.ui.fragment.edit

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
import androidx.navigation.fragment.findNavController
import com.example.socialmediaapp.R
import com.example.socialmediaapp.data.firebase.authentication.UserAuthentication
import com.example.socialmediaapp.databinding.FragmentEditBioBinding
import com.example.socialmediaapp.viewmodel.UserViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class EditBioFragment : Fragment() {

    private lateinit var binding: FragmentEditBioBinding

    private val mUserViewModel: UserViewModel by viewModels()

    @Inject
    lateinit var userAuthentication: UserAuthentication

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentEditBioBinding.inflate(inflater, container, false)
        setupToolbar()
        showCurrentBio()

        binding.toolBarEdPf.backToHome.setOnClickListener {
            findNavController().popBackStack()
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.toolBarEdPf.title.text = getString(R.string.bio)
        mUserViewModel.fetchUserInfo(userAuthentication.getCurrentUser()!!.uid)

        }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.done_toolbar_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.done -> {
                updateBio()
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
        return super.onOptionsItemSelected(item)
    }

    private fun setupToolbar() {
        (activity as AppCompatActivity).setSupportActionBar(binding.toolBarEdPf.toolBar)
        setHasOptionsMenu(true)

    }

    private fun showCurrentBio() {

        mUserViewModel.user.observe(viewLifecycleOwner) {
            binding.bioEditText.editText!!.setText(it!!.bio)
        }
    }

    private fun updateBio() {
        val bio = binding.bioEditText.editText!!.text.toString()
        if (bio.isNotEmpty()) {
            mUserViewModel.updateBio(userAuthentication.getCurrentUser()!!.uid, bio)
            findNavController().popBackStack()
        }

    }
}