package com.elapp.storyapp.presentation.login

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.elapp.storyapp.data.remote.ApiResponse
import com.elapp.storyapp.data.remote.auth.AuthResponse
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
class LoginViewModelTest {

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @Mock
    private lateinit var authRepository: AuthRepository
    private lateinit var loginViewModel: LoginViewModel

    private val dummyLoginBody = DataDummy.loginBodyDummy()
    private val dummyLoginResponse = DataDummy.loginResponseDummy()

    @Before
    fun setUp() {
        authRepository = mock(authRepository::class.java)
        loginViewModel = LoginViewModel(authRepository)
    }

    @get:Rule
    var mainCoroutineRule = CoroutinesTestRule()

    @Test
    fun `Login success and get result success`(): Unit = mainCoroutineRule.runBlockingTest {
        val expectedResult = flow<ApiResponse<AuthResponse>> {
            emit(ApiResponse.Success(dummyLoginResponse))
        }

        `when`(loginViewModel.userLogin(dummyLoginBody)).thenReturn(expectedResult)

        loginViewModel.userLogin(dummyLoginBody).collect { result ->
            when(result) {
                is ApiResponse.Success -> {
                    Assert.assertNotNull(result)
                    Assert.assertSame(dummyLoginResponse, result.data)
                }
            }
        }
        verify(authRepository).loginUser(dummyLoginBody)
    }

    @Test
    fun `Login failed and get error result with Exception`(): Unit = mainCoroutineRule.runBlockingTest {
        val expectedResult = flowOf<ApiResponse<AuthResponse>>(ApiResponse.Error("failed to login"))

        `when`(loginViewModel.userLogin(dummyLoginBody)).thenReturn(expectedResult)

        loginViewModel.userLogin(dummyLoginBody).collect { result ->
            when(result) {
                is ApiResponse.Error -> {
                    Assert.assertNotNull(result)
                }
            }
        }
        verify(authRepository).loginUser(dummyLoginBody)
    }

}