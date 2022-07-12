package com.elapp.storyapp.presentation.register

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.elapp.storyapp.data.remote.ApiResponse
import com.elapp.storyapp.data.remote.auth.RegisterResponse
import com.elapp.storyapp.data.repository.AuthRepository
import com.elapp.storyapp.utils.CoroutinesTestRule
import com.elapp.storyapp.utils.DataDummy
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runBlockingTest
import org.junit.*
import org.junit.runner.*
import org.mockito.*
import org.mockito.Mockito.*
import org.mockito.junit.*

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class RegisterViewModelTest {

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @Mock
    private lateinit var authRepository: AuthRepository
    private lateinit var registerViewModel: RegisterViewModel

    private val dummyRegisterBody = DataDummy.registerBodyDummy()
    private val dummyRegisterResponse = DataDummy.registerResponseDummy()

    @Before
    fun setUp() {
        authRepository = mock(authRepository::class.java)
        registerViewModel = RegisterViewModel(authRepository)
    }

    @get:Rule
    var mainCoroutineRule = CoroutinesTestRule()

    @Test
    fun `Register success and get result success`(): Unit = mainCoroutineRule.runBlockingTest {
        val expectedResult = flow<ApiResponse<RegisterResponse>> {
            emit(ApiResponse.Success(dummyRegisterResponse))
        }

        `when`(registerViewModel.userRegister(dummyRegisterBody)).thenReturn(expectedResult)
        registerViewModel.userRegister(dummyRegisterBody).collect { result ->
            when (result) {
                is ApiResponse.Success -> {
                    Assert.assertNotNull(result)
                    Assert.assertSame(dummyRegisterResponse, result.data)
                }
            }
        }
        verify(authRepository).registerUser(dummyRegisterBody)
    }

    @Test
    fun `Register failed and get error result with exception`(): Unit = mainCoroutineRule.runBlockingTest {
        val expectedResult = flowOf<ApiResponse<RegisterResponse>>(ApiResponse.Error("failed"))

        `when`(registerViewModel.userRegister(dummyRegisterBody)).thenReturn(expectedResult)

        registerViewModel.userRegister(dummyRegisterBody).collect { result ->
            when (result) {
                is ApiResponse.Error -> {
                    Assert.assertNotNull(result)
                }
            }
        }

        verify(authRepository).registerUser(dummyRegisterBody)
    }
}