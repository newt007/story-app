package com.elapp.storyapp

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.Menu
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import com.elapp.storyapp.R.string
import com.elapp.storyapp.databinding.ActivityMainBinding
import com.elapp.storyapp.presentation.profile.ProfileActivity
import com.elapp.storyapp.presentation.story.StoryAdapter
import com.elapp.storyapp.presentation.story.StoryViewModel
import com.elapp.storyapp.presentation.story.add.AddStoryActivity
import com.elapp.storyapp.presentation.story.map.StoryMapActivity
import com.elapp.storyapp.utils.SessionManager
import com.elapp.storyapp.utils.ext.gone
import com.elapp.storyapp.utils.ext.hide
import com.elapp.storyapp.utils.ext.show
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private val storyViewModel: StoryViewModel by viewModels()

    private var _activityMainBinding: ActivityMainBinding? = null
    private val binding get() = _activityMainBinding!!

    private lateinit var pref: SessionManager
    private var token: String? = null

    private lateinit var adapter: StoryAdapter

    companion object {

        fun start(context: Context) {
            val intent = Intent(context, MainActivity::class.java)
            context.startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _activityMainBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(_activityMainBinding?.root)
        pref = SessionManager(this)
        token = pref.getToken

        initAction()
        initUI()
        initAdapter()

        getAllStories("Bearer $token")
    }

    private fun initUI() {
        binding.rvStories.layoutManager = LinearLayoutManager(this)
        binding.tvGreetingName.text = getString(string.label_greeting_user, pref.getUserName)
    }

    private fun initAction() {
        binding.fabNewStory.setOnClickListener {
            AddStoryActivity.start(this)
        }
        binding.btnAccount.setOnClickListener {
            ProfileActivity.start(this)
        }
    }

    private fun getAllStories(token: String) {
        storyViewModel.getAllStories(token).observe(this@MainActivity) { stories ->
            adapter.submitData(lifecycle ,stories)
        }
    }

    private fun initAdapter() {
        adapter = StoryAdapter()
        binding.rvStories.adapter = adapter
        adapter.addLoadStateListener { loadState ->
            when (loadState.refresh) {
                is LoadState.Loading -> isLoading(true)
                is LoadState.NotLoading -> isLoading(false)
                else -> {
                    Timber.e(getString(string.message_unknown_state))
                }
            }
        }
    }

    private fun isLoading(loading: Boolean) {
        if (loading) {
            binding.apply {
                shimmerLoading.show()
                shimmerLoading.startShimmer()
                rvStories.hide()
            }
        } else {
            binding.apply {
                rvStories.show()
                shimmerLoading.stopShimmer()
                shimmerLoading.gone()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        getAllStories("Bearer $token")
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menuSetting -> {
                startActivity(Intent(Settings.ACTION_LOCALE_SETTINGS))
            }
            R.id.menuMap -> {
                StoryMapActivity.start(this)
            }
        }
        return super.onOptionsItemSelected(item)
    }
}