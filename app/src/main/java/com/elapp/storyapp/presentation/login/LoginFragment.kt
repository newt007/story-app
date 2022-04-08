package com.elapp.storyapp.presentation.login

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.elapp.storyapp.R
import com.elapp.storyapp.R.string
import com.elapp.storyapp.data.remote.ApiResponse
import com.elapp.storyapp.data.remote.auth.AuthBody
import com.elapp.storyapp.data.remote.auth.LoginBody
import com.elapp.storyapp.databinding.FragmentLoginBinding
import com.elapp.storyapp.utils.ConstVal.KEY_IS_LOGIN
import com.elapp.storyapp.utils.ConstVal.KEY_TOKEN
import com.elapp.storyapp.utils.ConstVal.KEY_USER_ID
import com.elapp.storyapp.utils.ConstVal.KEY_USER_NAME
import com.elapp.storyapp.utils.SessionManager
import com.elapp.storyapp.utils.UiConstValue
import com.elapp.storyapp.utils.ext.gone
import com.elapp.storyapp.utils.ext.hide
import com.elapp.storyapp.utils.ext.isEmailValid
import com.elapp.storyapp.utils.ext.show
import com.elapp.storyapp.utils.ext.showOKDialog
import com.elapp.storyapp.utils.ext.showToast
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginFragment : Fragment() {

    private val loginViewModel: LoginViewModel by viewModels()

    private var _fragmentLogin: FragmentLoginBinding? = null
    private val binding get() = _fragmentLogin!!

    private lateinit var pref: SessionManager

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _fragmentLogin = FragmentLoginBinding.inflate(inflater)
        return _fragmentLogin?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        pref = SessionManager(requireContext())

        initAction()
    }

    private fun initAction() {
        binding.btnLogin.setOnClickListener {
            val userEmail = binding.edtEmail.text.toString()
            val userPassword = binding.edtPassword.text.toString()

            Handler(Looper.getMainLooper()).postDelayed({
                when {
                    userEmail.isBlank() -> binding.edtEmail.error = getString(string.error_empty_email)
                    !userEmail.isEmailValid() -> binding.edtEmail.error = getString(string.error_invalid_email)
                    userPassword.isBlank() -> binding.edtPassword.error = getString(string.error_empty_password)
                    else -> {
                        val request = LoginBody(
                            userEmail, userPassword
                        )
                        loginUser(request)
                    }
                }
            }, UiConstValue.ACTION_DELAYED_TIME)
        }
        binding.tvToRegister.setOnClickListener {
            it.findNavController().navigate(R.id.action_loginFragment_to_registerFragment)
        }
    }

    private fun loginUser(loginBody: LoginBody) {
        loginViewModel.loginUser(loginBody).observe(viewLifecycleOwner) { response ->
            when (response) {
                is ApiResponse.Loading -> {
                    showLoading(true)
                    context?.showToast("Register Loading")
                }
                is ApiResponse.Success -> {
                    try {
                        showLoading(false)
                        val userData = response.data.loginResult
                        context?.showToast(response.data.message)
                        pref.apply {
                            setStringPreference(KEY_USER_ID, userData.userId)
                            setStringPreference(KEY_TOKEN, userData.token)
                            setStringPreference(KEY_USER_NAME, userData.name)
                            setBooleanPreference(KEY_IS_LOGIN, true)
                        }
                    } finally {
                        findNavController().navigate(R.id.action_loginFragment_to_homeFragment)
                    }
                }
                is ApiResponse.Error -> {
                    showLoading(false)
                    context?.showOKDialog(getString(R.string.title_dialog_error), response.errorMessage)
                }
                else -> {
                    context?.showToast("Unknown State")
                }
            }
        }
    }

    private fun showLoading(isLoading: Boolean) {
        if (isLoading) binding.progressBar.show() else binding.progressBar.gone()
        if (isLoading) binding.bgDim.show() else binding.bgDim.gone()
        binding.edtEmail.isClickable = !isLoading
        binding.edtEmail.isEnabled = !isLoading
        binding.edtPassword.isClickable = !isLoading
        binding.edtPassword.isEnabled = !isLoading
        binding.btnLogin.isClickable = !isLoading
    }

}