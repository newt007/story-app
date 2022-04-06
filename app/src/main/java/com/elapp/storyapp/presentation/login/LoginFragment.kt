package com.elapp.storyapp.presentation.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.elapp.storyapp.databinding.FragmentLoginBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginFragment: Fragment() {

    private val loginViewModel : LoginViewModel by viewModels()

    private var _fragmentLogin: FragmentLoginBinding? = null
    private val binding get() = _fragmentLogin

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _fragmentLogin = FragmentLoginBinding.inflate(inflater)
        return _fragmentLogin?.root
    }

}