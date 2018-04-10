package com.jam.utils.easybanner.loader

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView

/**
 * Created by hejiaming on 2018/4/9.
 * @desciption:
 */
abstract class ImageLoader : DisplayViewLoaderInterface {

    override fun createDisplayView(context: Context): ImageView {
        return ImageView(context)
    }

}