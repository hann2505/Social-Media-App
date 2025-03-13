package com.example.socialmediaapp.ui.fragment.main

import android.os.Bundle
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.socialmediaapp.adapter.PostAdapter
import com.example.socialmediaapp.data.firebase.authentication.UserAuthentication
import com.example.socialmediaapp.databinding.FragmentPostBinding
import com.example.socialmediaapp.viewmodel.PostViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class PostFragment: Fragment() {

    private var _binding: FragmentPostBinding? = null
    private val binding get() = _binding!!

    private val args: PostFragmentArgs by navArgs()

    private val mPostViewModel: PostViewModel by viewModels()

    private var recyclerViewState: Parcelable? = null

    @Inject
    lateinit var userAuthentication: UserAuthentication

    @Inject
    lateinit var postAdapter: PostAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPostBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (args.isNavigatedFromNotification) {
            postAdapter.setIsNavigatedByNotification()
        }
        subscribeToRecyclerView()
        onClickListeners()

        binding.recyclerView.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                recyclerViewState?.let {
                    binding.recyclerView.layoutManager?.onRestoreInstanceState(it)
                }
                binding.recyclerView.viewTreeObserver.removeOnGlobalLayoutListener(this)
            }
        })
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        recyclerViewState = binding.recyclerView.layoutManager?.onSaveInstanceState()
        outState.putParcelable("RECYCLER_VIEW_STATE", recyclerViewState)
    }

    private fun subscribeToRecyclerView() {
        val recyclerView = binding.recyclerView
        recyclerView.layoutManager = LinearLayoutManager(
            requireContext(),
            LinearLayoutManager.VERTICAL,
            false
        )
        val postList = listOf(args.postWithUser)
        postAdapter.setData(postList)

        recyclerView.adapter = postAdapter
    }

    private fun onClickListeners() {

        postAdapter.setOnCommentClickListener {
            val action = PostFragmentDirections.actionPostFragmentToCommentListBottomSheetDialog(it.userId, it.postId)
            findNavController().navigate(action)

        }

        postAdapter.setOnBackClickListener {
            findNavController().popBackStack()
        }
    }

}