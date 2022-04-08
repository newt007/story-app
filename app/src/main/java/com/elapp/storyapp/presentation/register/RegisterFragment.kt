package com.elapp.storyapp.presentation.register

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
import com.elapp.storyapp.databinding.FragmentRegisterBinding
import com.elapp.storyapp.utils.UiConstValue
import com.elapp.storyapp.utils.ext.isEmailValid
import com.elapp.storyapp.utils.ext.showOKDialog
import com.elapp.storyapp.utils.ext.showToast
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RegisterFragment: Fragment() {

    private val registerViewModel: RegisterViewModel by viewModels()

    private var _fragmentRegisterBinding: FragmentRegisterBinding? = null
    private val binding get() = _fragmentRegisterBinding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _fragmentRegisterBinding = FragmentRegisterBinding.inflate(inflater)
        return _fragmentRegisterBinding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initAction()
    }

    private fun initAction() {
        binding.btnRegister.setOnClickListener {
            val userName = binding.edtName.text.toString()
            val userEmail = binding.edtEmail.text.toString()
            val userPassword = binding.edtPassword.text.toString()

            Handler(Looper.getMainLooper()).postDelayed({
                when {
                    userName.isBlank() -> binding.edtName.error = getString(string.error_empty_name)
                    userEmail.isBlank() -> binding.edtEmail.error = getString(string.error_empty_email)
                    !userEmail.isEmailValid() -> binding.edtEmail.error = getString(string.error_invalid_email)
                    userPassword.isBlank() -> binding.edtPassword.error = getString(string.error_empty_password)
                    else -> {
                        val request = AuthBody(
                            userName, userEmail, userPassword
                        )
                        registerUser(request)
                    }
                }
            }, UiConstValue.ACTION_DELAYED_TIME)
        }
        binding.tvToLogin.setOnClickListener {
            it.findNavController().navigate(R.id.action_registerFragment_to_loginFragment)
        }
    }

    private fun registerUser(newUser: AuthBody) {
        registerViewModel.registerUser(newUser).observe(viewLifecycleOwner) { response ->
            when(response) {
                is ApiResponse.Loading -> {
                    showLoading(true)
                    context?.showToast("Register Loading")
                }
                is ApiResponse.Success -> {
                    try {
                        showLoading(false)
                        context?.showToast(response.data.message)
                    } finally {
                        findNavController().navigate(R.id.action_registerFragment_to_loginFragment)
                    }
                }
                is ApiResponse.Error -> {
                    showLoading(false)
                    context?.showOKDialog(getString(string.title_dialog_error), response.errorMessage)
                }
                else -> {
                    context?.showToast("Unknown State")
                }
            }
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        binding.bgDim.visibility = if (isLoading) View.VISIBLE else View.GONE
        binding.edtEmail.isClickable = !isLoading
        binding.edtEmail.isEnabled = !isLoading
        binding.edtPassword.isClickable = !isLoading
        binding.edtPassword.isEnabled = !isLoading
        binding.btnRegister.isClickable = !isLoading
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _fragmentRegisterBinding = null
    }

}