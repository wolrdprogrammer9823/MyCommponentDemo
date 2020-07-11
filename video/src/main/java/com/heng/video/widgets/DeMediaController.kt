package com.heng.video.widgets
import android.annotation.SuppressLint
import android.content.Context
import android.content.res.Configuration
import android.media.AudioManager
import android.util.AttributeSet
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.widget.FrameLayout
import android.widget.PopupWindow
import android.widget.SeekBar
import androidx.appcompat.widget.LinearLayoutCompat
import com.heng.common.log.VIDEO_ZOOM_IN
import com.heng.common.log.VIDEO_ZOOM_OUT
import com.heng.common.log.doVideoLog
import com.heng.common.util.CdTimeUtil
import com.heng.video.R
import com.heng.video.interfaces.ICommunication
import com.pili.pldroid.player.IMediaController
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.video_video_play_progressbar.view.*
import java.util.concurrent.TimeUnit

class DeMediaController : FrameLayout, IMediaController {

    private var mediaPlayerControl: IMediaController.MediaPlayerControl? = null

    private var mContentView: View? = null
    private var audioManager: AudioManager? = null
    private var mICommunication: ICommunication? = null

    private var mRootView: View? = null

    var mPopupWindow: PopupWindow? = null

    private var mVideoPlayed = false
    private var dismissProgressBar = true
    private var updateProgressPerSecond = true

    private var mDuration = 0L
    private var currentTime = 0L

    var mScreenState = VIDEO_ZOOM_OUT

    private var showingPop = false

    private var disposable: Disposable? = null

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

    override fun onConfigurationChanged(newConfig: Configuration?) {
        super.onConfigurationChanged(newConfig)
        doVideoLog(TAG,"override fun onConfigurationChanged(newConfig: Configuration?)")
    }

    override fun setMediaPlayer(mediaPlayerControl: IMediaController.MediaPlayerControl) {

        this.mediaPlayerControl = mediaPlayerControl
        doVideoLog(TAG,"setMediaPlayer(MediaPlayerControl mediaPlayerControl)")
        doVideoLog(TAG,"duration:" + this.mediaPlayerControl!!.duration)
    }

    override fun show() {
        doVideoLog(TAG,"show()")
        if (mVideoPlayed || mediaPlayerControl!!.isPlaying) {

            if (System.currentTimeMillis() - currentTime >= DEFAULT_SHOW_TIME) {

                show(DEFAULT_SHOW_TIME)
                doVideoLog(TAG,"show()111111111111")
                showingPop = true
            } else {

                showingPop = if (showingPop) {

                    mPopupWindow?.let {
                        if (it.isShowing) {
                            it.dismiss()
                        }
                    }
                    disposable?.dispose()
                    false
                } else {

                    show(DEFAULT_SHOW_TIME)
                    true
                }

                doVideoLog(TAG,"show()->${mPopupWindow?.isShowing}")
                doVideoLog(TAG,"show()222222222222")
            }

            currentTime = System.currentTimeMillis()
        }
    }

    @SuppressLint("CheckResult")
    override fun show(i: Int) {
        doVideoLog(TAG,"show(int i)")
        showPopupWindow(i)
        showProgressValue()
    }

    override fun hide() {
        doVideoLog(TAG,"hide()")
        //按HOME键或者最近任务键会调用该方法
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

            mScreenState = if (context.resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT) VIDEO_ZOOM_OUT else VIDEO_ZOOM_IN
            doVideoLog(TAG,"smScreenState:$mScreenState")

            disposable?.dispose()
            hidePopupWindow()

            mICommunication?.navigationToActivity(mScreenState)
            view.post {
                showPopupWindow(DEFAULT_SHOW_TIME)
            }
        }

