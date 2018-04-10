package com.jam.utils.easybanner.loader

import android.content.Context
import android.view.View
import java.io.Serializable

/**
 * Created by hejiaming on 2018/4/8.
 * @desciption:
 */
interface DisplayViewLoaderInterface : Serializable {

    fun display(context: Context, path: Any, displayView: View)

    fun createDisplayView(context: Context): View
}