package com.elapp.storyapp.data.repository

import androidx.paging.AsyncPagingDataDiffer
import androidx.paging.ExperimentalPagingApi
import androidx.recyclerview.widget.ListUpdateCallback
import com.elapp.storyapp.data.local.StoryAppDatabase
import com.elapp.storyapp.data.remote.ApiResponse
import com.elapp.storyapp.data.remote.story.GetStoriesResponse
import com.elapp.storyapp.data.remote.story.StoryService
import com.elapp.storyapp.data.source.StoryDataSource
import com.elapp.storyapp.presentation.story.StoryAdapter
import com.elapp.storyapp.utils.CoroutinesTestRule
import com.elapp.storyapp.utils.DataDummy
import com.elapp.storyapp.utils.DataDummy.dataFileUploadResponseDummy
import com.elapp.storyapp.utils.DataDummy.dataRequestBodyDummy
import com.elapp.storyapp.utils.DataDummy.getStoriesResponseDummy
import com.elapp.storyapp.utils.DataDummy.multipartFileDummy
import com.elapp.storyapp.utils.PagedTestDataSource
import com.elapp.storyapp.utils.ext.toBearerToken
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import org.junit.*
import org.junit.runner.*
import org.mockito.*
import org.mockito.Mockito.*
import org.mockito.junit.*

@ExperimentalPagingApi
@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class StoryRepositoryTest {

    @get:Rule
    var coroutineRuleTest = CoroutinesTestRule()

    @Mock
    private lateinit var storyDatabase: StoryAppDatabase

    private lateinit var apiService: StoryService

    @Mock
    private lateinit var storyDataSource: StoryDataSource

    @Mock
    private lateinit var storyDataSourceMock: StoryDataSource

    @Mock
    private lateinit var storyRepositoryMock: StoryRepository

    @Mock
    private lateinit var storyRepository: StoryRepository

    private var dataDummyToken = "testing_token"

    private val dummyFile = multipartFileDummy()
    private val dummyDescription = dataRequestBodyDummy()

    @Before
    fun setUp() {
        apiService = mock(StoryService::class.java)
        storyDatabase = mock(StoryAppDatabase::class.java)

        storyDataSource = StoryDataSource(storyDatabase, apiService)
        storyDataSourceMock = mock(StoryDataSource::class.java)

        storyRepository = mock(StoryRepository::class.java)
        storyRepositoryMock = StoryRepository(storyDataSource)
    }

    @Test
    fun `Get stories_successfully`() = runBlocking {
        val dataDummyStories = DataDummy.listStoryDummy()
        val data = PagedTestDataSource.snapshot(dataDummyStories)

        val expectedResult = flowOf(data)

        `when`(storyRepository.getAllStories(dataDummyToken)).thenReturn(expectedResult)

        storyRepository.getAllStories(dataDummyToken).collect { result ->
            val differ = AsyncPagingDataDiffer(
                diffCallback = StoryAdapter.DIFF_CALLBACK,
                updateCallback = listUpdateCallback,
                mainDispatcher = coroutineRuleTest.dispatcher,
                workerDispatcher = coroutineRuleTest.dispatcher
            )
            differ.submitData(result)
            Assert.assertNotNull(differ.snapshot())
            Assert.assertEquals(dataDummyStories.size, differ.snapshot().size)
        }
    }

    @Test
    fun `Get stories with location_successfully`(): Unit = runBlocking {
        val exceptedResult = flowOf<ApiResponse<GetStoriesResponse>>(ApiResponse.Success(getStoriesResponseDummy()))

        `when`(storyRepository.getStoriesWithLocation(dataDummyToken, 1)).thenReturn(exceptedResult)

        storyRepository.getStoriesWithLocation(dataDummyToken, 1).collect { result ->
            when (result) {
                is ApiResponse.Success -> {
                    Assert.assertNotNull(result)

                    exceptedResult.collect {
                        Assert.assertEquals(it, result)
                    }
                }
            }
        }
    }

    @Test
    fun `Get stories with location_getException`() = runBlocking {
        val expectedResponse = flowOf<ApiResponse<GetStoriesResponse>>(ApiResponse.Error("failed"))
        `when`(storyRepository.getStoriesWithLocation(dataDummyToken, 1)).thenReturn(expectedResponse)
        storyRepository.getStoriesWithLocation(dataDummyToken, 1).collect { result ->
            when (result) {
                is ApiResponse.Error -> {
                    Assert.assertNotNull(result)
                }
            }
        }
    }

    @Test
    fun `Upload new story_successfully`(): Unit = runBlocking {
//        val expectedResult = flowOf<ApiResponse<AddStoriesResponse>>(ApiResponse.Success(generateDummyFileUploadResponse()))

        val expectedResult = dataFileUploadResponseDummy()
        `when`(
            apiService.addNewStories(
                dataDummyToken.toBearerToken(),
                dummyFile,
                dummyDescription,
                null,
                null
            )
        ).thenReturn(expectedResult)

        storyRepositoryMock.addNewStory(
            dataDummyToken.toBearerToken(),
            dummyFile,
            dummyDescription,
            null,
            null
        ).collect { result ->
            when (result) {
                is ApiResponse.Success -> {
                    Assert.assertEquals(expectedResult, result.data)
                }
            }
        }

        verify(apiService).addNewStories(
            dataDummyToken.toBearerToken(),
            dummyFile,
            dummyDescription,
            null,
            null
        )
    }

    @Test
    fun `Upload image file_getException`(): Unit = runBlocking {
        `when`(
            apiService.addNewStories(
                dataDummyToken.toBearerToken(), dummyFile, dummyDescription, null, null
            )
        ).then { throw Exception() }

        storyRepositoryMock.addNewStory(dataDummyToken.toBearerToken(), dummyFile, dummyDescription, null, null).collect { result ->
            when (result) {
                is ApiResponse.Error -> {
                    Assert.assertNotNull(result)
                }
            }
        }
        verify(apiService).addNewStories(
            dataDummyToken.toBearerToken(),
            dummyFile,
            dummyDescription,
            null,
            null
        )
    }

    private val listUpdateCallback = object : ListUpdateCallback {
        override fun onInserted(position: Int, count: Int) {}
        override fun onRemoved(position: Int, count: Int) {}
        override fun onMoved(fromPosition: Int, toPosition: Int) {}
        override fun onChanged(position: Int, count: Int, payload: Any?) {}
    }
}