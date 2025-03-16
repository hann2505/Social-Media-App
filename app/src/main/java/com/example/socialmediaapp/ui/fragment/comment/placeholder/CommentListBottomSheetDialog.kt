package com.example.socialmediaapp.ui.fragment.comment.placeholder

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.socialmediaapp.adapter.comment.CommentAdapter
import com.example.socialmediaapp.data.firebase.authentication.UserAuthentication
import com.example.socialmediaapp.databinding.FragmentCommentListBottomSheetDialogBinding
import com.example.socialmediaapp.viewmodel.CommentViewModel
import com.example.socialmediaapp.viewmodel.UserViewModel
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class CommentListBottomSheetDialog : BottomSheetDialogFragment(), TextWatcher {

    private var _binding: FragmentCommentListBottomSheetDialogBinding? = null
    private val binding get() = _binding!!

    private val mCommentViewModel: CommentViewModel by viewModels()
    private val mUserViewModel: UserViewModel by viewModels()

    @Inject
    lateinit var userAuthentication: UserAuthentication

    private val commentAdapter = CommentAdapter()

    private val args by navArgs<CommentListBottomSheetDialogArgs>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCommentListBottomSheetDialogBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        mCommentViewModel.getCommentsRealtimeUpdates(args.postOwnerId, args.postId)
//        mCommentViewModel.getCommentsRealtimeUpdatesLiveData(args.postOwnerId, args.postId)
        mUserViewModel.fetchUserInfo(userAuthentication.getCurrentUser()!!.uid)

        binding.comment.addTextChangedListener(this)


        mUserViewModel.user.observe(viewLifecycleOwner) {
            Glide.with(this).load(it!!.profilePictureUrl).into(binding.userPfp)
        }

        subscribeToObservers()
        binding.uploadButton.setOnClickListener {
            mUserViewModel.user.observe(viewLifecycleOwner) {

                mCommentViewModel.addComment(it, args.postOwnerId, args.postId, binding.comment.text.toString())

            }
            binding.comment.text.clear()
        }
    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        Log.d("CommentListBottomSheetDialog", "beforeTextChanged: $s")
    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        binding.uploadButton.visibility = if (s.isNullOrEmpty()) View.GONE else View.VISIBLE
    }

    override fun afterTextChanged(s: Editable?) {
        Log.d("CommentListBottomSheetDialog", "afterTextChanged: $s")
    }

    private fun subscribeToObservers() {

        val recyclerView = binding.commentRecyclerView
        recyclerView.layoutManager = LinearLayoutManager(
            requireContext(),
            LinearLayoutManager.VERTICAL,
            false
            )

        recyclerView.adapter = commentAdapter

        lifecycleScope.launch {
            mCommentViewModel.comment.collectLatest {
                commentAdapter.setData(it)
            }
        }

//        mCommentViewModel.commentLiveData.observe(viewLifecycleOwner) {
//            Log.d("CommentList", "subscribeToObservers: $it")
//
//            commentAdapter.setData(it)
//
//        }
    }

}