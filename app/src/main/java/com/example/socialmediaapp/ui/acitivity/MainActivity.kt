package com.example.socialmediaapp.ui.acitivity

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat.startActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.socialmediaapp.R
import com.example.socialmediaapp.databinding.ActivityMainBinding
import com.example.socialmediaapp.viewmodel.FollowerViewModel
import com.example.socialmediaapp.viewmodel.PostViewModel
import com.example.socialmediaapp.viewmodel.UserViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private var _binding: ActivityMainBinding ?= null
    private val binding get() = _binding!!

    private val mUserViewModel: UserViewModel by viewModels()
    private val mFollowerViewModel: FollowerViewModel by viewModels()
    private val mPostViewModel: PostViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        replaceFragment()
        checkOnRealtimeDatabase()

    }

    private fun replaceFragment() {

        val bottomNavigationView = binding.bottomNavigationBarLayout.bottomNavigationBar

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController

        bottomNavigationView.setupWithNavController(navController)

        binding.bottomNavigationBarLayout.bottomNavigationBar.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.homeFragment,
                R.id.searchFragment,
                R.id.notificationFragment,
                R.id.profileFragment -> {
                    navController.navigate(it.itemId)
                    true
                }
                R.id.addFragment -> {
                    startActivity(Intent(this, PostActivity::class.java))
                    true
                }

                else -> false
            }
        }

    }

    private fun checkOnRealtimeDatabase() {
        mUserViewModel.checkIfUserChanges()
        mPostViewModel.checkIfPostChanges()
        mFollowerViewModel.checkIfFollowingChanges()

    }
}