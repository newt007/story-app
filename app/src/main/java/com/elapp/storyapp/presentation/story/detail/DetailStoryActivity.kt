package com.elapp.storyapp.presentation.story.detail

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import com.elapp.storyapp.R
import com.elapp.storyapp.R.string
import com.elapp.storyapp.data.local.entity.StoryEntity
import com.elapp.storyapp.databinding.ActivityDetailStoryBinding
import com.elapp.storyapp.utils.ConstVal
import com.elapp.storyapp.utils.ext.setImageUrl

class DetailStoryActivity : AppCompatActivity() {

    private var _activityDetailStoryBinding: ActivityDetailStoryBinding? = null
    private val binding get() = _activityDetailStoryBinding!!

    private lateinit var story: StoryEntity

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _activityDetailStoryBinding = ActivityDetailStoryBinding.inflate(layoutInflater)
        setContentView(_activityDetailStoryBinding?.root)

        initIntent()
        initUI()
        initToolbar()
    }

    private fun initUI() {
        binding.apply {
            imgStoryThumbnail.setImageUrl(story.photoUrl, true)
            tvStoryTitle.text = story.name
            tvStoryDesc.text = story.description
        }
        title = getString(string.title_detail_story)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    private fun initIntent() {
        story = intent.getParcelableExtra(ConstVal.BUNDLE_KEY_STORY)!!
    }

    private fun initToolbar() {
        binding.detailToolbar.apply {
            navigationIcon = AppCompatResources.getDrawable(context, R.drawable.ic_arrow_back)
        }
        setSupportActionBar(binding.detailToolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onNavigateUp()
    }

}