package com.example.storyapp.ui.login

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.storyapp.data.Result
import com.example.storyapp.data.pref.User
import com.example.storyapp.databinding.ActivityLoginBinding
import com.example.storyapp.ui.ViewModelFactory
import com.example.storyapp.ui.main.MainActivity
import com.example.storyapp.ui.register.RegisterActivity
import kotlinx.coroutines.launch


class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private val viewModel by viewModels<LoginViewModel> {
        ViewModelFactory.getInstance(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupAction()
    }

    private fun setupAction() {
        binding.btnLogin.setOnClickListener {
            val email = binding.edLoginEmail.text.toString()
            val password = binding.edLoginPassword.text.toString()

            if (email.isNotEmpty() && password.isNotEmpty() && password.length >= 8) {
                viewModel.login(email, password).observe(this) { result ->
                    if (result != null) {
                        when (result) {
                            is Result.Loading -> {
                                binding.progressBar.visibility = View.VISIBLE
                            }
                            is Result.Success -> {
                                binding.progressBar.visibility = View.GONE
                                val token = result.data.loginResult.token
                                val userToSave = User(email, token, true)

                                lifecycleScope.launch {
                                    viewModel.saveSession(userToSave)
                                }
                                showToast("Login Berhasil!")
                                navigateToMain()
                            }
                            is Result.Error -> {
                                binding.progressBar.visibility = View.GONE
                                showToast(result.error)
                            }
                        }
                    }
                }
            } else {
                showToast("Email atau Password tidak valid.")
            }
        }

        binding.tvToRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }

    private fun navigateToMain() {
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
        finish()
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}