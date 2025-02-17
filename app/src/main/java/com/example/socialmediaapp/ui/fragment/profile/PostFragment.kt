package com.example.socialmediaapp.ui.fragment.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.socialmediaapp.R
import com.example.socialmediaapp.adapter.PostAdapter
import com.example.socialmediaapp.data.firebase.authentication.UserAuthentication
import com.example.socialmediaapp.databinding.FragmentPostBinding
import com.example.socialmediaapp.viewmodel.PostViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class PostFragment : Fragment() {

    private var _binding: FragmentPostBinding? = null
    private val binding get() = _binding!!

    private val postAdapter = PostAdapter()

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
        _binding = FragmentPostBinding.inflate(inflater, container, false)

        mPostViewModel.fetchDataFromFirebase()
        subscribeToObservers()

        postAdapter.setOnCommentClickListener {
            val action = PostFragmentDirections.actionPostFragmentToCommentListBottomSheetDialog(it)
            findNavController().navigate(action)
        }


        return binding.root
    }

    private fun subscribeToObservers() {

        binding.recyclerView.layoutManager = LinearLayoutManager(
            requireContext(),
            LinearLayoutManager.VERTICAL,
            false
        )

        binding.recyclerView.isNestedScrollingEnabled = false

        mPostViewModel.getPostWithUserByUserId(userAuthentication.getCurrentUser()!!.uid).observe(viewLifecycleOwner) { posts ->
            postAdapter.setData(posts)
            binding.recyclerView.adapter = postAdapter
        }

    }

}