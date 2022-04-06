package com.elapp.storyapp.presentation.splash

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.elapp.storyapp.R
import com.elapp.storyapp.databinding.FragmentSplashBinding
import com.elapp.storyapp.utils.SessionManager
import com.elapp.storyapp.utils.UiConstValue
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SplashFragment: Fragment() {

    private var _fragmentSplashBinding: FragmentSplashBinding? = null
    private val binding get() = _fragmentSplashBinding
    private lateinit var pref: SessionManager

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _fragmentSplashBinding = FragmentSplashBinding.inflate(inflater)
        return _fragmentSplashBinding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        pref = SessionManager(requireContext())
        val isLogin = pref.isLogin
        Handler(Looper.getMainLooper()).postDelayed({
            when {
                isLogin -> {
                    findNavController().navigate(R.id.action_splashFragment_to_homeFragment)
                }
                else -> {
                    findNavController().navigate(R.id.action_splashFragment_to_registerFragment)
                }
            }
        }, UiConstValue.LOADING_TIME)
    }

}