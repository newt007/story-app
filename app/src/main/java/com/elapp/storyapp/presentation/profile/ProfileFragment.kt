package com.elapp.storyapp.presentation.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.elapp.storyapp.databinding.FragmentProfileBinding

class ProfileFragment: Fragment() {

    private var _fragmentProfileBinding: FragmentProfileBinding? = null
    private val binding get() = _fragmentProfileBinding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _fragmentProfileBinding = FragmentProfileBinding.inflate(inflater)
        return _fragmentProfileBinding?.root
    }

}