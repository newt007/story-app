package com.elapp.storyapp

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.recyclerview.widget.LinearLayoutManager
import com.elapp.storyapp.R.string
import com.elapp.storyapp.data.local.entity.StoryEntity
import com.elapp.storyapp.databinding.ActivityMainBinding
import com.elapp.storyapp.presentation.other.LoadingStateAdapter
import com.elapp.storyapp.presentation.profile.ProfileActivity
import com.elapp.storyapp.presentation.story.StoryAdapter
import com.elapp.storyapp.presentation.story.StoryViewModel
import com.elapp.storyapp.presentation.story.add.AddStoryActivity
import com.elapp.storyapp.presentation.story.map.StoryMapActivity
import com.elapp.storyapp.utils.SessionManager
import com.elapp.storyapp.utils.ext.gone
import com.elapp.storyapp.utils.ext.hide
import com.elapp.storyapp.utils.ext.isTrue
import com.elapp.storyapp.utils.ext.show
import com.elapp.storyapp.utils.ext.toBearerToken
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.launch
import timber.log.Timber

@ExperimentalPagingApi
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

        initUI()
        initToolbar()
        initAdapter()
        initAction()

        getAllStories(token!!.toBearerToken())
    }

    private fun initUI() {
        binding.rvStories.layoutManager = LinearLayoutManager(this)
        binding.rvStories.layoutManager?.onRestoreInstanceState(binding.rvStories.layoutManager?.onSaveInstanceState())
        binding.tvGreetingName.text = getString(string.label_greeting_user, pref.getUserName)
    }

    private fun initToolbar() {
        binding.homeToolbar.apply {
            setBackgroundColor(ContextCompat.getColor(context, R.color.colorDarkBlue))
            title = getString(string.app_name)
            setTitleTextColor(ContextCompat.getColor(context, R.color.white))
            inflateMenu(R.menu.main_menu)
            setOnMenuItemClickListener {
                when (it.itemId) {
                    R.id.menuMap -> {
                        StoryMapActivity.start(this@MainActivity)
                        true
                    }
                    R.id.menuSetting -> {
                        context.startActivity(Intent(Settings.ACTION_LOCALE_SETTINGS))
                        true
                    }
                    else -> false
                }
            }
        }
    }

    private fun initAction() {
        binding.fabNewStory.setOnClickListener {
            AddStoryActivity.start(this)
        }
        binding.btnAccount.setOnClickListener {
            ProfileActivity.start(this)
        }
        binding.srStories.setOnRefreshListener {
            getAllStories("Bearer $token")
        }
    }

    private fun initAdapter() {
        adapter = StoryAdapter()
        binding.rvStories.layoutManager?.onRestoreInstanceState(binding.rvStories.layoutManager?.onSaveInstanceState())
        binding.rvStories.adapter = adapter
        lifecycleScope.launch {
            adapter.loadStateFlow.distinctUntilChanged { old, new ->
                old.mediator?.prepend?.endOfPaginationReached.isTrue() == new.mediator?.prepend?.endOfPaginationReached.isTrue()
            }
                .filter { it.refresh is LoadState.NotLoading && it.prepend.endOfPaginationReached && !it.append.endOfPaginationReached }
                .collect {
                    binding.rvStories.scrollToPosition(0)
                }
        }
        adapter.withLoadStateFooter(
            footer = LoadingStateAdapter {
                adapter.retry()
            }
        )
        adapter.addLoadStateListener { loadState ->
            when (loadState.refresh) {
                is LoadState.Loading -> {
                    isError(false)
                    isLoading(true)
                }
                is LoadState.NotLoading -> {
                    isError(false)
                    isLoading(false)
                }
                is LoadState.Error -> isError(true)
                else -> {
                    Timber.e(getString(string.message_unknown_state))
                }
            }
        }
    }

    private fun getAllStories(token: String) {
        storyViewModel.getAllStories(token).observe(this) { stories ->
            initRecyclerViewUpdate(stories)
        }
    }

    private fun initRecyclerViewUpdate(storiesData: PagingData<StoryEntity>) {
        val recyclerViewState = binding.rvStories.layoutManager?.onSaveInstanceState()

        adapter.submitData(lifecycle, storiesData)
        binding.srStories.isRefreshing = false

        binding.rvStories.layoutManager?.onRestoreInstanceState(recyclerViewState)
    }

    private fun isError(error: Boolean) {
        if (error) {
            binding.tvStoriesError.show()
            binding.rvStories.hide()
        } else {
            binding.tvStoriesError.hide()
            binding.rvStories.show()
        }
    }

    private fun isLoading(loading: Boolean) {
        if (loading) {
            binding.apply {
                rvStories.hide()
                shimmerLoading.show()
                shimmerLoading.startShimmer()
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
        adapter.refresh()
    }

}