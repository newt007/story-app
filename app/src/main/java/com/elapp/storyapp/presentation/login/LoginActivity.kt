package com.elapp.storyapp.presentation.login

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.paging.ExperimentalPagingApi
import com.elapp.storyapp.MainActivity
import com.elapp.storyapp.R.string
import com.elapp.storyapp.data.remote.ApiResponse
import com.elapp.storyapp.data.remote.auth.LoginBody
import com.elapp.storyapp.databinding.ActivityLoginBinding
import com.elapp.storyapp.presentation.register.RegisterActivity
import com.elapp.storyapp.utils.ConstVal.KEY_EMAIL
import com.elapp.storyapp.utils.ConstVal.KEY_IS_LOGIN
import com.elapp.storyapp.utils.ConstVal.KEY_TOKEN
import com.elapp.storyapp.utils.ConstVal.KEY_USER_ID
import com.elapp.storyapp.utils.ConstVal.KEY_USER_NAME
import com.elapp.storyapp.utils.SessionManager
import com.elapp.storyapp.utils.ext.gone
import com.elapp.storyapp.utils.ext.show
import com.elapp.storyapp.utils.ext.showOKDialog
import com.elapp.storyapp.utils.ext.showToast
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@ExperimentalPagingApi
@AndroidEntryPoint
class LoginActivity : AppCompatActivity() {

    private val loginViewModel: LoginViewModel by viewModels()

    private var _activityLoginBinding: ActivityLoginBinding? = null
    private val binding get() = _activityLoginBinding!!

    private lateinit var pref: SessionManager

    companion object {

        fun start(context: Context) {
            val intent = Intent(context, LoginActivity::class.java)
            context.startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _activityLoginBinding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(_activityLoginBinding?.root)

        pref = SessionManager(this)

        initAction()
    }

    private fun initAction() {
        binding.btnLogin.setOnClickListener {
            val userEmail = binding.edtEmail.text.toString()
            val userPassword = binding.edtPassword.text.toString()

            when {
                userEmail.isBlank() -> {
                    binding.edtEmail.requestFocus()
                    binding.edtEmail.error = getString(string.error_empty_password)
                }
                userPassword.isBlank() -> {
                    binding.edtPassword.requestFocus()
                    binding.edtPassword.error = getString(string.error_empty_password)
                }
                else -> {
                    val request = LoginBody(
                        userEmail, userPassword
                    )

                    loginUser(request, userEmail)
                }
            }
        }
        binding.tvToRegister.setOnClickListener {
            RegisterActivity.start(this)
        }
    }

    private fun loginUser(loginBody: LoginBody, email: String) {
        lifecycleScope.launch {
            loginViewModel.userLogin(loginBody).collect { response ->
                when (response) {
                    is ApiResponse.Loading -> {
                        showLoading(true)
                    }
                    is ApiResponse.Success -> {
                        try {
                            showLoading(false)
                            val userData = response.data.loginResult
                            pref.apply {
                                setStringPreference(KEY_USER_ID, userData.userId)
                                setStringPreference(KEY_TOKEN, userData.token)
                                setStringPreference(KEY_USER_NAME, userData.name)
                                setStringPreference(KEY_EMAIL, email)
                                setBooleanPreference(KEY_IS_LOGIN, true)
                            }
                        } finally {
                            val intent = Intent(this@LoginActivity, MainActivity::class.java)
                            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                            startActivity(intent)
                            finish()
                        }
                    }
                    is ApiResponse.Error -> {
                        showLoading(false)
                        showOKDialog(getString(string.title_dialog_error), getString(string.message_incorrect_auth))
                    }
                    else -> {
                        showToast(getString(string.message_unknown_state))
                    }
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