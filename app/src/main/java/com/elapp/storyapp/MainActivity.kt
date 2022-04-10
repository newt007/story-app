package com.elapp.storyapp

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.View
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.elapp.storyapp.databinding.ActivityMainBinding
import com.elapp.storyapp.presentation.login.LoginActivity
import com.elapp.storyapp.presentation.story.add.AddStoryActivity
import com.elapp.storyapp.utils.ext.gone
import com.elapp.storyapp.utils.ext.show
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private var _activityMainBinding: ActivityMainBinding? = null
    private val binding get() = _activityMainBinding!!

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

        initAction()
    }

    private fun initAction() {
        binding.fabNewStory.setOnClickListener {
            AddStoryActivity.start(this)
        }
    }

}