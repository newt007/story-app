package com.elapp.storyapp

import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.intent.Intents.intended
import androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent
import androidx.test.espresso.matcher.ViewMatchers.hasDescendant
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.elapp.storyapp.utils.ConstVal.API_BASE_URL
import com.elapp.storyapp.utils.JsonConverter
import com.elapp.storyapp.utils.test.EspressoIdlingResource
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.HiltTestApplication
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.robolectric.annotation.Config
import org.junit.*
import org.junit.rules.*
import org.junit.runner.*

@MediumTest
@HiltAndroidTest
@Config(application = HiltTestApplication::class)
class MainActivityTest {

    @get:Rule
    val hiltRule: RuleChain = RuleChain.outerRule(HiltAndroidRule(this))
        .around(ActivityScenarioRule(MainActivity::class.java))

    @Before
    fun setup() {
        IdlingRegistry.getInstance().register(EspressoIdlingResource.countingIdlingResource)
        mockWebServer.start(8000)
        API_BASE_URL = "http://127.0.0.1:8080/"
    }

    @After
    fun teardown() {
        IdlingRegistry.getInstance().unregister(EspressoIdlingResource.countingIdlingResource)
    }

    private val mockWebServer = MockWebServer()

    @Test
    fun getStories_Success() {
        intended(hasComponent(MainActivity::class.java.name))
        val mockResponse = MockResponse()
            .setResponseCode(200)
            .setBody(JsonConverter.readStringFromFile("success_response.json"))
        mockWebServer.enqueue(mockResponse)
        onView(withId(R.id.rvStories))
            .check(matches(isDisplayed()))
        onView(withText("Bintang")).check(matches(isDisplayed()))
        onView(withId(R.id.rvStories))
            .perform(
                RecyclerViewActions.scrollTo<RecyclerView.ViewHolder>(
                    hasDescendant(withText("bambang"))
                )
            )
    }
}