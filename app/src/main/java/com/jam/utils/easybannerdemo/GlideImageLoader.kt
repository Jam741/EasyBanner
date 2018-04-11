package com.jam.utils.easybannerdemo

import android.content.Context
import android.view.View
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.jam.utils.easybanner.loader.ImageLoader

/**
 * Created by hejiaming on 2018/4/9.
 * @desciption:
 */
class GlideImageLoader : ImageLoader() {


    override fun display(context: Context, path: Any, displayView: View) {
        Glide.with(context)
                .load(path as String)
                .placeholder(R.mipmap.ic_launcher)
                .into(displayView as ImageView)

    }

}