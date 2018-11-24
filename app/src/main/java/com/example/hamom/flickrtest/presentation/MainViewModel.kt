package com.example.hamom.flickrtest.presentation

import android.arch.lifecycle.ViewModel
import com.example.hamom.flickrtest.data.PhotosRepository
import com.example.hamom.flickrtest.data.local.model.Photos
import io.reactivex.Observable
import io.reactivex.Scheduler
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject

class MainViewModel(private val repository: PhotosRepository, private val bgScheduler: Scheduler) : ViewModel() {

    private val dataSubject = BehaviorSubject.create<MainViewData>()
    private val errorSubject = PublishSubject.create<Throwable>()
    private val disposable: CompositeDisposable = CompositeDisposable()

    init {
        searchPhotos("")
        disposable.add(
                repository.getSearchPhrases()
                        .observeOn(bgScheduler)
                        .subscribe(
                                { phrases -> applyMutation { it.copy(searchPhrases = phrases) } },
                                { errorSubject.onNext(it) }))
    }

    fun getData(): Observable<MainViewData> = dataSubject

    fun getErrors(): Observable<Throwable> = errorSubject

    fun searchPhotos(text: String) {
        disposable.add(
                if (text.isBlank()) {
                    repository.getRecentPhotos()
                } else {
                    repository.searchPhotos(text)
                }.subscribeOn(bgScheduler)
                        .subscribe(
                                { photos -> applyMutation { it.copy(photos = photos.photo) } },
                                { errorSubject.onNext(it) })
        )
    }

    private fun applyMutation(mutation: (MainViewData) -> MainViewData) {
        dataSubject.onNext(
                dataSubject.value?.let {
                    mutation(it)
                } ?: mutation(MainViewData()))
    }

    override fun onCleared() {
        disposable.dispose()
        super.onCleared()
    }
}