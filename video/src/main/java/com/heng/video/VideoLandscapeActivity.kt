package com.heng.video
import android.content.Intent
import android.view.View
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.heng.common.CommonConstant
import com.heng.common.base.BaseActivity
import com.heng.common.log.*
import com.heng.video.interfaces.ICommunication
import com.heng.video.widgets.DeMediaController
import com.pili.pldroid.player.AVOptions
import com.pili.pldroid.player.PLOnCompletionListener
import com.pili.pldroid.player.PLOnPreparedListener
import kotlinx.android.synthetic.main.video_activity_video_landscape.*


@Route(path = CommonConstant.TO_VIDEO_LANDSCAPE_ACTIVITY)
class VideoLandscapeActivity : BaseActivity() , ICommunication{

    @JvmField
    @Autowired(name = VIDEO_PATH)
    var videoPath : String? = null

    @JvmField
    @Autowired(name = VIDEO_CURRENT_POSITION)
    var videoCurrentPosition = 0L

    @JvmField
    @Autowired(name = VIDEO_IS_PLAYED)
    var videoIsPlayed = false

    var mVideoPath : String? = null
    var mVideoCurrentPosition = 0L
    var mVideoIsPlayed = false

    private lateinit  var mediaController: DeMediaController

    override fun getContentLayoutId(): Int = R.layout.video_activity_video_landscape

    override fun initWidget() {
        super.initWidget()
        ARouter.getInstance().inject(this)
        landscape_play_pause_iv.setOnClickListener(onClickListener)
        landscape_zoom_iv.setOnClickListener(onClickListener)
    }

    override fun initData() {
        super.initData()

        getIntentData()

        //设置封面view
        landscape_pl_video_view.setCoverView(landscape_cover_iv)

        //设置视频相关的参数
        val avOptions = AVOptions()
        //每一帧超时时间
        avOptions.setInteger(AVOptions.KEY_PREPARE_TIMEOUT, 10 * 1000)
        //设置编码方式
        avOptions.setInteger(AVOptions.KEY_MEDIACODEC, AVOptions.MEDIA_CODEC_SW_DECODE)
        avOptions.setInteger(AVOptions.KEY_LIVE_STREAMING, 0)
        avOptions.setInteger(AVOptions.KEY_LOG_LEVEL, 0)
        landscape_pl_video_view.setAVOptions(avOptions)

        //设置相关的监听器
//        pl_video_view.setOnVideoFrameListener(onVideoFrameListener)
//        pl_video_view.setOnAudioFrameListener(onAudioFrameListener)
//        pl_video_view.setOnBufferingUpdateListener(onBufferingUpdateListener)
//        pl_video_view.setOnVideoSizeChangedListener(onVideoSizeChangedListener)
//        pl_video_view.setOnErrorListener(onErrorListener)
//        pl_video_view.setOnCompletionListener(onCompleteListener)

        landscape_pl_video_view.setVideoPath(mVideoPath)

//        mediaController = MediaController(this, true, false, landscape_pl_video_view)
//        mediaController?.setOnClickSpeedAdjustListener(onClickSpeedAdjustListener)
        landscape_pl_video_view.setOnPreparedListener(onPreparedListener)
        landscape_pl_video_view.setOnCompletionListener(onCompletionListener)

        mediaController = DeMediaController(this, landscape_pl_video_view, this)
        mediaController.mScreenState = VIDEO_ZOOM_IN
        landscape_pl_video_view.setMediaController(mediaController)

        doVideoLog(this.javaClass.simpleName,"videoPath:$mVideoPath")
        doVideoLog(this.javaClass.simpleName,"videoCurrentPosition:$videoCurrentPosition,videoIsPlayed:$videoIsPlayed")
    }

    override fun onPause() {
        super.onPause()
        if (landscape_pl_video_view != null && landscape_pl_video_view.isPlaying) {
            landscape_pl_video_view.pause()
            landscape_play_pause_iv.setImageResource(R.drawable.video_play_arrow_24)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (landscape_pl_video_view != null) {
            landscape_pl_video_view.stopPlayback()
        }
    }

    override fun onBackPressed() {
        setIntentData()
        super.onBackPressed()
        doVideoLog("override fun onBackPressed()")
    }

    override fun navigationToActivity(flag : Int) {
        doVideoLog("override fun navigationToActivity(),flag->$flag")
        when (flag) {
            VIDEO_ZOOM_IN->{
                setIntentData(true)
            }
        }
    }

    private fun getIntentData() {
        mVideoPath = intent.getStringExtra(VIDEO_PATH)
        mVideoCurrentPosition = intent.getLongExtra(VIDEO_CURRENT_POSITION, 0L)
        mVideoIsPlayed = intent.getBooleanExtra(VIDEO_IS_PLAYED, false)
    }

    private fun setIntentData(finish: Boolean) {
        val dataIntent = Intent()
        dataIntent.putExtra(VIDEO_PATH, mVideoPath)
        dataIntent.putExtra(VIDEO_CURRENT_POSITION, landscape_pl_video_view!!.currentPosition)
        dataIntent.putExtra(VIDEO_IS_PLAYED, landscape_pl_video_view!!.isPlaying)
        setResult(VIDEO_PLAY_PAUSE_CODE, dataIntent)
        if (finish) {
            finish()
        }
    }

    private fun setIntentData() {
        setIntentData(false)
    }

    private val onClickListener = View.OnClickListener { view ->
        when (view.id) {
            R.id.landscape_play_pause_iv -> {
                if (landscape_pl_video_view.isPlaying) {
                    landscape_pl_video_view.pause()
                } else {
                    landscape_pl_video_view.start()
                    if (mVideoCurrentPosition != 0L) {
                        mediaController.seekToPosition(mVideoCurrentPosition)
                    }
                    landscape_pl_video_view.start()
                }

                setPlayPauseIvBg(!landscape_pl_video_view.isPlaying)
            }
            R.id.landscape_zoom_iv -> {
                setIntentData()
            }
            else -> {

            }
        }
    }

    private fun setPlayPauseIvBg(playing: Boolean) {
        val resId = if (playing) {
            //暂停状态
            R.drawable.video_play_arrow_24
        } else {
            //播放状态
            R.drawable.video_pause_24
        }
        landscape_play_pause_iv.setImageResource(resId)
    }

    private val onPreparedListener = PLOnPreparedListener{
        if (mVideoIsPlayed) {

            setPlayPauseIvBg(!mVideoIsPlayed)
            landscape_pl_video_view.start()
            if (mVideoCurrentPosition != 0L) {
                mediaController.seekToPosition(mVideoCurrentPosition)
            }
            landscape_pl_video_view.start()
        }
    }

    private val onCompletionListener = PLOnCompletionListener {
        mVideoIsPlayed = false
        setPlayPauseIvBg(true)
    }
}