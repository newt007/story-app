package com.elapp.storyapp.utils

import com.elapp.storyapp.data.local.entity.StoryEntity
import com.elapp.storyapp.data.model.Story
import com.elapp.storyapp.data.model.User
import com.elapp.storyapp.data.remote.auth.AuthBody
import com.elapp.storyapp.data.remote.auth.AuthResponse
import com.elapp.storyapp.data.remote.auth.LoginBody
import com.elapp.storyapp.data.remote.auth.RegisterResponse
import com.elapp.storyapp.data.remote.story.AddStoriesResponse
import com.elapp.storyapp.data.remote.story.GetStoriesResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody

object DataDummy {

    fun loginBodyDummy(): LoginBody {
        return LoginBody(
            email = "testinguserbintang",
            password = "123456"
        )
    }

    fun registerBodyDummy(): AuthBody {
        return AuthBody(
            name = "testingnamebintang",
            email = "testinguserbintang",
            password = "123456",
        )
    }

    fun loginResponseDummy(): AuthResponse {
        val loginResult = User(
            userId = "user-yj5pc_LARC_AgK61",
            name = "Arif Faizin",
            token = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VySWQiOiJ1c2VyLXlqNXBjX0xBUkNfQWdLNjEiLCJpYXQiOjE2NDE3OTk5NDl9.flEMaQ7zsdYkxuyGbiXjEDXO8kuDTcI__3UjCwt6R_I"
        )

        return AuthResponse(
            error = false,
            message = "success",
            loginResult = loginResult
        )
    }

    fun registerResponseDummy(): RegisterResponse {
        return RegisterResponse(
            error = false,
            message = "success"
        )
    }

    fun listStoryDummy(): List<StoryEntity> {
        val items = arrayListOf<StoryEntity>()
        for (i in 0 until 10) {
            val story = StoryEntity(
                id = "story-FvU4u0Vp2S3PMsFg",
                photoUrl = "https://story-api.dicoding.dev/images/stories/photos-1641623658595_dummy-pic.png",
                createdAt = "2022-01-08T06:34:18.598Z",
                name = "Testing user",
                description = "Lorem Ipsum",
                lon = -16.002,
                lat = -10.212
            )
            items.add(story)
        }
        return items
    }

    fun getStoriesResponseDummy(): GetStoriesResponse {
        val error = false
        val message = "Stories fetched successfully"
        val listStory = mutableListOf<Story>()

        for (i in 0 until 10) {
            val story = Story(
                id = "story-FvU4u0Vp2S3PMsFg",
                photoUrl = "https://story-api.dicoding.dev/images/stories/photos-1641623658595_dummy-pic.png",
                createdAt = "2022-01-08T06:34:18.598Z",
                name = "Testing user",
                description = "Lorem Ipsum",
                lon = -16.002,
                lat = -10.212
            )
            listStory.add(story)
        }
        return GetStoriesResponse(error, message, listStory)
    }

    fun multipartFileDummy(): MultipartBody.Part {
        val dummyText = "dummy"
        return MultipartBody.Part.create(dummyText.toRequestBody())
    }

    fun dataRequestBodyDummy(): RequestBody {
        val dummyText = "dummy"
        return dummyText.toRequestBody()
    }

    fun dataFileUploadResponseDummy(): AddStoriesResponse {
        return AddStoriesResponse(
            error = false,
            message = "success"
        )
    }

}