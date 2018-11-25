package com.example.hamom.flickrtest.data

import com.example.hamom.flickrtest.data.local.LocalStorage
import com.example.hamom.flickrtest.data.local.model.Photos
import com.example.hamom.flickrtest.data.remote.ApiError
import com.example.hamom.flickrtest.data.remote.FlickrApi
import com.example.hamom.flickrtest.data.remote.model.PhotosResponse
import com.example.hamom.flickrtest.data.remote.model.PhotosResponseMapper
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import io.reactivex.Observable
import org.junit.Before
import org.junit.Test

class PhotosRepositoryTest {

    private val mockApi: FlickrApi = mockk()
    private val mockLocalStorage: LocalStorage = mockk(relaxed = true)
    private val mockResponseMapper: PhotosResponseMapper = mockk()
    private val mockPhotosResponse: PhotosResponse = mockk()
    private val mockPhotos: Photos = mockk()

    private val testSearchPhrase = "search"
    private val testPageNum = 1
    private val testResponse = Observable.just(mockPhotosResponse)
    private val testError = Throwable("404")
    private val testErrorResponse = Observable.error<PhotosResponse>(testError)
    private val testFailStat = "fail"
    private val repository = PhotosRepository(mockApi, mockLocalStorage, mockResponseMapper)

    @Before
    fun setUp() {
        every { mockApi.getRecentPhotos(any()) } returns testResponse
        every { mockApi.searchPhotos(any(), any()) } returns testResponse
        every { mockResponseMapper.map(any()) } returns mockPhotos
        every { mockPhotosResponse.stat } returns "ok"
    }

    @Test
    fun `get recent photos success`() {
        repository.getRecentPhotos(testPageNum).test().assertValue {
            it == mockPhotos
        }

        verify(exactly = 1) { mockApi.getRecentPhotos(testPageNum) }
        verify(exactly = 1) { mockResponseMapper.map(mockPhotosResponse) }
    }

    @Test
    fun `get recent connection error`() {
        every { mockApi.getRecentPhotos(any()) } returns testErrorResponse

        repository.getRecentPhotos(testPageNum).test().assertError(testError)

        verify(exactly = 1) { mockApi.getRecentPhotos(testPageNum) }
    }

    @Test
    fun `get recent api error`() {
        every { mockApi.getRecentPhotos(any()) } returns testResponse
        every { mockPhotosResponse.stat } returns testFailStat
        every { mockPhotosResponse.message } returns testFailStat

        repository.getRecentPhotos(testPageNum).test().assertError {
            it is ApiError && it.message == testFailStat
        }

        verify(exactly = 1) { mockApi.getRecentPhotos(testPageNum) }
    }

    @Test
    fun `search photos success`() {
        repository.searchPhotos(testSearchPhrase, testPageNum).test().assertValue {
            it == mockPhotos
        }

        verify(exactly = 1) { mockLocalStorage.saveSearchPhrase(testSearchPhrase) }
        verify(exactly = 1) { mockApi.searchPhotos(testSearchPhrase, testPageNum) }
        verify(exactly = 1) { mockResponseMapper.map(mockPhotosResponse) }
    }

    @Test
    fun `search photos connection error`() {
        every { mockApi.searchPhotos(any(), any()) } returns testErrorResponse

        repository.searchPhotos(testSearchPhrase, testPageNum).test().assertError(testError)

        verify(exactly = 1) { mockLocalStorage.saveSearchPhrase(testSearchPhrase) }
        verify(exactly = 1) { mockApi.searchPhotos(testSearchPhrase, testPageNum) }
    }

    @Test
    fun `search photos api error`() {
        every { mockApi.searchPhotos(any(), any()) } returns testResponse
        every { mockPhotosResponse.stat } returns testFailStat
        every { mockPhotosResponse.message } returns testFailStat

        repository.searchPhotos(testSearchPhrase, testPageNum).test().assertError {
            it is ApiError && it.message == testFailStat
        }

        verify(exactly = 1) { mockLocalStorage.saveSearchPhrase(testSearchPhrase) }
        verify(exactly = 1) { mockApi.searchPhotos(testSearchPhrase, testPageNum) }
    }
}