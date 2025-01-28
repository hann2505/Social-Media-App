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
import com.example.socialmediaapp.databinding.FragmentEditGenderBinding
import com.example.socialmediaapp.viewmodel.UserViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class EditGenderFragment : Fragment() {

    private var _binding : FragmentEditGenderBinding? = null
    private val binding get() = _binding!!

    @Inject
    lateinit var userAuthentication: UserAuthentication

    private val mUserViewModel: UserViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEditGenderBinding.inflate(inflater, container, false)
        setupToolbar()
        showCurrentGender()

        binding.toolBarEdGender.backToHome.setOnClickListener {
            findNavController().popBackStack()
        }


        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.toolBarEdGender.title.text = getString(R.string.gender)

        setupCheckBoxListeners()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.done_toolbar_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.done -> {
                updateGender()
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }

    private fun setupToolbar() {
        (activity as AppCompatActivity).setSupportActionBar(binding.toolBarEdGender.toolBar)
        setHasOptionsMenu(true)

    }

    private fun updateGender() {

        val userId = userAuthentication.getCurrentUser()?.uid ?: return

        when {
            binding.maleCheckBox.isChecked ->
                mUserViewModel.updateGender(userId, true)

            binding.femaleCheckBox.isChecked ->
                mUserViewModel.updateGender(userId, false)
        }

        findNavController().popBackStack()

    }

    private fun showCurrentGender() {
        mUserViewModel.getUserInfoById(userAuthentication.getCurrentUser()!!.uid).observe(viewLifecycleOwner) {
            if (it.gender) {
                binding.maleCheckBox.isChecked = true
            } else {
                binding.femaleCheckBox.isChecked = true
            }
        }
    }

    private fun setupCheckBoxListeners() {
        binding.maleCheckBox.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                binding.femaleCheckBox.isChecked = false
            }
        }

        binding.femaleCheckBox.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                binding.maleCheckBox.isChecked = false
            }
        }
    }
    
}