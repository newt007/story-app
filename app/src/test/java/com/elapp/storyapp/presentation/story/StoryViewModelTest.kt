package com.elapp.storyapp.presentation.story

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import androidx.paging.AsyncPagingDataDiffer
import androidx.paging.ExperimentalPagingApi
import androidx.paging.PagingData
import androidx.recyclerview.widget.ListUpdateCallback
import com.elapp.storyapp.data.local.entity.StoryEntity
import com.elapp.storyapp.data.remote.ApiResponse
import com.elapp.storyapp.data.remote.story.AddStoriesResponse
import com.elapp.storyapp.data.remote.story.GetStoriesResponse
import com.elapp.storyapp.data.repository.StoryRepository
import com.elapp.storyapp.utils.CoroutinesTestRule
import com.elapp.storyapp.utils.DataDummy
import com.elapp.storyapp.utils.DataDummy.dataFileUploadResponseDummy
import com.elapp.storyapp.utils.DataDummy.getStoriesResponseDummy
import com.elapp.storyapp.utils.PagedTestDataSource
import com.elapp.storyapp.utils.getOrAwaitValue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.*
import org.junit.runner.*
import org.mockito.*
import org.mockito.Mockito.*
import org.mockito.junit.*

@ExperimentalPagingApi
@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class StoryViewModelTest {

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @Mock
    private lateinit var storyViewModel: StoryViewModel

    @Mock
    private lateinit var storyRepository: StoryRepository

    private var dataDummyToken = "testing_token"
    private val dummyFile = DataDummy.multipartFileDummy()
    private val dummyDescription = DataDummy.dataRequestBodyDummy()

    @Before
    fun setup() {
        storyRepository = mock(StoryRepository::class.java)
    }

    @get:Rule
    var mainCoroutineRule = CoroutinesTestRule()

    @Test
    fun `Get all stories successfully`() = mainCoroutineRule.runBlockingTest {
        val dataDummyStories = DataDummy.listStoryDummy()
        val data = PagedTestDataSource.snapshot(dataDummyStories)

        val dataStories = MutableLiveData<PagingData<StoryEntity>>()
        dataStories.value = data

        `when`(storyViewModel.getAllStories(dataDummyToken)).thenReturn(dataStories)

        val result = storyViewModel.getAllStories(dataDummyToken).getOrAwaitValue()
        val differ = AsyncPagingDataDiffer(
            diffCallback = StoryAdapter.DIFF_CALLBACK,
            updateCallback = listUpdateCallback,
            mainDispatcher = mainCoroutineRule.dispatcher,
            workerDispatcher = mainCoroutineRule.dispatcher
        )
        differ.submitData(result)

        advanceUntilIdle()

        verify(storyViewModel).getAllStories(dataDummyToken)

        Assert.assertNotNull(differ.snapshot())
        Assert.assertEquals(dataDummyStories.size, differ.snapshot().size)
    }

    @Test
    fun `Get stories with location successfully`() = mainCoroutineRule.runBlockingTest {
        val dataDummy = getStoriesResponseDummy()

        val data = MutableLiveData<ApiResponse<GetStoriesResponse>>()
        data.value = ApiResponse.Success(dataDummy)

        `when`(storyViewModel.getStoriesWithLocation(dataDummyToken, 1)).thenReturn(data)

        val actualStories = storyViewModel.getStoriesWithLocation(dataDummyToken, 1).getOrAwaitValue()
        verify(storyViewModel).getStoriesWithLocation(dataDummyToken, 1)

        advanceUntilIdle()

        Assert.assertNotNull(actualStories)
        Assert.assertTrue(actualStories is ApiResponse.Success)
    }

    @Test
    fun `Get stories with location error`() = mainCoroutineRule.runBlockingTest {
        val dataStories = MutableLiveData<ApiResponse<GetStoriesResponse>>()
        dataStories.value = ApiResponse.Error("get stories with location failed")

        `when`(storyViewModel.getStoriesWithLocation(dataDummyToken, 1)).thenReturn(dataStories)

        val actualStories = storyViewModel.getStoriesWithLocation(dataDummyToken, 1).getOrAwaitValue()
        verify(storyViewModel).getStoriesWithLocation(dataDummyToken, 1)

        advanceUntilIdle()

        Assert.assertNotNull(actualStories)
        Assert.assertTrue(actualStories is ApiResponse.Error)
    }

    @Test
    fun `Upload new story successfully`() = mainCoroutineRule.runBlockingTest{
        val dummyUploadResponse = dataFileUploadResponseDummy()

        val dataUploadResponse = MutableLiveData<ApiResponse<AddStoriesResponse>>()
        dataUploadResponse.value = ApiResponse.Success(dummyUploadResponse)

        `when`(storyViewModel.addNewStory(dataDummyToken, dummyFile, dummyDescription, null, null)).thenReturn(dataUploadResponse)

        val actualUploadResponse = storyViewModel.addNewStory(dataDummyToken, dummyFile, dummyDescription,  null, null).getOrAwaitValue()
        verify(storyViewModel).addNewStory(dataDummyToken, dummyFile, dummyDescription,  null, null)

        advanceUntilIdle()

        Assert.assertNotNull(actualUploadResponse)
        Assert.assertTrue(actualUploadResponse is ApiResponse.Success)

    }

    @Test
    fun `Upload new story failed`() = mainCoroutineRule.runBlockingTest {
        val dataUploadResponse = MutableLiveData<ApiResponse<AddStoriesResponse>>()
        dataUploadResponse.value = ApiResponse.Error("add story failed")

        `when`(storyViewModel.addNewStory(dataDummyToken, dummyFile, dummyDescription,  null, null)).thenReturn(dataUploadResponse)

        val actualUploadResponse = storyViewModel.addNewStory(dataDummyToken, dummyFile, dummyDescription,  null, null).getOrAwaitValue()
        verify(storyViewModel).addNewStory(dataDummyToken, dummyFile, dummyDescription,  null, null)

        advanceUntilIdle()

        Assert.assertNotNull(actualUploadResponse)
        Assert.assertTrue(actualUploadResponse is ApiResponse.Error)
    }

    private val listUpdateCallback = object : ListUpdateCallback {
        override fun onInserted(position: Int, count: Int) {}
        override fun onRemoved(position: Int, count: Int) {}
        override fun onMoved(fromPosition: Int, toPosition: Int) {}
        override fun onChanged(position: Int, count: Int, payload: Any?) {}
    }
}