package com.example.storyapp.ui.register

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.storyapp.data.Result
import com.example.storyapp.data.response.RegisterResponse
import com.example.storyapp.databinding.ActivityRegisterBinding
import com.example.storyapp.ui.ViewModelFactory

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private val viewModel by viewModels<RegisterViewModel> {
        ViewModelFactory.getInstance(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupAction()
        playAnimation()
    }

    private fun playAnimation() {
        val titleAnimatorX = ObjectAnimator.ofFloat(binding.tvRegisterTitle, View.TRANSLATION_X, -50f, 0f).setDuration(500)
        val titleAlpha = ObjectAnimator.ofFloat(binding.tvRegisterTitle, View.ALPHA, 0f, 1f).setDuration(500)

        val nameAlpha = ObjectAnimator.ofFloat(binding.edRegisterName, View.ALPHA, 0f, 1f).setDuration(500)
        val emailAlpha = ObjectAnimator.ofFloat(binding.edRegisterEmail, View.ALPHA, 0f, 1f).setDuration(500)
        val passwordAlpha = ObjectAnimator.ofFloat(binding.edRegisterPassword, View.ALPHA, 0f, 1f).setDuration(500)
        val registerButtonAnimatorX = ObjectAnimator.ofFloat(binding.btnRegister, View.TRANSLATION_X, -50f, 0f).setDuration(500)
        val registerButtonAlpha = ObjectAnimator.ofFloat(binding.btnRegister, View.ALPHA, 0f, 1f).setDuration(500)

        AnimatorSet().apply {
            playTogether(
                titleAnimatorX,
                titleAlpha,
                nameAlpha,
                emailAlpha,
                passwordAlpha,
                registerButtonAnimatorX,
                registerButtonAlpha
            )
            start()
        }
    }

    private fun setupAction() {
        binding.btnRegister.setOnClickListener {
            val name = binding.edRegisterName.text.toString()
            val email = binding.edRegisterEmail.text.toString()
            val password = binding.edRegisterPassword.text.toString()

            viewModel.register(name, email, password)
                .observe(this) { result: Result<RegisterResponse> ->
                    when (result) {
                        is Result.Loading -> {
                            binding.progressBar.visibility = View.VISIBLE
                        }

                        is Result.Success<*> -> {
                            binding.progressBar.visibility = View.GONE
                            showSuccessDialog(email)
                        }

                        is Result.Error -> {
                            binding.progressBar.visibility = View.GONE
                            showToast(result.error)
                        }
                    }
                }
        }
    }

    private fun showSuccessDialog(email: String) {
        AlertDialog.Builder(this).apply {
            setTitle("Yeah!")
            setMessage("Akun dengan $email sudah jadi nih. Yuk, login dan bagikan ceritamu.")
            setPositiveButton("Lanjut") { _, _ ->
                finish()
            }
            create()
            show()
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}