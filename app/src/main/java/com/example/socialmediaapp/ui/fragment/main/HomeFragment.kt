package com.example.socialmediaapp.ui.fragment.main

import android.content.Context
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
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.socialmediaapp.R
import com.example.socialmediaapp.adapter.post.PostAdapter
import com.example.socialmediaapp.data.firebase.authentication.UserAuthentication
import com.example.socialmediaapp.databinding.FragmentHomeBinding
import com.example.socialmediaapp.ui.acitivity.ChatActivity
import com.example.socialmediaapp.viewmodel.FollowerViewModel
import com.example.socialmediaapp.viewmodel.PostViewModel
import com.example.socialmediaapp.viewmodel.UserViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class HomeFragment : Fragment() {


    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val mUserViewModel: UserViewModel by viewModels()
    private val mPostViewModel: PostViewModel by viewModels()
    private val mFollowerViewModel: FollowerViewModel by viewModels()

    @Inject
    lateinit var userAuthentication: UserAuthentication

    @Inject
    lateinit var postAdapter: PostAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        println("HomeFragment: OnCreate")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        setupToolbar()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        println("HomeFragment: OnViewCreated")
        mPostViewModel.getNewestPost()
        setUpRecyclerView()
        onClickListener()

        binding.swipeRefreshLayout.setOnRefreshListener {
            mPostViewModel.getNewestPost()
            binding.swipeRefreshLayout.isRefreshing = false
        }

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

    private fun setupToolbar() {
        (activity as AppCompatActivity).setSupportActionBar(binding.homeToolbar)
        (activity as AppCompatActivity).supportActionBar?.setDisplayShowTitleEnabled(false)
        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.home_toolbar_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.message_btn -> {
                val intent = Intent(requireActivity(), ChatActivity::class.java)
                startActivity(intent)
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun setUpRecyclerView() {
        binding.postRecyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        }

        lifecycleScope.launch {
            mPostViewModel.newFeed.collectLatest {
                postAdapter.setData(it)
                binding.postRecyclerView.adapter = postAdapter
            }
        }

    }

    private fun onClickListener() {
        postAdapter.setOnCommentClickListener {
            val action = HomeFragmentDirections.actionHomeFragmentToCommentListBottomSheetDialog(it.userId, it.postId)
            findNavController().navigate(action)
        }
    }
}