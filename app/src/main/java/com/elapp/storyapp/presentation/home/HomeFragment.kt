package com.elapp.storyapp.presentation.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.elapp.storyapp.data.remote.ApiResponse
import com.elapp.storyapp.databinding.FragmentHomeBinding
import com.elapp.storyapp.presentation.story.StoryAdapter
import com.elapp.storyapp.utils.SessionManager
import com.elapp.storyapp.utils.ext.showToast
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeFragment: Fragment() {

    private val homeViewModel: HomeViewModel by viewModels()

    private var _fragmentHomeBinding: FragmentHomeBinding? = null
    private val binding get() = _fragmentHomeBinding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _fragmentHomeBinding = FragmentHomeBinding.inflate(inflater)
        return _fragmentHomeBinding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUI()

        val token = SessionManager(requireContext()).getToken
        getAllStories("Bearer ${token.toString()}")
    }

    private fun initUI() {
        binding.rvStories.layoutManager = LinearLayoutManager(context)
    }

    private fun getAllStories(token: String) {
        homeViewModel.getAllStories(token).observe(viewLifecycleOwner) { response ->
            when(response) {
                is ApiResponse.Loading -> {
                    context?.showToast("Loading Story Data")
                }
                is ApiResponse.Success -> {
                    val storyAdapter = StoryAdapter(response.data.listStory)
                    binding.rvStories.adapter = storyAdapter
                }
            }
        }
    }

}