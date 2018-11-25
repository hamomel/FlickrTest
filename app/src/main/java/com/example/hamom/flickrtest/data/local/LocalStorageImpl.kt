package com.example.hamom.flickrtest.data.local

import android.content.Context
import android.content.SharedPreferences
import io.reactivex.Observable

class LocalStorageImpl(context: Context) : LocalStorage {

    companion object {
        private const val SEARCH_PREFS = "SEARCH_PREFS"
        private const val SEARCH_PHRASES = "SEARCH_PHRASES"
    }

    private val preferences: SharedPreferences =
            context.getSharedPreferences(SEARCH_PREFS, Context.MODE_PRIVATE)

    private val changesListener: Observable<String> = Observable.create { emitter ->
        val listener = SharedPreferences.OnSharedPreferenceChangeListener { _, key ->
            emitter.onNext(key)
        }

        preferences.registerOnSharedPreferenceChangeListener(listener)
        emitter.setCancellable { preferences.unregisterOnSharedPreferenceChangeListener(listener) }
    }

    override fun getSearchPhrases(): Observable<Set<String>> =
            changesListener
                    .startWith(SEARCH_PHRASES)
                    .filter { it == SEARCH_PHRASES }
                    .map {
                        preferences.getStringSet(it, emptySet())
                    }

    override fun saveSearchPhrase(phrase: String) {
        mutableSetOf(phrase).let {
            it.apply {
                addAll(preferences.getStringSet(SEARCH_PHRASES, mutableSetOf()))
            }
            preferences.edit().putStringSet(SEARCH_PHRASES, it).apply()
        }
    }
}