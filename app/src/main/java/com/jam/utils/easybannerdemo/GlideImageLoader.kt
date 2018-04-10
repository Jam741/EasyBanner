package com.jam.utils.easybannerdemo

import android.content.Context
import android.graphics.Color
import android.util.Log
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
        Log.d("JAM", "display:" + displayView.measuredWidth)
        (displayView as ImageView).setBackgroundColor(Color.parseColor("#000000"))
        Glide.with(context)
                .load(path as String)
                .placeholder(R.mipmap.ic_launcher)
                .into(displayView)

//        Picasso.get().load("http://i.imgur.com/DvpvklR.png").into(displayView as ImageView)

    }


}