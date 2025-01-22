package com.example.socialmediaapp.ui.fragment.main

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.socialmediaapp.R
import com.example.socialmediaapp.databinding.FragmentProfileBinding
import com.example.socialmediaapp.ui.acitivity.SettingActivity

class ProfileFragment : Fragment() {

    private lateinit var binding: FragmentProfileBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentProfileBinding.inflate(inflater, container, false)

        setupToolbar()
        replaceFragment()
        
        return binding.root
    }

    private fun setupToolbar() {
        (activity as AppCompatActivity).setSupportActionBar(binding.pfToolbar)
        (activity as AppCompatActivity).supportActionBar?.setDisplayShowTitleEnabled(false)
        (activity as AppCompatActivity).supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_back)

    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.pf_toolbar_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    private fun replaceFragment() {
        val bottomNavigationView = binding.pfNavigationBarLayout.pfNavigationBar
        val navHostFragment = childFragmentManager.findFragmentById(R.id.pf_fragment_container) as NavHostFragment
        val navController = navHostFragment.navController
        bottomNavigationView.setupWithNavController(navController)
    }

    private fun goToSetting() {
        val intent = Intent(requireActivity(), SettingActivity::class.java)
        startActivity(intent)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menuFragment -> {
                goToSetting()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }
}
