package com.example.socialmediaapp.ui.fragment.profile

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.socialmediaapp.databinding.FragmentVideoBinding


class VideoFragment : Fragment() {

    private var _binding: FragmentVideoBinding ?= null
    private val binding get() = _binding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
         _binding = FragmentVideoBinding.inflate(inflater, container, false)

        return binding!!.root
    }

}