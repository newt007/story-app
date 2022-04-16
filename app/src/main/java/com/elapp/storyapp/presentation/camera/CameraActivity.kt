package com.elapp.storyapp.presentation.camera

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCapture.OutputFileResults
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import com.elapp.storyapp.R.string
import com.elapp.storyapp.databinding.ActivityCameraBinding
import com.elapp.storyapp.utils.ConstVal.CAMERA_X_RESULT
import com.elapp.storyapp.utils.ConstVal.KEY_IS_BACK_CAMERA
import com.elapp.storyapp.utils.ConstVal.KEY_PICTURE
import com.elapp.storyapp.utils.createFile
import com.elapp.storyapp.utils.ext.showToast
import timber.log.Timber
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class CameraActivity : AppCompatActivity() {

    private var _activityCameraBinding: ActivityCameraBinding? = null
    private val binding get() = _activityCameraBinding!!

    private lateinit var cameraExecutor: ExecutorService
    private var cameraSelector: CameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
    private var imageCapture: ImageCapture? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _activityCameraBinding = ActivityCameraBinding.inflate(layoutInflater)
        setContentView(_activityCameraBinding?.root)

        initExecutor()
        initAction()
    }

    override fun onResume() {
        super.onResume()
        startCamera()
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown()
    }

    private fun initAction() {
        binding.apply {
            captureImage.setOnClickListener {
                takePhoto()
            }
        }
    }

    private fun initExecutor() {
        cameraExecutor = Executors.newSingleThreadExecutor()
    }

    private fun takePhoto() {
        val imageCapture = imageCapture ?: return
        val photoFile = createFile(application)

        val outputOption = ImageCapture.OutputFileOptions.Builder(photoFile).build()
        imageCapture.takePicture(
            outputOption,
            ContextCompat.getMainExecutor(this),
            object: ImageCapture.OnImageSavedCallback {
                override fun onImageSaved(outputFileResults: OutputFileResults) {
                    val intent = Intent()
                    intent.putExtra(KEY_PICTURE, photoFile)
                    intent.putExtra(KEY_IS_BACK_CAMERA, cameraSelector == CameraSelector.DEFAULT_BACK_CAMERA)
                    setResult(CAMERA_X_RESULT, intent)
                    finish()
                }

                override fun onError(exception: ImageCaptureException) {
                    showToast(getString(string.message_failed_take_picture))
                    Timber.e(exception.message.toString())
                }
            }
        )
    }

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)

        cameraProviderFuture.addListener({
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()
            val preview = Preview.Builder()
                .build()
                .also {
                    it.setSurfaceProvider(binding.viewFinder.surfaceProvider)
                }
            imageCapture = ImageCapture.Builder().build()

            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(
                    this,
                    cameraSelector,
                    preview,
                    imageCapture
                )
            } catch (ex: Exception) {
                Toast.makeText(this, "Gagal memunculkan kamera", Toast.LENGTH_SHORT).show()
                Timber.e(ex.message)
            }
        }, ContextCompat.getMainExecutor(this))
    }
}