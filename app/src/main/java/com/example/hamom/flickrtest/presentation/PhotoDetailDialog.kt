package com.example.hamom.flickrtest.presentation

import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.hamom.flickrtest.R
import com.example.hamom.flickrtest.data.local.model.Photos
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_photo_detail.*

class PhotoDetailDialog : DialogFragment() {

    companion object {
        val TAG = "${PhotoDetailDialog::class.java.name}.TAG"
        private val PHOTO_ARG = "${PhotoDetailDialog::class.java.name}.tag.PHOTO"

        fun createInstance(photo: Photos.Photo) =
                PhotoDetailDialog().apply {
                    arguments = Bundle().apply {
                        putParcelable(PHOTO_ARG, photo)
                    }
                    setStyle(DialogFragment.STYLE_NORMAL, R.style.FullScreenDialogStyle)
                }
    }

    private val photo: Photos.Photo by lazy { arguments!!.getParcelable<Photos.Photo>(PHOTO_ARG) }

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View =
        inflater.inflate(R.layout.fragment_photo_detail, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Picasso.get().
                load(photo.url)
                .fit()
                .centerInside()
                .placeholder(R.drawable.photo_placeholder)
                .into(photoView)

        photo.description.takeIf { it.isNotBlank() }?.let {
            titleView.text = it
        } ?: let { titleView.visibility = View.GONE }

        view.setOnClickListener { dismiss() }
    }
}