        doVideoLog(TAG, "(media_seek_bar != null)->${media_seek_bar != null}")
        if (media_seek_bar != null) {
            media_seek_bar.setOnSeekBarChangeListener(onSeekBarChangeListener)
            media_seek_bar.thumbOffset = 1
            media_seek_bar.max = 100
            media_seek_bar.isEnabled = true
        }
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        showPopupWindow(DEFAULT_SHOW_TIME)
        return true
    }

    override fun onTrackballEvent(event: MotionEvent?): Boolean {
        showPopupWindow(DEFAULT_SHOW_TIME)
        return false
    }

    companion object {

        private const val DEFAULT_SHOW_TIME = 3000
        private const val TAG = "DeMediaController"
    }

    private fun init(context: Context) {

        mContentView = LayoutInflater.from(context).inflate(R.layout.video_video_play_progressbar, this)

        audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager?

        mPopupWindow = PopupWindow(context)
        mPopupWindow?.contentView = mContentView
        mPopupWindow?.setBackgroundDrawable(null)
        mPopupWindow?.isFocusable = false
        mPopupWindow?.isOutsideTouchable = true
        mPopupWindow?.animationStyle = R.style.PopupWindow_Animation_Theme
        mPopupWindow?.width = LinearLayoutCompat.LayoutParams.MATCH_PARENT
        mPopupWindow?.height =
            TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 30F, resources.displayMetrics).toInt()
    }

    @SuppressLint("CheckResult")
    fun showPopupWindow() {
        showProgressValue()
        showPopupWindow(DEFAULT_SHOW_TIME)
    }

    @SuppressLint("CheckResult")
    private fun showPopupWindow(showTime: Int) {

        doVideoLog(TAG,"mPopupWindow=${mPopupWindow != null}")
        doVideoLog(TAG,"mRootView=${mRootView != null}")
        doVideoLog(TAG,"mRootView=${mRootView?.height}")
        doVideoLog(TAG,"mContentView=${mContentView?.height}")

        try {
            //try-catch住 有时会报The specified child already has a parent.
            // You must call removeView() on the child's parent first.错误
            mPopupWindow?.showAsDropDown(mRootView!!, 0, -mPopupWindow!!.height)
        } catch (e: Exception) {}

        disposable = Observable.timer(showTime.toLong(), TimeUnit.MILLISECONDS)
            .subscribeOn(Schedulers.single())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnError {
                println(it.cause!!.message)
            }.takeWhile { dismissProgressBar }
            .subscribe {
                hidePopupWindow()
            }
    }

    fun hidePopupWindow() {
        mPopupWindow?.let {
            if (it.isShowing) {
                try {
                    it.dismiss()
                } catch (e: NullPointerException) {}
            }
        }
    }

    @SuppressLint("CheckResult")
    private fun showProgressValue() {

        setProgress()

        Observable.interval(0, 50, TimeUnit.MILLISECONDS, AndroidSchedulers.mainThread())
            .takeWhile {
                mediaPlayerControl != null && mediaPlayerControl!!.isPlaying && updateProgressPerSecond
            }.subscribe {
                setProgress()
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
        if (media_seek_bar != null) {
            if (currentPosition!! > 0L) {
                val position = currentPosition.times(100F).div(duration!!).plus(0.5F)
                doVideoLog("TAG","position->${position.times(100F).toInt()}")
                media_seek_bar.setProgress(position.toInt(), false)
            }
            //val percent = mediaPlayerControl?.bufferPercentage
            //media_progress_bar.secondaryProgress = percent!!.toInt().times(10)
        }

        mDuration = duration!!

        if (media_start_time_tv != null) {
            media_start_time_tv.text = CdTimeUtil.generateTime(currentPosition!!)
        }

        if (media_end_time_tv != null) {
            media_end_time_tv.text = CdTimeUtil.generateTime(duration)
        }

        return currentPosition!!.toLong()
    }

    private val onSeekBarChangeListener = object : SeekBar.OnSeekBarChangeListener {
        override fun onStartTrackingTouch(seekBar: SeekBar?) {

            audioManager?.adjustStreamVolume(
                AudioManager.STREAM_MUSIC,
                AudioManager.ADJUST_SAME,
                AudioManager.FLAG_PLAY_SOUND
            )

            dismissProgressBar = false
            updateProgressPerSecond = false

            showPopupWindow(DEFAULT_SHOW_TIME)
        }

        @SuppressLint("CheckResult")
        override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
            if (!fromUser) {
                return
            }

            val newPosition = mDuration.times(progress.div(100F)).toLong()
            val newStartTime = CdTimeUtil.generateTime(newPosition)
            if (media_start_time_tv != null) {
                media_start_time_tv.text = newStartTime
            }
        }

        @SuppressLint("CheckResult")
        override fun onStopTrackingTouch(seekBar: SeekBar) {

            dismissProgressBar = true
            updateProgressPerSecond = true

            audioManager?.adjustStreamVolume(
                AudioManager.STREAM_MUSIC,
                AudioManager.ADJUST_SAME,
                AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE
            )

            val newPosition = mDuration.times(seekBar.progress.div(100F)).toLong()

            Observable.fromCallable {
                mediaPlayerControl!!.seekTo(newPosition)
            }.observeOn(AndroidSchedulers.mainThread()).subscribe()

            showProgressValue()
        }
    }

    @SuppressLint("CheckResult")
    fun seekToPosition(position: Long) {

        if (position < 0) {
            doVideoLog("position must more than zero!!!")
            return
        }

        mediaPlayerControl?.let {
            Observable.fromCallable {
                it.seekTo(position)
            }.observeOn(AndroidSchedulers.mainThread()).subscribe()
        }

        if (position > 0L) {
            mediaPlayerControl?.let {
                val newPosition = position.times(1.0F).div(it.duration)
                media_seek_bar.setProgress(newPosition.times(100F).toInt(), false)
            }
        }

        if (media_start_time_tv != null) {
            media_start_time_tv.text = CdTimeUtil.generateTime(position)
        }
    }
}