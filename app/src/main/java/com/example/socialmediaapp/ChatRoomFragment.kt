package com.example.socialmediaapp

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.socialmediaapp.adapter.MessageAdapter
import com.example.socialmediaapp.data.firebase.authentication.UserAuthentication
import com.example.socialmediaapp.databinding.FragmentChatRoomBinding
import com.example.socialmediaapp.viewmodel.MessageViewModel
import com.example.socialmediaapp.viewmodel.UserViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class ChatRoomFragment : Fragment(), TextWatcher {

    private var _binding: FragmentChatRoomBinding? = null
    private val binding get() = _binding!!

    private val mMessageViewModel: MessageViewModel by viewModels()
    private val mUserViewModel: UserViewModel by viewModels()

    private val args: ChatRoomFragmentArgs by navArgs()

    private var isFirstLoad = true

    @Inject
    lateinit var auth: UserAuthentication

    @Inject
    lateinit var messageAdapter: MessageAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentChatRoomBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mMessageViewModel.fetchMessagesRealtime(args.user.userId)
        mUserViewModel.fetchUserInfo(auth.getCurrentUser()!!.uid)
        setUpRecyclerView()
        binding.message.addTextChangedListener(this)

        binding.username.text = args.user.username
        Glide.with(binding.userProfilePicture).load(args.user.profilePictureUrl).into(binding.userProfilePicture)

        mUserViewModel.user.observe(viewLifecycleOwner) {
            Glide.with(binding.userPfp).load(it.profilePictureUrl).into(binding.userPfp)
        }

        binding.sentBtn.setOnClickListener {
            mMessageViewModel.sendMessage(args.user.userId, binding.message.text.toString())
            binding.message.text.clear()
        }

        binding.back.setOnClickListener {
            findNavController().popBackStack()
        }

    }

    private fun setUpRecyclerView() {
        val recyclerView = binding.recyclerView

        val layoutManager = LinearLayoutManager(
            requireContext(),
            LinearLayoutManager.VERTICAL,
            false
        )

        recyclerView.layoutManager = layoutManager
        recyclerView.adapter = messageAdapter


        lifecycleScope.launch {
            mMessageViewModel.messagesList.collectLatest {
                Log.d("ChatRoomFragment", it.toString())
                messageAdapter.updateMessageList(it)
                recyclerView.post {
                    if (isFirstLoad && it.isNotEmpty()) {
                        recyclerView.scrollToPosition(messageAdapter.itemCount - 1)
                        isFirstLoad = false
                    }
                    else if (isUserAtBottom(layoutManager)) {
                        recyclerView.scrollToPosition(messageAdapter.itemCount - 1)
                    }
                }            }
        }

    }

    private fun isUserAtBottom(layoutManager: LinearLayoutManager): Boolean {
        val lastVisibleItem = layoutManager.findLastVisibleItemPosition()
        return lastVisibleItem >= messageAdapter.itemCount - 3
    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        binding.sentBtn.visibility = if (s.isNullOrEmpty()) View.GONE else View.VISIBLE
    }

    override fun afterTextChanged(s: Editable?) {
    }
}