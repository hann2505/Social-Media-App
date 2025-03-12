package com.example.socialmediaapp.ui.fragment.profile

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.socialmediaapp.R
import com.example.socialmediaapp.adapter.PostAdapter
import com.example.socialmediaapp.data.firebase.authentication.UserAuthentication
import com.example.socialmediaapp.databinding.FragmentPostListBinding
import com.example.socialmediaapp.extensions.LiveDataExtensions.observeOnce
import com.example.socialmediaapp.ui.fragment.main.ProfileFragmentDirections
import com.example.socialmediaapp.viewmodel.PostViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class PostListFragment : Fragment() {

    private var _binding: FragmentPostListBinding? = null
    private val binding get() = _binding!!

    @Inject
    lateinit var postAdapter: PostAdapter

    @Inject
    lateinit var userAuthentication: UserAuthentication

    private val mPostViewModel: PostViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPostListBinding.inflate(inflater, container, false)
        println("PostListFragment: OnCreateView")

        subscribeToObservers()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

//        binding.pfSwipeRefreshLayout.setOnRefreshListener {
//            binding.pfSwipeRefreshLayout.isRefreshing = false
//        }

        onLikeClickListener()

        mPostViewModel.fetchPostByUserId(userAuthentication.getCurrentUser()!!.uid)

        postAdapter.setOnCommentClickListener {
            val action = ProfileFragmentDirections.actionProfileFragmentToCommentListBottomSheetDialog(it.postId)
            requireActivity().findNavController(R.id.nav_host_fragment).navigate(action)
        }
    }

    private fun onLikeClickListener() {

    }

    private fun subscribeToObservers() {
        binding.recyclerView.adapter = postAdapter

        binding.recyclerView.layoutManager = LinearLayoutManager(
            requireContext(),
            LinearLayoutManager.VERTICAL,
            false
        )

        binding.recyclerView.isNestedScrollingEnabled = false

        mPostViewModel.fetchPostByUserId(userAuthentication.getCurrentUser()!!.uid).observe(viewLifecycleOwner) { posts ->

            postAdapter.setData(posts)
        }

    }

}