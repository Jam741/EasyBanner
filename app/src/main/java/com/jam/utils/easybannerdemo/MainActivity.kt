package com.jam.utils.easybannerdemo

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import com.bumptech.glide.Glide
import com.jam.utils.easybanner.EasyBannerPagerAdapter
import com.jam.utils.easybanner.listener.OnEasyBannerListener
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val data = resources.getStringArray(R.array.url)


        val urls = ArrayList<String>()
        for (s in data) {
            urls.add(s)
        }

//        val urls = arrayListOf(
//                "http://ww4.sinaimg.cn/large/006uZZy8jw1faic1xjab4j30ci08cjrv.jpg",
//                "http://ww4.sinaimg.cn/large/006uZZy8jw1faic1xjab4j30ci08cjrv.jpg",
//                "http://ww4.sinaimg.cn/large/006uZZy8jw1faic1xjab4j30ci08cjrv.jpg")

        val views = ArrayList<ImageView>()

        for (s in urls) {
            val imageView = ImageView(this)
            imageView.setBackgroundColor(Color.parseColor("#44000000"))
            Log.d("JAM", "imageView:" + imageView.width)
            views.add(imageView)
            Glide.with(this).load(s).into(imageView)
        }

        banner.adapter = EasyBannerPagerAdapter(views as ArrayList<View>)
        easy_banner
                .setData(urls as ArrayList<Any>)
                .isAutoPlay(true)
                .setDisplayLoader(GlideImageLoader())
                .setOnEasyBannerListener(object : OnEasyBannerListener {
                    override fun onBannerClick(position: Int) {
                        Log.d("JAM", "position:" + position)
                        Toast.makeText(this@MainActivity, position.toString(), Toast.LENGTH_SHORT).show()
                    }
                })
                .start()


    }
}
