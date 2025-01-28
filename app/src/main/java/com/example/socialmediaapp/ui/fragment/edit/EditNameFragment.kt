package com.example.socialmediaapp.ui.fragment.edit

import android.os.Bundle
import android.util.Log
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
import com.example.socialmediaapp.databinding.FragmentEditNameBinding
import com.example.socialmediaapp.viewmodel.UserViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class EditNameFragment : Fragment() {

    private var _binding: FragmentEditNameBinding ?= null
    private val binding get() = _binding!!

    private val mUserViewModel: UserViewModel by viewModels()

    @Inject
    lateinit var userAuthentication: UserAuthentication

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEditNameBinding.inflate(inflater, container, false)
        setupToolbar()

        binding.toolBarEdPf.backToHome.setOnClickListener {
            findNavController().popBackStack()
        }

        showCurrentName()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.toolBarEdPf.title.text = getString(R.string.name)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.done_toolbar_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.done -> {
                updateName()
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }

    private fun setupToolbar() {
        (activity as AppCompatActivity).setSupportActionBar(binding.toolBarEdPf.toolBar)
        setHasOptionsMenu(true)

    }

    private fun updateName() {
        val name = binding.nameEditText.editText!!.text.toString()
        if (name.isNotEmpty()) {
            mUserViewModel.updateName(userAuthentication.getCurrentUser()!!.uid, name)
            findNavController().popBackStack()
        }
    }

    private fun showCurrentName() {
        val name = mUserViewModel.getUserInfoById(userAuthentication.getCurrentUser()!!.uid)
        Log.d("name", "$name")
        mUserViewModel.getUserInfoById(userAuthentication.getCurrentUser()!!.uid).observe(viewLifecycleOwner) {
            binding.nameEditText.editText!!.setText(it.name)
        }
    }
}