package com.example.hamom.flickrtest.presentation

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.ArrayAdapter
import com.example.hamom.flickrtest.R
import com.example.hamom.flickrtest.data.local.model.Photos.Photo
import com.example.hamom.flickrtest.di.MAIN
import io.reactivex.Scheduler
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.activity_main.*
import org.koin.android.ext.android.inject
import org.koin.android.viewmodel.ext.android.viewModel

class MainActivity : AppCompatActivity() {

    companion object {
        private const val PAGE_LOAD_TRESHOLD = 30
        private val TAG = MainActivity::class.java.name
    }
    private val mainScheduler: Scheduler by inject(MAIN)
    private val viewModel: MainViewModel by viewModel()
    private val disposable = CompositeDisposable()
    private lateinit var adapter: PhotosAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        searchLayout.requestFocus()

        disposable.addAll(
                viewModel.getData()
                        .observeOn(mainScheduler)
                        .subscribe { renderData(it) },

                viewModel.getErrors()
                        .observeOn(mainScheduler)
                        .subscribe { showError(it) })

        adapter = PhotosAdapter { showPhoto(it) }
        recyclerView.adapter = adapter
        recyclerView.layoutManager = GridLayoutManager(this, 2)

        searchBtn.setOnClickListener { search() }
        searchVeiw.setOnEditorActionListener { _, actionId, _ ->
            when (actionId) {
                EditorInfo.IME_ACTION_SEARCH -> search().let { false }
                else -> false
            }
        }

        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                if (dy > 0) {
                    (recyclerView.layoutManager as? GridLayoutManager)
                            ?.findLastVisibleItemPosition()?.let {
                               if (it > adapter.itemCount - PAGE_LOAD_TRESHOLD) {
                                   Log.d(TAG, " last visible pos: $it")
                                   viewModel.loadNextPage(adapter.lastPage + 1)
                               }
                            }
                }
            }
        })
    }

    private fun showError(error: Throwable?) {
        error?.let {
            Snackbar.make(recyclerView, it.message
                    ?: it::class.java.name, Snackbar.LENGTH_LONG).show()
        }
    }

    private fun renderData(data: MainViewData) {
        if (data.photos.page == 0) {
            adapter.reset()
        } else {
            adapter.addData(data.photos.photo, data.photos.page)
        }
        searchVeiw.setAdapter(ArrayAdapter(this,
                android.R.layout.simple_expandable_list_item_1,
                data.searchPhrases.toTypedArray()))

        progress.visibility =
                if (data.isLoading && adapter.itemCount == 0) View.VISIBLE else View.GONE
    }

    private fun search() {
        hideKeyboard()
        searchVeiw.text.toString().let {
            viewModel.searchPhotos(it)
        }
    }

    private fun showPhoto(photo: Photo) {
        supportFragmentManager.findFragmentByTag(PhotoDetailDialog.TAG)
                ?: PhotoDetailDialog.createInstance(photo).let {
                    supportFragmentManager.beginTransaction().add(it, PhotoDetailDialog.TAG).commit()
                }
    }

    override fun onDestroy() {
        disposable.dispose()
        super.onDestroy()
    }
}
