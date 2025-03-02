package com.example.socialmediaapp.ui.fragment.comment.placeholder

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.socialmediaapp.adapter.CommentAdapter
import com.example.socialmediaapp.data.firebase.authentication.UserAuthentication
import com.example.socialmediaapp.databinding.FragmentCommentListBottomSheetDialogBinding
import com.example.socialmediaapp.viewmodel.CommentViewModel
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class CommentListBottomSheetDialog : BottomSheetDialogFragment(), TextWatcher {

    private var _binding: FragmentCommentListBottomSheetDialogBinding? = null
    private val binding get() = _binding!!

    private val mCommentViewModel: CommentViewModel by viewModels()

    @Inject
    lateinit var userAuthentication: UserAuthentication

    private val commentAdapter = CommentAdapter()

    private val args by navArgs<CommentListBottomSheetDialogArgs>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCommentListBottomSheetDialogBinding.inflate(inflater, container, false)
        subscribeToObservers()

        binding.uploadButton.setOnClickListener {
            mCommentViewModel.addComment(
                userAuthentication.getCurrentUser()!!.uid,
                args.postId,
                binding.comment.text.toString()
            )
            binding.comment.text.clear()
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.comment.addTextChangedListener(this)
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

        mCommentViewModel.getCommentWithUser(args.postId).observe(viewLifecycleOwner) {
            commentAdapter.setData(it)
            recyclerView.adapter = commentAdapter
        }

    }

}