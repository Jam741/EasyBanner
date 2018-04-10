package com.jam.utils.easybanner

import android.support.v4.view.PagerAdapter
import android.view.View
import android.view.ViewGroup
import com.jam.utils.easybanner.listener.OnEasyBannerListener

/**
 * Created by hejiaming on 2018/4/9.
 * @desciption:
 */
class EasyBannerPagerAdapter(val views: ArrayList<View>, val bannerListener: OnEasyBannerListener? = null) : PagerAdapter() {

    override fun isViewFromObject(view: View?, `object`: Any?): Boolean {
        return view == `object`
    }

    override fun getCount(): Int {
        return views.size
    }

    override fun instantiateItem(container: ViewGroup?, position: Int): Any {
        val childView = views[position]
        container?.addView(childView)
        if (bannerListener != null) {
            childView.setOnClickListener { bannerListener.onBannerClick(toRealPosition(position)) }
        }
        return childView
    }


    override fun destroyItem(container: ViewGroup?, position: Int, `object`: Any?) {
        container?.removeView(`object` as View?)
    }

    /**
     * 返回真实的位置
     *
     * @param position
     * @return 下标从0开始
     */
    private fun toRealPosition(position: Int): Int {
        var realPosition = (position - 1) % count
        if (realPosition < 0)
            realPosition += count
        return realPosition
    }
}