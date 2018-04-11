package com.jam.utils.easybanner

import android.content.Context
import android.support.v4.view.ViewPager
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.ImageView.ScaleType
import android.widget.LinearLayout
import android.widget.TextView
import com.jam.utils.easybanner.listener.OnEasyBannerListener
import com.jam.utils.easybanner.loader.DisplayViewLoaderInterface
import com.jam.utils.easybanner.view.BannerViewPager

/**
 * Created by hejiaming on 2018/4/8.
 * @desciption:
 */
class EasyBanner @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : FrameLayout(context, attrs, defStyleAttr) {

    val TAG = "EasyBanner"

    /**
     * 标题
     */
    private val titles by lazy { ArrayList<String>() }

    /**
     * 图片地址
     */
    private val imageUrls by lazy { ArrayList<Any>() }

    /**
     * 广告图片View
     */
    private val views by lazy { ArrayList<View>() }

    /**
     * 广告指示器图片View
     */
    private val indicatorImageViews by lazy { ArrayList<ImageView>() }

    /**
     * 指示器
     */
    private val indicatorDrawable by lazy { ArrayList<ImageView>() }

    /**
     * 指示默认器尺寸
     */
    private val indicatorSize by lazy { if (displayMetrics == null) 0 else displayMetrics!!.widthPixels / 80 }

    private val mScroller: BannerScroller by lazy { BannerScroller(vp_banner.context).apply { duration = scrollTime } }


    private val displayMetrics by lazy { context.resources.displayMetrics }


    private var indicatorWidth: Int = 0

    private var indicatorHeight: Int = 0

    private var indicatorMargin: Int = 0

    private var indicatorDrawableSelected = -1

    private var indicatorDrawableNormal = -1

    private var delayTime = EasyBannerConfig.TIME

    private var scrollTime = EasyBannerConfig.DURATION

    private var autoPlay = EasyBannerConfig.IS_AUTO_PLAY

    private var isScroll = EasyBannerConfig.IS_SCROLL

    private var defaultBackgroundRes = -1

    private var displayViewLoader: DisplayViewLoaderInterface? = null

    private var indicatorGravity = -1

    private var indicatorStyle = EasyBannerConfig.CIRCLE_INDICATOR

    private var currentItem = 1

    private val handler = WeakHandler()

    private var onEasyBannerListener: OnEasyBannerListener? = null

    private var outerOnPageChangeListener: ViewPager.OnPageChangeListener? = null

    private val adapter by lazy { EasyBannerPagerAdapter(views, onEasyBannerListener) }

    private val autoPlayTask: Runnable by lazy {
        Runnable {
            if (imageUrls.size > 1 && autoPlay) {
                currentItem = currentItem % (imageUrls.size + 1) + 1
                if (currentItem == 1) {
                    vp_banner.setCurrentItem(currentItem, false)
                    handler.post(autoPlayTask)
                } else {
                    vp_banner.currentItem = currentItem
                    handler.postDelayed(autoPlayTask, delayTime.toLong())
                }
            }
        }
    }

    private val innerOnPageChangeListener by lazy {
        object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {
                outerOnPageChangeListener?.onPageScrollStateChanged(state)
                when (state) { //模拟无线滚动
                    0 -> {//No operation
                        if (currentItem == 0) {
                            vp_banner.setCurrentItem(imageUrls.size, false)
                        } else if (currentItem == imageUrls.size + 1) {
                            vp_banner.setCurrentItem(1, false)
                        }
                    }
                    1 -> {//start Sliding
                        if (currentItem == imageUrls.size + 1) {
                            vp_banner.setCurrentItem(1, false)
                        } else if (currentItem == 0) {
                            vp_banner.setCurrentItem(imageUrls.size, false)
                        }
                    }
                    2 -> {//end Sliding

                    }
                }
            }

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
                outerOnPageChangeListener?.onPageScrolled(toRealPosition(position), positionOffset, positionOffsetPixels)
            }

