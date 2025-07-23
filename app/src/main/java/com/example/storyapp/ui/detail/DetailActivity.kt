package com.example.storyapp.ui.detail

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.storyapp.data.Result
import com.example.storyapp.databinding.ActivityDetailBinding
import com.example.storyapp.ui.ViewModelFactory
import com.example.storyapp.utils.formatDate

class DetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailBinding
    private val viewModel by viewModels<DetailViewModel> {
        ViewModelFactory.getInstance(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.title = "Detail Cerita"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val storyId = intent.getStringExtra(EXTRA_ID)

        if (storyId != null) {
            viewModel.getDetailStory(storyId).observe(this) { result ->
                if (result != null) {
                    when (result) {
                        is Result.Loading -> {
                            binding.progressBar.visibility = View.VISIBLE
                        }

                        is Result.Success -> {
                            binding.progressBar.visibility = View.GONE
                            val story = result.data.story
                            binding.tvDetailName.text = story.name
                            binding.tvDetailDescription.text = story.description
                            "Dibuat pada: ${formatDate(story.createdAt)}".also {
                                binding.tvDetailCreatedAt.text = it
                            }
                            Glide.with(this)
                                .load(story.photoUrl)
                                .into(binding.ivDetailPhoto)

                            playAnimation()
                        }

                        is Result.Error -> {
                            binding.progressBar.visibility = View.GONE
                        }
                    }
                }
            }
        }
    }

    private fun playAnimation() {
        val photoAnimatorY = ObjectAnimator.ofFloat(binding.ivDetailPhoto, View.TRANSLATION_Y, -50f, 0f).setDuration(500)
        val photoAlpha = ObjectAnimator.ofFloat(binding.ivDetailPhoto, View.ALPHA, 0f, 1f).setDuration(500)

        val nameAnimatorX = ObjectAnimator.ofFloat(binding.tvDetailName, View.TRANSLATION_X, -50f, 0f).setDuration(500)
        val nameAlpha = ObjectAnimator.ofFloat(binding.tvDetailName, View.ALPHA, 0f, 1f).setDuration(500)

        val createdAtAnimatorX = ObjectAnimator.ofFloat(binding.tvDetailCreatedAt, View.TRANSLATION_X, -50f, 0f).setDuration(500)
        val createdAtAlpha = ObjectAnimator.ofFloat(binding.tvDetailCreatedAt, View.ALPHA, 0f, 1f).setDuration(500)

        val descriptionAnimatorX = ObjectAnimator.ofFloat(binding.tvDetailDescription, View.TRANSLATION_X, -50f, 0f).setDuration(500)
        val descriptionAlpha = ObjectAnimator.ofFloat(binding.tvDetailDescription, View.ALPHA, 0f, 1f).setDuration(500)

        AnimatorSet().apply {
            playTogether(
                photoAnimatorY,
                photoAlpha,
                nameAnimatorX,
                nameAlpha,
                createdAtAnimatorX,
                createdAtAlpha,
                descriptionAnimatorX,
                descriptionAlpha
            )
            start()
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }

    companion object {
        const val EXTRA_ID = "extra_id"
    }
}