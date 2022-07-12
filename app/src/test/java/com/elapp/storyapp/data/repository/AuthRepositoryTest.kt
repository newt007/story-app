package com.elapp.storyapp.data.repository

import com.elapp.storyapp.data.remote.ApiResponse
import com.elapp.storyapp.data.remote.auth.AuthResponse
import com.elapp.storyapp.data.remote.auth.AuthService
import com.elapp.storyapp.data.remote.auth.RegisterResponse
import com.elapp.storyapp.data.source.AuthDataSource
import com.elapp.storyapp.utils.CoroutinesTestRule
import com.elapp.storyapp.utils.DataDummy.loginBodyDummy
import com.elapp.storyapp.utils.DataDummy.loginResponseDummy
import com.elapp.storyapp.utils.DataDummy.registerBodyDummy
import com.elapp.storyapp.utils.DataDummy.registerResponseDummy
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.runBlocking
import org.junit.*
import org.junit.runner.*
import org.mockito.*
import org.mockito.Mockito.*
import org.mockito.junit.*

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class AuthRepositoryTest {

    @get:Rule
    var coroutineRuleTest = CoroutinesTestRule()

    private lateinit var apiService: AuthService

    @Mock
    private lateinit var authDataSource: AuthDataSource
    private lateinit var authRepository: AuthRepository

    @Before
    fun setUp() {
        apiService = mock(AuthService::class.java)
        authDataSource = AuthDataSource(apiService)
        authRepository = AuthRepository(authDataSource)
    }

    @Test
    fun `User login successfully`(): Unit = runBlocking {
        val expectedResult = flow<ApiResponse<AuthResponse>> {
            this.emit(ApiResponse.Success(loginResponseDummy()))
        }
        `when`(apiService.loginUser(loginBodyDummy())).thenReturn(loginResponseDummy())
        authRepository.loginUser(loginBodyDummy()).collect { result ->
            when (result) {
                is ApiResponse.Success -> {
                    Assert.assertNotNull(result)
                    expectedResult.collect {
                        Assert.assertEquals(it, result)
                    }
                }
                is ApiResponse.Error -> {
                    Assert.assertNull(result)
                }
            }
        }
    }

    @Test
    fun `User login failed and throw exception`(): Unit = runBlocking {
        `when`(apiService.loginUser(loginBodyDummy())).then { throw Exception() }
        authRepository.loginUser(loginBodyDummy()).collect { result ->
            when (result) {
                is ApiResponse.Error -> {
                    Assert.assertNotNull(result)
                }
            }
        }
    }

    @Test
    fun `User Register Success`() : Unit = runBlocking {
        val expectedResult = flow<ApiResponse<RegisterResponse>> {
            this.emit(ApiResponse.Success(registerResponseDummy()))
        }
        `when`(apiService.registerUser(registerBodyDummy())).thenReturn(registerResponseDummy())
        authRepository.registerUser(registerBodyDummy()).collect { result ->
            when (result) {
                is ApiResponse.Success -> {
                    Assert.assertNotNull(result)
                    expectedResult.collect {
                        Assert.assertEquals(it, result)
                    }
                }
                is ApiResponse.Error -> {
                    Assert.assertNull(result)
                }
            }
        }
    }

    @Test
    fun `User Register failed and throw exception`(): Unit = runBlocking {
        `when`(apiService.registerUser(registerBodyDummy())).then { throw Exception() }
        authRepository.registerUser(registerBodyDummy()).collect { result ->
            when (result) {
                is ApiResponse.Error -> {
                    Assert.assertNotNull(result)
                }
            }
        }
    }

}