            override fun onPageSelected(position: Int) {
                Log.d(TAG, "position:" + position)
                currentItem = position
                outerOnPageChangeListener?.onPageSelected(toRealPosition(position))
            }
        }
    }

    /**
     * 返回真实的位置
     *
     * @param position
     * @return 下标从0开始
     */
    fun toRealPosition(position: Int): Int {
        var realPosition = (position - 1) % imageUrls.size
        if (realPosition < 0)
            realPosition += imageUrls.size
        return realPosition
    }

    /**
     * 容器布局
     */
    private lateinit var containerView: View

    private val vp_banner by lazy { containerView.findViewById<BannerViewPager>(R.id.vp_banner) }

    private val tv_indicatorNum by lazy { containerView.findViewById<TextView>(R.id.tv_indicatorNum) }

    private val iv_defaultImage by lazy { containerView.findViewById<ImageView>(R.id.iv_defaultImage) }

    private val ll_indicatorContainer by lazy { containerView.findViewById<LinearLayout>(R.id.ll_indicatorContainer) }


    init {

        Log.d(TAG, "init")

        if (attrs != null) {
            val typeArray = context.obtainStyledAttributes(attrs, R.styleable.EasyBanner)
            indicatorWidth = typeArray.getDimensionPixelOffset(R.styleable.EasyBanner_indicatorWidth, indicatorSize)
            indicatorHeight = typeArray.getDimensionPixelOffset(R.styleable.EasyBanner_indicatorHeight, indicatorSize)
            indicatorMargin = typeArray.getDimensionPixelOffset(R.styleable.EasyBanner_indicatorMargin, EasyBannerConfig.PADDING_SIZE)

            indicatorDrawableSelected = typeArray.getResourceId(R.styleable.EasyBanner_indicatorDrawableSelected, R.drawable.banner_indicator_selected_gray)
            indicatorDrawableNormal = typeArray.getResourceId(R.styleable.EasyBanner_indicatorDrawableSelected, R.drawable.banner_indicator_normal_white)

            delayTime = typeArray.getInt(R.styleable.EasyBanner_delayTime, delayTime)

            scrollTime = typeArray.getInt(R.styleable.EasyBanner_scrollTime, scrollTime)

            autoPlay = typeArray.getBoolean(R.styleable.EasyBanner_autoScroll, autoPlay)

            defaultBackgroundRes = typeArray.getResourceId(R.styleable.EasyBanner_defaultBackground, R.mipmap.no_banner)

            typeArray.recycle()
        }

        initView()
    }


    private fun initView() {
        containerView = LayoutInflater.from(context).inflate(R.layout.easybanner_layout, this, true)
        views.clear()
        iv_defaultImage.setImageResource(defaultBackgroundRes)
        initViewPagerScroll()
    }

    private fun initViewPagerScroll() {
        try {
            val mField = ViewPager::class.java.getDeclaredField("mScroller")
            mField.isAccessible = true
            mField.set(vp_banner, mScroller)
        } catch (e: Exception) {
            Log.e(TAG, e.message)
        }

    }


    fun isAutoPlay(autoPlay: Boolean): EasyBanner {
        this@EasyBanner.autoPlay = autoPlay
        return this
    }

    fun setIndicatorStyle(indicatorStyle: Int): EasyBanner {
        this@EasyBanner.indicatorStyle = indicatorStyle
        return this
    }

    fun setDisplayLoader(displayViewLoader: DisplayViewLoaderInterface): EasyBanner {
        this@EasyBanner.displayViewLoader = displayViewLoader
        return this
    }

    fun setData(objects: ArrayList<Any>?): EasyBanner {
        this@EasyBanner.imageUrls.clear()
        if (objects != null)
            this@EasyBanner.imageUrls.addAll(objects)
        return this
    }

