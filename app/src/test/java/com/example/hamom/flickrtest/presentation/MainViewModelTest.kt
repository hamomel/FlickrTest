package com.example.hamom.flickrtest.presentation

import com.example.hamom.flickrtest.data.PhotosRepository
import com.example.hamom.flickrtest.data.local.model.Photos
import com.example.hamom.flickrtest.data.remote.ApiError
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test

class MainViewModelTest {

    private val testScheduler = Schedulers.trampoline()
    private val mockRepository: PhotosRepository = mockk()
    private val firstMockPhotos: Photos = mockk(relaxed = true)
    private val secondMockPhotos: Photos = mockk(relaxed = true)
    private val testSearchPhrase = "search"
    private val mockSearchPhrases: Set<String> = mockk(relaxed = true)

    private lateinit var viewModel: MainViewModel

    @Before
    fun setUp() {
        every { firstMockPhotos.page } returns 1
        every { firstMockPhotos.pages } returns 100
        every { mockRepository.searchPhotos(any(), any()) } returns Observable.just(firstMockPhotos)
        every { mockRepository.getRecentPhotos(any()) } returns Observable.just(firstMockPhotos)
        every { mockRepository.getSearchPhrases() } returns Observable.just(mockSearchPhrases)
        every { mockRepository.searchPhotos(testSearchPhrase, any()) } returns Observable.just(secondMockPhotos)

        viewModel  = MainViewModel(mockRepository, testScheduler)
    }

    @Test
    fun `get data initial`() {
        viewModel.getData().test().assertValue {
            it.photos == firstMockPhotos
            && !it.isLoading
            && it.searchPhrases == mockSearchPhrases
        }

        verify(exactly = 1) { mockRepository.getRecentPhotos(1) }
    }

    @Test
    fun `get errors initial`() {
        viewModel.getErrors().test().assertEmpty()
    }

    @Test
    fun `load next page success`() {
        viewModel.getData().test().assertValue {
            it.photos == firstMockPhotos
        }

        every { mockRepository.getRecentPhotos(2) } returns Observable.just(secondMockPhotos)

        viewModel.loadNextPage(2)

        viewModel.getData().test().assertValue {
            it.photos == secondMockPhotos
        }

        verify(exactly = 1) { mockRepository.getRecentPhotos(2) }
    }

    @Test
    fun `search photos success`() {
        val fetchedValus: MutableList<MainViewData> = mutableListOf()

        viewModel.getData().subscribe {
            fetchedValus.add(it)
        }

        viewModel.searchPhotos(testSearchPhrase)

        assertEquals(firstMockPhotos, fetchedValus[0].photos)
        assertNotNull(fetchedValus.find {  it.photos == secondMockPhotos})
        verify(exactly = 1) { mockRepository.searchPhotos(testSearchPhrase, 1) }
    }
}