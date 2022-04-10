package com.elapp.storyapp.presentation.login

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.elapp.storyapp.MainActivity
import com.elapp.storyapp.R.string
import com.elapp.storyapp.data.remote.ApiResponse
import com.elapp.storyapp.data.remote.auth.LoginBody
import com.elapp.storyapp.databinding.ActivityLoginBinding
import com.elapp.storyapp.utils.ConstVal
import com.elapp.storyapp.utils.SessionManager
import com.elapp.storyapp.utils.UiConstValue
import com.elapp.storyapp.utils.ext.gone
import com.elapp.storyapp.utils.ext.isEmailValid
import com.elapp.storyapp.utils.ext.show
import com.elapp.storyapp.utils.ext.showOKDialog
import com.elapp.storyapp.utils.ext.showToast
import dagger.hilt.android.AndroidEntryPoint

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

        }
    }

    private fun loginUser(loginBody: LoginBody) {
        loginViewModel.loginUser(loginBody).observe(this) { response ->
            when (response) {
                is ApiResponse.Loading -> {
                    showLoading(true)
                    showToast(getString(string.message_register_loading))
                }
                is ApiResponse.Success -> {
                    try {
                        showLoading(false)
                        val userData = response.data.loginResult
                        showToast(response.data.message)
                        pref.apply {
                            setStringPreference(ConstVal.KEY_USER_ID, userData.userId)
                            setStringPreference(ConstVal.KEY_TOKEN, userData.token)
                            setStringPreference(ConstVal.KEY_USER_NAME, userData.name)
                            setBooleanPreference(ConstVal.KEY_IS_LOGIN, true)
                        }
                    } finally {
                        MainActivity.start(this)
                    }
                }
                is ApiResponse.Error -> {
                    showLoading(false)
                    showOKDialog(getString(string.title_dialog_error), response.errorMessage)
                }
                else -> {
                    showToast(getString(string.message_unknown_state))
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