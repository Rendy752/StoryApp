package com.example.storyapp.ui.detail

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
                        }

                        is Result.Error -> {
                            binding.progressBar.visibility = View.GONE
                        }
                    }
                }
            }
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