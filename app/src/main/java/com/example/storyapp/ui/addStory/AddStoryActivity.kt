package com.example.storyapp.ui.addStory

import android.Manifest
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.storyapp.R
import com.example.storyapp.data.Result
import com.example.storyapp.databinding.ActivityAddStoryBinding
import com.example.storyapp.ui.ViewModelFactory
import com.example.storyapp.ui.main.MainActivity
import com.example.storyapp.ui.widget.StoryWidget
import com.example.storyapp.utils.getImageUri
import com.example.storyapp.utils.reduceFileImage
import com.example.storyapp.utils.uriToFile
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices

class AddStoryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddStoryBinding
    private var currentImageUri: Uri? = null
    private var currentLocation: Location? = null
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private val viewModel by viewModels<AddStoryViewModel> {
        ViewModelFactory.getInstance(this)
    }

    private val requestCameraPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                Toast.makeText(this, "Camera permission granted", Toast.LENGTH_LONG).show()
                startCamera()
            } else {
                Toast.makeText(this, "Camera permission denied", Toast.LENGTH_LONG).show()
            }
        }

    private val requestLocationPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                getMyLocation()
            } else {
                Toast.makeText(this, "Location permission denied", Toast.LENGTH_SHORT).show()
                binding.switchLocation.isChecked = false
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        playAnimation()

        binding.btnGallery.setOnClickListener { startGallery() }
        binding.btnCamera.setOnClickListener { startCamera() }
        binding.buttonAdd.setOnClickListener { uploadImage() }
        binding.switchLocation.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                getMyLocation()
            } else {
                currentLocation = null
            }
        }
    }

    private fun playAnimation() {
        val previewAnimatorY =
            ObjectAnimator.ofFloat(binding.ivPreview, View.TRANSLATION_Y, -50f, 0f).setDuration(500)
        val previewAlpha =
            ObjectAnimator.ofFloat(binding.ivPreview, View.ALPHA, 0f, 1f).setDuration(500)

        val galleryAnimatorX =
            ObjectAnimator.ofFloat(binding.btnGallery, View.TRANSLATION_X, -50f, 0f)
                .setDuration(500)
        val galleryAlpha =
            ObjectAnimator.ofFloat(binding.btnGallery, View.ALPHA, 0f, 1f).setDuration(500)

        val cameraAnimatorX =
            ObjectAnimator.ofFloat(binding.btnCamera, View.TRANSLATION_X, -50f, 0f).setDuration(500)
        val cameraAlpha =
            ObjectAnimator.ofFloat(binding.btnCamera, View.ALPHA, 0f, 1f).setDuration(500)

        val descriptionAlpha =
            ObjectAnimator.ofFloat(binding.edAddDescription, View.ALPHA, 0f, 1f).setDuration(500)

        val addAnimatorX =
            ObjectAnimator.ofFloat(binding.buttonAdd, View.TRANSLATION_X, -50f, 0f).setDuration(500)
        val addAlpha =
            ObjectAnimator.ofFloat(binding.buttonAdd, View.ALPHA, 0f, 1f).setDuration(500)

        AnimatorSet().apply {
            playTogether(
                previewAnimatorY,
                previewAlpha,
                galleryAnimatorX,
                galleryAlpha,
                cameraAnimatorX,
                cameraAlpha,
                descriptionAlpha,
                addAnimatorX,
                addAlpha
            )
            start()
        }
    }

    private fun getMyLocation() {
        if (ContextCompat.checkSelfPermission(
                this.applicationContext,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                if (location != null) {
                    currentLocation = location
                    showToast("Location captured successfully")
                } else {
                    Toast.makeText(
                        this,
                        "Location is not found. Please try again",
                        Toast.LENGTH_SHORT
                    ).show()
                    binding.switchLocation.isChecked = false
                }
            }
        } else {
            requestLocationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    private fun uploadImage() {
        currentImageUri?.let { uri ->
            val imageFile = uriToFile(uri, this).reduceFileImage()
            val description = binding.edAddDescription.text.toString()

            if (description.isEmpty()) {
                showToast("Description cannot be empty.")
                return
            }

            val lat = if (binding.switchLocation.isChecked) currentLocation?.latitude else null
            val lon = if (binding.switchLocation.isChecked) currentLocation?.longitude else null

            viewModel.uploadImage(imageFile, description, lat, lon).observe(this) { result ->
                if (result != null) {
                    when (result) {
                        is Result.Loading -> {
                            showLoading(true)
                        }

                        is Result.Success -> {
                            showLoading(false)
                            showToast(result.data.message)

                            val updateWidgetIntent = Intent(this, StoryWidget::class.java).apply {
                                action = StoryWidget.UPDATE_ACTION
                            }
                            sendBroadcast(updateWidgetIntent)

                            val intent = Intent(this, MainActivity::class.java)
                            intent.flags =
                                Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                            startActivity(intent)
                            finish()
                        }

                        is Result.Error -> {
                            showLoading(false)
                            showToast(result.error)
                        }
                    }
                }
            }
        } ?: showToast(getString(R.string.empty_image_warning))
    }

    private fun startCamera() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            currentImageUri = getImageUri(this)
            currentImageUri?.let {
                launcherIntentCamera.launch(it)
            } ?: showToast(getString(R.string.camera_failed_to_start))
        } else {
            requestCameraPermissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    private val launcherGallery = registerForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            currentImageUri = uri
            showImage()
        } else {
            Log.d("Photo Picker", "No media selected")
        }
    }

    private fun startGallery() {
        launcherGallery.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }

    private val launcherIntentCamera = registerForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { isSuccess ->
        if (isSuccess) {
            showImage()
        }
    }

    private fun showImage() {
        currentImageUri?.let {
            binding.ivPreview.setImageURI(it)
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }
}