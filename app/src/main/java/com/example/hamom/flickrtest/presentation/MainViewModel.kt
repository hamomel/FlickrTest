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

    private val dataProcessor = BehaviorProcessor.create<MainViewData>()
    private val errorSubject = PublishSubject.create<Throwable>()
    private val paginator = BehaviorProcessor.create<Pair<String, Int>>()
    private val disposable: CompositeDisposable = CompositeDisposable()

    init {
        disposable.add(
                repository.getSearchPhrases()
                        .observeOn(bgScheduler)
                        .subscribe(
                                { phrases -> mutateData { it.copy(searchPhrases = phrases) } },
                                { errorSubject.onNext(it) }))

        disposable.add(
                paginator.startWith("" to 1)
                        .concatMap { loadPhotos(it.first, it.second) }
                        .subscribe(
                                { photos -> mutateData { it.copy(photos = photos, isLoading = false) } },
                                { errorSubject.onNext(it) }))
    }

    fun getData(): Flowable<MainViewData> = dataProcessor

    fun getErrors(): Observable<Throwable> = errorSubject

    private fun loadPhotos(text: String, pageNumger: Int): Flowable<Photos> =
            if (text.isBlank()) {
                repository.getRecentPhotos(pageNumger)
            } else {
                repository.searchPhotos(text, pageNumger)
            }.doOnSubscribe { mutateData { it.copy(isLoading = true, searchPhrase = text) } }
                    .toFlowable(BackpressureStrategy.BUFFER)
                    .subscribeOn(bgScheduler)

    // this should be moved to base class in case we decide to develop this application
    private fun mutateData(mutation: (MainViewData) -> MainViewData) {
        dataProcessor.onNext(
                dataProcessor.value?.let {
                    mutation(it)
                } ?: mutation(MainViewData()))
    }

    fun searchPhotos(text: String) {
        if (text != dataProcessor.value?.searchPhrase) {
            mutateData { it.copy(photos = Photos()) }
        }
        paginator.apply {
            text.takeIf { it != value?.first }
                    ?.let {
                        onNext(text to 1)
                    }
        }
    }

    fun loadNextPage(requestedPage: Int) {
        dataProcessor.value?.run {
            if (!isLoading
                    && requestedPage <= photos.total / photos.pageSize
                    && requestedPage > paginator.value?.second ?: 0) {
                Log.d(MainViewModel::class.java.name, "curent page: ${photos.page}, load page: ${photos.page + 1}")
                paginator.onNext(searchPhrase to requestedPage)
            }
        }
    }

    override fun onCleared() {
        disposable.dispose()
        super.onCleared()
    }
}