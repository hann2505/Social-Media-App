package com.example.socialmediaapp.ui.fragment.search

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.socialmediaapp.R
import com.example.socialmediaapp.databinding.FragmentUserSearchViewBinding

class UserSearchViewFragment : Fragment() {
    private var _binding: FragmentUserSearchViewBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentUserSearchViewBinding.inflate(inflater, container, false)

        return binding.root
    }

}