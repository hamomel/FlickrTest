package com.example.hamom.flickrtest.presentation

import android.arch.lifecycle.ViewModel
import android.util.Log
import com.example.hamom.flickrtest.data.PhotosRepository
import com.example.hamom.flickrtest.data.local.model.Photos
import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable
import io.reactivex.Observable
import io.reactivex.Scheduler
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.processors.BehaviorProcessor
import io.reactivex.subjects.PublishSubject

class MainViewModel(private val repository: PhotosRepository, private val bgScheduler: Scheduler) : ViewModel() {

    companion object {
        private val TAG = MainViewModel::class.java.name
    }

    private val dataProcessor = BehaviorProcessor.create<MainViewData>()
    private val errorSubject = PublishSubject.create<Throwable>()
    private val loader = BehaviorProcessor.create<Pair<String, Int>>()
    private val disposable: CompositeDisposable = CompositeDisposable()

    init {
        disposable.add(
                repository.getSearchPhrases()
                        .observeOn(bgScheduler)
                        .subscribe(
                                { phrases -> mutateData { it.copy(searchPhrases = phrases) } },
                                { errorSubject.onNext(it) }))

        disposable.add(
                loader.startWith("" to 1)
                        .concatMap { loadPhotos(it.first, it.second) }
                        .subscribe(
                                { photos -> mutateData { it.copy(photos = photos, isLoading = false) } },
                                { errorSubject.onNext(it) }))
    }

    fun getData(): Flowable<MainViewData> = dataProcessor

    fun getErrors(): Observable<Throwable> = errorSubject

    fun searchPhotos(text: String) {
        loader.apply {
            text.takeIf { it != value?.first }
                ?.let {
                    mutateData { it.copy(photos = Photos()) }
                    onNext(text to 1)
                }
        }
    }

    fun loadNextPage(requestedPage: Int) {
        dataProcessor.value?.run {
            if (!isLoading
                && requestedPage <= photos.pages
                && requestedPage > loader.value?.second ?: 0) {

                Log.d(TAG, "curent page: ${photos.page}, requested page: ${requestedPage}")
                loader.onNext((loader.value?.first ?: "") to requestedPage)
            }
        }
    }

    private fun loadPhotos(text: String, pageNumger: Int): Flowable<Photos> =
            if (text.isBlank()) {
                repository.getRecentPhotos(pageNumger)
            } else {
                repository.searchPhotos(text, pageNumger)
            }.doOnSubscribe { mutateData { it.copy(isLoading = true) } }
                    .toFlowable(BackpressureStrategy.BUFFER)
                    .subscribeOn(bgScheduler)

    // this should be moved to base class in case we decide to develop this application
    private fun mutateData(mutation: (MainViewData) -> MainViewData) {
        dataProcessor.onNext(
                dataProcessor.value?.let {
                    mutation(it)
                } ?: mutation(MainViewData()))
    }

    override fun onCleared() {
        disposable.dispose()
        super.onCleared()
    }
}