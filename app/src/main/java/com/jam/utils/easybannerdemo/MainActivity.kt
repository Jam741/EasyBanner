package com.jam.utils.easybannerdemo

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.Toast
import com.jam.utils.easybanner.listener.OnEasyBannerListener
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val data = resources.getStringArray(R.array.url)

        val urls = ArrayList<String>()
        urls += data

        easy_banner
                .setData(urls as ArrayList<Any>)
                .isAutoPlay(true)
                .setDisplayLoader(GlideImageLoader())
                .setOnEasyBannerListener(object : OnEasyBannerListener {
                    override fun onBannerClick(position: Int) {
                        Toast.makeText(this@MainActivity, position.toString(), Toast.LENGTH_SHORT).show()
                    }
                })
                .start()


    }
}
