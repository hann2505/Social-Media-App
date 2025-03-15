package com.example.socialmediaapp.ui.fragment.main

import android.content.Intent
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
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.navigation.ui.setupWithNavController
import com.bumptech.glide.Glide
import com.example.socialmediaapp.R
import com.example.socialmediaapp.data.firebase.authentication.UserAuthentication
import com.example.socialmediaapp.databinding.FragmentProfileBinding
import com.example.socialmediaapp.ui.acitivity.EditProfileActivity
import com.example.socialmediaapp.ui.acitivity.SettingActivity
import com.example.socialmediaapp.viewmodel.FollowerViewModel
import com.example.socialmediaapp.viewmodel.PostViewModel
import com.example.socialmediaapp.viewmodel.UserViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class ProfileFragment : Fragment() {

    private lateinit var binding: FragmentProfileBinding

    private val mUserViewModel: UserViewModel by activityViewModels()
    private val mPostViewModel: PostViewModel by activityViewModels()
    private val mFollowerViewModel: FollowerViewModel by activityViewModels()

    private val args: ProfileFragmentArgs by navArgs()

    @Inject
    lateinit var userAuthentication: UserAuthentication

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentProfileBinding.inflate(inflater, container, false)

        binding.editProfileBtn.setOnClickListener {
            val intent = Intent(requireActivity(), EditProfileActivity::class.java)
            startActivity(intent)
        }

        binding.messageBtn.setOnClickListener {

        }

        binding.followers.setOnClickListener {

        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mUserViewModel.fetchUserInfo(userAuthentication.getCurrentUser()!!.uid)
        mFollowerViewModel.getFollowingCount(userAuthentication.getCurrentUser()!!.uid)
        mFollowerViewModel.getFollowerCount(userAuthentication.getCurrentUser()!!.uid)
        mPostViewModel.getPostCountUpdate(userAuthentication.getCurrentUser()!!.uid)


        setupToolbar()
        replaceFragment()
        displayBackButton()
        showCurrentUserInfo()



        binding.pfSwipeRefreshLayout.setOnRefreshListener {
            mUserViewModel.fetchUserInfo(userAuthentication.getCurrentUser()!!.uid)

            binding.pfSwipeRefreshLayout.isRefreshing = false

        }


        binding.editProfileBtn.setOnClickListener {
            val intent = Intent(requireActivity(), EditProfileActivity::class.java)
            startActivity(intent)
        }

        binding.messageBtn.setOnClickListener {

        }

        binding.followers.setOnClickListener {

        }

    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
    }

    //* with this approach, app will show the clone user profile when current user is null
    private fun showCurrentUserInfo() {
        if (userAuthentication.getCurrentUser() == null)
            return
        else {
            mUserViewModel.user.observe(viewLifecycleOwner) {
                binding.userName.text = it.username
                binding.name.text = it.name
                binding.userBio.text = it.bio
                Glide.with(binding.userPfp).load(it.profilePictureUrl).into(binding.userPfp)
            }
        }

        getFollowInfo()

    }

    private fun setupToolbar() {
        (activity as AppCompatActivity).setSupportActionBar(binding.pfToolbar)
        (activity as AppCompatActivity).supportActionBar?.setDisplayShowTitleEnabled(false)
        setHasOptionsMenu(true)
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
                Log.d("TAG", "onOptionsItemSelected: ")
                goToSetting()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun displayBackButton() {
        if (args.isCurrentUser) {
            binding.back.visibility = View.VISIBLE
            binding.back.setOnClickListener {
                findNavController().popBackStack()
            }
        }
    }

    private fun getFollowInfo() {
        mFollowerViewModel.followingCount.observe(viewLifecycleOwner) {
            binding.followingNumber.text = it.toString()
        }

        mFollowerViewModel.followerCount.observe(viewLifecycleOwner) {
            binding.followersNumber.text = it.toString()
        }

        mPostViewModel.postCount.observe(viewLifecycleOwner) {
            binding.postNumber.text = it.toString()
        }
    }
}
