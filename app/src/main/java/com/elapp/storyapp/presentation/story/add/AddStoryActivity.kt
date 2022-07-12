package com.elapp.storyapp.presentation.story.add

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.Intent.ACTION_GET_CONTENT
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.paging.ExperimentalPagingApi
import com.elapp.storyapp.R
import com.elapp.storyapp.R.string
import com.elapp.storyapp.data.remote.ApiResponse
import com.elapp.storyapp.databinding.ActivityAddStoryBinding
import com.elapp.storyapp.presentation.camera.CameraActivity
import com.elapp.storyapp.presentation.story.StoryViewModel
import com.elapp.storyapp.utils.ConstVal.CAMERA_X_RESULT
import com.elapp.storyapp.utils.ConstVal.KEY_PICTURE
import com.elapp.storyapp.utils.ConstVal.REQUEST_CODE_PERMISSIONS
import com.elapp.storyapp.utils.SessionManager
import com.elapp.storyapp.utils.ext.gone
import com.elapp.storyapp.utils.ext.show
import com.elapp.storyapp.utils.ext.showOKDialog
import com.elapp.storyapp.utils.ext.showToast
import com.elapp.storyapp.utils.reduceFileImage
import com.elapp.storyapp.utils.uriToFile
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

@ExperimentalPagingApi
@AndroidEntryPoint
class AddStoryActivity : AppCompatActivity() {

    private val storyViewModel: StoryViewModel by viewModels()

    private var _activityAddStoryBinding: ActivityAddStoryBinding? = null
    private val binding get() = _activityAddStoryBinding!!

    private var uploadFile: File? = null
    private var token: String? = null
    private var currentLocation: Location? = null

    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private lateinit var pref: SessionManager

    companion object {

        fun start(context: Context) {
            val intent = Intent(context, AddStoryActivity::class.java)
            context.startActivity(intent)
        }

        private val REQUIRED_PERMISSIONS =
            arrayOf(Manifest.permission.CAMERA)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _activityAddStoryBinding = ActivityAddStoryBinding.inflate(layoutInflater)
        setContentView(_activityAddStoryBinding?.root)

        pref = SessionManager(this)
        token = pref.getToken
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        if (!allPermissionsGranted()) {
            ActivityCompat.requestPermissions(
                this,
                REQUIRED_PERMISSIONS,
                REQUEST_CODE_PERMISSIONS
            )
        }

        initUI()
        initToolbar()
        initAction()
    }

    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->
            when {
                permissions[Manifest.permission.ACCESS_FINE_LOCATION] ?: false -> {
                    getMyLastLocation()
                }
                permissions[Manifest.permission.ACCESS_COARSE_LOCATION] ?: false -> {
                    getMyLastLocation()
                }
                else -> {
                    Snackbar
                        .make(
                            binding.root,
                            getString(string.message_location_not_found),
                            Snackbar.LENGTH_SHORT
                        )
                        .setActionTextColor(ContextCompat.getColor(this, R.color.white))
                        .setAction(getString(string.action_change_setting)) {
                            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                            val uri = Uri.fromParts("package", packageName, null)
                            intent.data = uri
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                            startActivity(intent)
                        }
                        .show()

                    binding.cbShareLocation.isChecked = false
                }
            }
        }

    private fun checkPermission(permission: String): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            permission
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun getMyLastLocation() {
        if (checkPermission(Manifest.permission.ACCESS_FINE_LOCATION) &&
            checkPermission(Manifest.permission.ACCESS_COARSE_LOCATION)
        ) {
            fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                if (location != null) {
                    currentLocation = location
                    showToast("Location ${currentLocation!!.longitude} ${currentLocation!!.latitude}")
                } else {
                    showToast(getString(string.message_location_not_found))

                    binding.cbShareLocation.isChecked = false
                }
            }
        } else {
            requestPermissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (!allPermissionsGranted()) {
            showToast(getString(string.message_not_permitted))
        }
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

    private fun initToolbar() {
        binding.toolbar.apply {
            navigationIcon = AppCompatResources.getDrawable(context, R.drawable.ic_arrow_back)
        }
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    private fun initAction() {
        binding.btnOpenCamera.setOnClickListener {
            val intent = Intent(this, CameraActivity::class.java)
            launchIntentCamera.launch(intent)
        }
        binding.btnOpenGallery.setOnClickListener {
            val intent = Intent()
            intent.action = ACTION_GET_CONTENT
            intent.type = "image/*"
            val chooser = Intent.createChooser(intent, getString(string.title_choose_a_picture))
            launchIntentGallery.launch(chooser)
        }
        binding.btnUpload.setOnClickListener {
            uploadImage()
        }
        binding.cbShareLocation.setOnCheckedChangeListener { _, checked ->
            if (checked) {
                getMyLastLocation()
            } else {
                currentLocation = null
            }
        }
    }

    private fun initUI() {
        title = getString(string.title_new_story)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    private val launchIntentCamera = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == CAMERA_X_RESULT) {
            val file = it?.data?.getSerializableExtra(KEY_PICTURE) as File

            uploadFile = file

            val result = BitmapFactory.decodeFile(file.path)

            binding.imgPreview.setImageBitmap(result)
        }
    }

    private val launchIntentGallery = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val selectedImg: Uri = result.data?.data as Uri
            val file = uriToFile(selectedImg, this)

            uploadFile = file
            binding.imgPreview.setImageURI(selectedImg)
        }
    }

    private fun uploadImage() {
        if (uploadFile != null) {
            val file = reduceFileImage(uploadFile as File)
            val description = binding.edtStoryDesc.text
            if (description.isBlank()) {
                binding.edtStoryDesc.requestFocus()
                binding.edtStoryDesc.error = getString(string.error_desc_empty)
            } else {
                val descMediaTyped = description.toString().toRequestBody("text/plain".toMediaType())
                val requestImageFile = file.asRequestBody("image/jpeg".toMediaTypeOrNull())
                val imageMultipart = MultipartBody.Part.createFormData(
                    "photo",
                    file.name,
                    requestImageFile
                )

                var latitude: RequestBody? = null
                var longitude: RequestBody? = null

                if (currentLocation != null) {
                    longitude = currentLocation?.longitude.toString().toRequestBody("text/plain".toMediaType())
                    latitude = currentLocation?.latitude.toString().toRequestBody("text/plain".toMediaType())
                }

                storyViewModel.addNewStory("Bearer $token", imageMultipart, descMediaTyped, latitude, longitude).observe(this) { response ->
                    when (response) {
                        is ApiResponse.Loading -> {
                            showLoading(true)
                        }
                        is ApiResponse.Success -> {
                            showLoading(false)
                            showToast(getString(string.message_success_upload))
                            finish()
                        }
                        is ApiResponse.Error -> {
                            showLoading(false)
                            showOKDialog(getString(string.title_upload_info), response.errorMessage)
                        }
                        else -> {
                            showLoading(false)
                            showToast(getString(string.message_unknown_state))
                        }
                    }
                }
            }
        } else {
            showOKDialog(getString(string.title_message), getString(string.message_pick_image))
        }
    }

    private fun showLoading(isLoading: Boolean) {
        if (isLoading) binding.progressBar.show() else binding.progressBar.gone()
        if (isLoading) binding.bgDim.show() else binding.bgDim.gone()
        binding.apply {
            btnUpload.isClickable = !isLoading
            btnUpload.isEnabled = !isLoading
            btnOpenGallery.isClickable = !isLoading
            btnOpenGallery.isEnabled = !isLoading
            btnOpenCamera.isClickable = !isLoading
            btnOpenCamera.isEnabled = !isLoading
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }
}