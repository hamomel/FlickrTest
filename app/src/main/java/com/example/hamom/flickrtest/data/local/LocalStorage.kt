package com.example.hamom.flickrtest.data.local

import io.reactivex.Observable


interface LocalStorage {

    fun saveSearchPhrase(phrase: String)
    fun getSearchPhrases(): Observable<Set<String>>
}