//    fun setIndicatorGravity(type: Int) {
//        when (type) {
//            EasyBannerConfig.LEFT -> {
//                indicatorGravity = Gravity.LEFT | Gravity.CENTER_VERTICAL
//            }
//        }
//    }

    fun setAnimation(transformer: ViewPager.PageTransformer): EasyBanner {
        setPageTransformer(true, transformer)
        return this
    }

    /**
     * Set a [PageTransformer] that will be called for each attached page whenever
     * the scroll position is changed. This allows the application to apply custom property
     * transformations to each page, overriding the default sliding look and feel.
     *
     * @param reverseDrawingOrder true if the supplied PageTransformer requires page views
     * to be drawn from last to first instead of first to last.
     * @param transformer         PageTransformer that will modify each page's animation properties
     * @return Banner
     */
    fun setPageTransformer(reverseDrawingOrder: Boolean, transformer: ViewPager.PageTransformer): EasyBanner {
        vp_banner.setPageTransformer(reverseDrawingOrder, transformer)
        return this
    }

    fun setOffscreenPageLimit(limit: Int): EasyBanner {
        vp_banner.offscreenPageLimit = limit
        return this
    }

    fun setOnEasyBannerListener(onEasyBannerListener: OnEasyBannerListener): EasyBanner {
        this@EasyBanner.onEasyBannerListener = onEasyBannerListener
        return this
    }


    fun setViewPagerIsScroll(scroll: Boolean): EasyBanner {
        this@EasyBanner.isScroll = scroll
        return this
    }

    fun update(imageUrls: ArrayList<Any>) {
        this@EasyBanner.imageUrls.clear()
        this@EasyBanner.views.clear()
        this@EasyBanner.indicatorImageViews.clear()
        this@EasyBanner.imageUrls.addAll(imageUrls)
        start()
    }

    fun start(): EasyBanner {
//        setIndicatorsStyle()
//        loadingIndicator()
        loadingDisplayViews()


        if (imageUrls.isNotEmpty()) {
            currentItem = 1
            vp_banner.apply {
                addOnPageChangeListener(innerOnPageChangeListener)
                adapter = this@EasyBanner.adapter
                isFocusable = true
                currentItem = this@EasyBanner.currentItem
                setScrollable(isScroll && imageUrls.size > 1)
            }

            if (indicatorGravity != -1) ll_indicatorContainer.gravity = indicatorGravity
            if (autoPlay) startAutoPlay()
        }


        return this
    }

    private fun loadingIndicator() {
        if (indicatorStyle == EasyBannerConfig.CIRCLE_INDICATOR) {
            indicatorImageViews.clear()
            ll_indicatorContainer.removeAllViews()
            for (i in imageUrls.indices) {
                val params = LinearLayout.LayoutParams(indicatorWidth, indicatorHeight).apply {
                    leftMargin = indicatorMargin
                    rightMargin = indicatorMargin
                }
                val imageView = ImageView(context).apply {
                    scaleType = ScaleType.CENTER_CROP
                    if (i == 0)
                        setImageResource(indicatorDrawableSelected)
                    else
                        setImageResource(indicatorDrawableNormal)
                }
                indicatorImageViews.add(imageView)
                ll_indicatorContainer.addView(imageView, params)
            }
        } else if (indicatorStyle == EasyBannerConfig.NUM_INDICATOR) {
            tv_indicatorNum.text = "1/" + imageUrls.size.toString()
        }
    }


    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        if (autoPlay) {
            val action = ev?.action
            if (action == MotionEvent.ACTION_UP || action == MotionEvent.ACTION_CANCEL || action == MotionEvent.ACTION_OUTSIDE) {
                startAutoPlay()
            } else {
                stopAutoPlay()
            }
        }
        return super.dispatchTouchEvent(ev)
    }


    fun startAutoPlay() {
        handler.removeCallbacks(autoPlayTask)
        handler.postDelayed(autoPlayTask, delayTime.toLong())
    }

    fun stopAutoPlay() {
        handler.removeCallbacks(autoPlayTask)
    }

    private fun setIndicatorsStyle() {
        val visible = if (imageUrls.isEmpty()) View.GONE else View.VISIBLE
        when (indicatorStyle) {
            EasyBannerConfig.CIRCLE_INDICATOR -> ll_indicatorContainer.visibility = visible
            EasyBannerConfig.NUM_INDICATOR -> tv_indicatorNum.visibility = visible
        }
    }

    private fun loadingDisplayViews() {
        if (imageUrls.isEmpty()) {
            iv_defaultImage.visibility = View.VISIBLE
            Log.e(TAG, "The image data set is empty.")
            return
        }
        iv_defaultImage.visibility = View.GONE
        views.clear()
        if (displayViewLoader == null) throw KotlinNullPointerException("must call setDisplayLoader() ")
        for (i in 0..imageUrls.size + 1) {
            var displayView = displayViewLoader?.createDisplayView(context)
            if (displayView is ImageView) {
                var url: Any = when (i) {
                    0 -> imageUrls[imageUrls.size - 1]
                    imageUrls.size + 1 -> imageUrls[0]
                    else -> imageUrls[i - 1]
                }
                views.add(displayView)
                displayView.scaleType = ScaleType.CENTER_CROP
                displayViewLoader?.display(context, url, displayView)
                Log.d(TAG, "displayView is ImageView")
            }
        }
    }


}