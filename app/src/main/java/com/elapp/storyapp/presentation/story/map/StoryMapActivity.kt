package com.elapp.storyapp.presentation.story.map

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.elapp.storyapp.R
import com.elapp.storyapp.R.string
import com.elapp.storyapp.data.remote.ApiResponse.Error
import com.elapp.storyapp.data.remote.ApiResponse.Loading
import com.elapp.storyapp.data.remote.ApiResponse.Success
import com.elapp.storyapp.databinding.ActivityStoryMapBinding
import com.elapp.storyapp.presentation.story.StoryViewModel
import com.elapp.storyapp.utils.SessionManager
import com.elapp.storyapp.utils.ext.showOKDialog
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

@AndroidEntryPoint
class StoryMapActivity : AppCompatActivity(), OnMapReadyCallback {

    private val storyViewModel: StoryViewModel by viewModels()

    private var _activityStoryMapBinding: ActivityStoryMapBinding? = null
    private val binding get() = _activityStoryMapBinding!!

    private lateinit var mMap: GoogleMap
    private lateinit var token: String
    private lateinit var pref: SessionManager

    companion object {

        fun start(context: Context) {
            val intent = Intent(context, StoryMapActivity::class.java)
            context.startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _activityStoryMapBinding = ActivityStoryMapBinding.inflate(layoutInflater)
        setContentView(binding.root)

        pref = SessionManager(this)
        token = "Bearer ${pref.getToken}"

        initUI()
    }

    private fun initUI() {
        title = getString(string.title_story_maps)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        storyViewModel.getStoriesWithLocation(token, 1).observe(this) { response ->
            when (response) {
                is Success -> {
                    response.data.listStory.forEach {
                        val latLng = LatLng(it.lat, it.lon)
                        mMap.addMarker(MarkerOptions().position(latLng).title(it.name))
                        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng))
                    }
                }
                is Error -> showOKDialog(getString(string.title_dialog_error), response.errorMessage)
                is Loading -> Timber.d(getString(string.message_loading_map))
                else -> { Timber.d(getString(string.message_unknown_state)) }
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }
}