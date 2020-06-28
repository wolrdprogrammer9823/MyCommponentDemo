package com.heng.video.widgets
import android.annotation.SuppressLint
import android.content.Context
import android.content.res.TypedArray
import android.util.AttributeSet
import android.util.Log
import android.util.TypedValue
import android.view.Gravity
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.widget.FrameLayout
import android.widget.PopupWindow
import android.widget.SeekBar
import androidx.appcompat.widget.LinearLayoutCompat
import com.heng.common.CommonConstant
import com.heng.common.log.doVideoLog
import com.heng.common.util.CdTimeUtil
import com.heng.video.R
import com.heng.video.interfaces.ICommunication
import com.pili.pldroid.player.IMediaController
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Consumer
import io.reactivex.functions.Function
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.video_video_play_progressbar.view.*
import java.util.concurrent.TimeUnit

class DeMediaController : FrameLayout, IMediaController {

    private var mediaPlayerControl: IMediaController.MediaPlayerControl? = null

    private var mRootView: View? = null
    private var mContentView: View? = null
    private var mPopupWindow: PopupWindow? = null
    private var mICommunication: ICommunication? = null

    private var mVideoPlayed = false

    constructor(
        context: Context,
        rootView: View,
        mICommunication: ICommunication?
    ) : super(context) {
        init(context)
        this.mRootView = rootView
        this.mICommunication = mICommunication
    }

    constructor(
        context: Context,
        attributeSet: AttributeSet,
        rootView: View,
        mICommunication: ICommunication?
    ) : super(context, attributeSet) {
        init(context)
        this.mRootView = rootView
        this.mICommunication = mICommunication
    }

    constructor(
        context: Context,
        attributeSet: AttributeSet,
        defaultInt: Int,
        rootView: View,
        mICommunication: ICommunication?
    ) : super(context, attributeSet, defaultInt) {
        init(context)
        this.mRootView = rootView
        this.mICommunication = mICommunication
    }

    override fun setMediaPlayer(mediaPlayerControl: IMediaController.MediaPlayerControl) {

        this.mediaPlayerControl = mediaPlayerControl
        doVideoLog(TAG,"setMediaPlayer(MediaPlayerControl mediaPlayerControl)")
        doVideoLog(TAG,"duration:" + this.mediaPlayerControl!!.duration)
    }

    @SuppressLint("CheckResult")
    override fun show() {
        doVideoLog( TAG,"show()")
        if (mVideoPlayed || mediaPlayerControl!!.isPlaying) {
            showPopupWindow()
        }

        setProgress()

        Observable.interval(0, 50, TimeUnit.MILLISECONDS, AndroidSchedulers.mainThread())
            .takeWhile {
                mediaPlayerControl != null && mediaPlayerControl!!.isPlaying
            }.subscribe {
                setProgress()
            }
    }

    override fun show(i: Int) {

        doVideoLog(TAG,"show(int i)")
    }

    override fun hide() {

        doVideoLog(TAG,"hide()")
    }

    override fun isShowing(): Boolean {

        doVideoLog(TAG, "isShowing()")
        return false
    }

    override fun setEnabled(b: Boolean) {

        doVideoLog(TAG,"setEnabled(boolean b)")
    }

    override fun setAnchorView(view: View) {
        doVideoLog(TAG,"setAnchorView(View view):${view != null}")
        media_zoom_iv.setOnClickListener {
            mICommunication?.navigationToActivity()
        }

        if (media_progress_bar != null) {
            if (media_progress_bar is SeekBar) {

                val mSeekBar : SeekBar = media_progress_bar as SeekBar
                mSeekBar.setOnSeekBarChangeListener(onSeekBarChangeListener)
                mSeekBar.thumbOffset = 1
            }

            media_progress_bar.max = 100
            media_progress_bar.isEnabled = true
        }
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        showPopupWindow()
        return true
    }

    companion object {

        private const val DEFAULT_SHOW_TIME = 3000L
        private const val TAG = "DeMediaController"
    }

    private fun init(context: Context) {

        mContentView = LayoutInflater.from(context).inflate(R.layout.video_video_play_progressbar, this)

        mPopupWindow = PopupWindow(context)
        mPopupWindow?.contentView = mContentView
        mPopupWindow?.setBackgroundDrawable(null)
        mPopupWindow?.isFocusable = false
        mPopupWindow?.isOutsideTouchable = true
        mPopupWindow?.animationStyle = android.R.anim.fade_in
        mPopupWindow?.width = LinearLayoutCompat.LayoutParams.MATCH_PARENT
        mPopupWindow?.height =
            TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 30F, resources.displayMetrics).toInt()
    }

    @SuppressLint("CheckResult")
    private fun showPopupWindow() {
        showPopupWindow(DEFAULT_SHOW_TIME)
    }

    @SuppressLint("CheckResult")
    private fun showPopupWindow(showTime: Long) {

        doVideoLog(TAG,"mPopupWindow=${mPopupWindow != null}")
        doVideoLog(TAG,"mRootView=${mRootView != null}")
        doVideoLog(TAG,"mRootView=${mRootView?.height}")
        doVideoLog(TAG,"mContentView=${mContentView?.height}")

        mPopupWindow?.showAsDropDown(mRootView!!, 0, -mPopupWindow!!.height)

        Observable.timer(showTime, TimeUnit.MILLISECONDS)
            .subscribeOn(Schedulers.single())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                hidePopupWindow()
            }

    }

    private fun hidePopupWindow() {
        mPopupWindow?.let {
            if (it.isShowing) {
                it.dismiss()
            }
        }
    }

    private fun setProgress(): Long {

        if (mediaPlayerControl == null) {
            return 0L
        }

        if (!mediaPlayerControl!!.isPlaying) {
            return 0L
        } else {
            mVideoPlayed = true
        }

        val currentPosition: Long? = mediaPlayerControl?.currentPosition
        doVideoLog("TAG","currentPosition->$currentPosition")
        val duration : Long? = mediaPlayerControl?.duration
        doVideoLog("TAG","duration->$duration")
        if (media_progress_bar != null) {
            if (currentPosition!! > 0L) {
                val position = currentPosition.times(100F).div(duration!!).plus(0.5F)
                doVideoLog("TAG","position->${position.toInt()}")
                media_progress_bar.setProgress(position.toInt(), false)
            }
            val percent = mediaPlayerControl?.bufferPercentage
            media_progress_bar.secondaryProgress = percent!!.toInt().times(10)
        }

        if (media_start_time_tv != null) {
            media_start_time_tv.text = CdTimeUtil.generateTime(currentPosition!!)
        }

        if (media_end_time_tv != null) {
            media_end_time_tv.text = CdTimeUtil.generateTime(duration!!)
        }

        return currentPosition!!.toLong()
    }

    private val onSeekBarChangeListener = object : SeekBar.OnSeekBarChangeListener {
        override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {

        }

        override fun onStartTrackingTouch(seekBar: SeekBar?) {

        }

        override fun onStopTrackingTouch(seekBar: SeekBar?) {

        }
    }
}