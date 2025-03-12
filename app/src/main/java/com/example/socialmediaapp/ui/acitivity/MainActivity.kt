package com.example.socialmediaapp.ui.acitivity

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavOptions
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.socialmediaapp.R
import com.example.socialmediaapp.data.firebase.authentication.UserAuthentication
import com.example.socialmediaapp.databinding.ActivityMainBinding
import com.example.socialmediaapp.viewmodel.CommentViewModel
import com.example.socialmediaapp.viewmodel.FollowerViewModel
import com.example.socialmediaapp.viewmodel.PostViewModel
import com.example.socialmediaapp.viewmodel.UserViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private var _binding: ActivityMainBinding ?= null
    private val binding get() = _binding!!

    private val mUserViewModel: UserViewModel by viewModels()
    private val mFollowerViewModel: FollowerViewModel by viewModels()
    private val mPostViewModel: PostViewModel by viewModels()
    private val mCommentViewModel: CommentViewModel by viewModels()

    @Inject
    lateinit var userAuthentication: UserAuthentication

    private val tabOrder = listOf(R.id.homeFragment, R.id.searchFragment, R.id.notificationFragment, R.id.profileFragment)
    private var currentTabId: Int = R.id.homeFragment

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

        println("MainActivity: OnCreated")

    }

    override fun onStart() {
        super.onStart()
        replaceFragment()
        println("MainActivity: OnStart")
    }

    override fun onResume() {
        super.onResume()
        println("MainActivity: OnResume")
        checkIfUserLoggedIn()
    }

    override fun onPause() {
        super.onPause()
        println("MainActivity: OnPause")
    }

    override fun onStop() {
        super.onStop()
        println("MainActivity: OnStop")
    }

    override fun onDestroy() {
        super.onDestroy()
        println("MainActivity: OnDestroy")
        _binding = null
    }

    private fun checkIfUserLoggedIn() {
        if (userAuthentication.getCurrentUser() == null) {
            startActivity(Intent(this, SignInActivity::class.java))
            finish()
        }
    }

    private fun replaceFragment() {

        val bottomNavigationView = binding.bottomNavigationBarLayout.bottomNavigationBar

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController

        bottomNavigationView.setupWithNavController(navController)

        binding.bottomNavigationBarLayout.bottomNavigationBar.setOnItemSelectedListener { item ->
            val currentIndex = tabOrder.indexOf(currentTabId)
            val newIndex = tabOrder.indexOf(item.itemId)

            if (currentIndex == item.itemId) return@setOnItemSelectedListener false

            val isForward = newIndex > currentIndex

            // Animate through intermediate steps
            val steps = if (isForward)
                    currentIndex + 1..newIndex
            else
                currentIndex - 1 downTo newIndex

            val navOptions = getSlideAnimation(isForward)

            when (item.itemId) {
                R.id.homeFragment,
                R.id.searchFragment,
                R.id.notificationFragment,
                R.id.profileFragment -> {
                    lifecycleScope.launch {
                        for (step in steps) {
                            delay(100)
                            navController.navigate(tabOrder[step], null, navOptions)
                        }
                    }
                    currentTabId = item.itemId
                    true
                }
                R.id.addFragment -> {
                    startActivity(Intent(this, UploadActivity::class.java))
                    true
                }

                else -> false
            }
        }

    }

    private fun getSlideAnimation(isForward: Boolean): NavOptions {


        val slideIn = if (isForward) R.anim.slide_in_right else R.anim.slide_in_left_main_bottom_navigation
        val slideOut = if (isForward) R.anim.slide_to_left_main_bottom_navigation else R.anim.slide_to_right

        return NavOptions.Builder()
            .setEnterAnim(slideIn)
            .setExitAnim(slideOut)
            .setPopEnterAnim(slideIn)
            .setPopExitAnim(slideOut)
            .build()
    }
}