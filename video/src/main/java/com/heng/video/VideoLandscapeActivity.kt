package com.heng.video
import android.view.View
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.heng.common.CommonConstant
import com.heng.common.base.BaseActivity
import com.heng.common.log.VIDEO_CURRENT_POSITION
import com.heng.common.log.VIDEO_IS_PLAYED
import com.heng.common.log.VIDEO_PATH
import com.heng.common.log.doVideoLog
import com.heng.video.widgets.MediaController
import com.pili.pldroid.player.AVOptions
import kotlinx.android.synthetic.main.video_activity_video_landscape.*
import kotlinx.android.synthetic.main.video_fragment_video.*


@Route(path = CommonConstant.TO_VIDEO_LANDSCAPE_ACTIVITY)
class VideoLandscapeActivity : BaseActivity() {

    @JvmField
    @Autowired(name = VIDEO_PATH)
    var videoPath : String? = null

    @JvmField
    @Autowired(name = VIDEO_CURRENT_POSITION)
    var videoCurrentPosition = 0L

    @JvmField
    @Autowired(name = VIDEO_IS_PLAYED)
    var videoIsPlayed = false

    override fun getContentLayoutId(): Int = R.layout.video_activity_video_landscape

    override fun initWidget() {
        super.initWidget()
        ARouter.getInstance().inject(this)
        landscape_play_pause_iv.setOnClickListener(onClickListener)
    }

    override fun initData() {
        super.initData()

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

        landscape_pl_video_view.setVideoPath(videoPath)

        mediaController = MediaController(this, true, false, landscape_pl_video_view)
//      mediaController?.setOnClickSpeedAdjustListener(onClickSpeedAdjustListener)

        landscape_pl_video_view.setMediaController(mediaController)

        doVideoLog(this.javaClass.simpleName,"videoPath:$videoPath")
        doVideoLog(this.javaClass.simpleName,"videoCurrentPosition:$videoCurrentPosition,videoIsPlayed:$videoIsPlayed")

        if (videoIsPlayed) {

            landscape_pl_video_view.start()
            setPlayPauseIvBg(!videoIsPlayed)

            if (videoCurrentPosition != 0L) {
                mediaController?.seekToPosition(videoCurrentPosition)
            }
        }
    }

    override fun onPause() {
        super.onPause()
        if (landscape_pl_video_view != null && landscape_pl_video_view.isPlaying) {
            landscape_pl_video_view.pause()
            landscape_play_pause_iv.setImageResource(R.drawable.ic_baseline_play_arrow_24)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (landscape_pl_video_view != null) {
            landscape_pl_video_view.stopPlayback()
        }
    }

    private var mediaController: MediaController? = null

    private val onClickListener = View.OnClickListener { view ->
        when (view.id) {
            R.id.landscape_play_pause_iv -> {
                if (landscape_pl_video_view.isPlaying) {
                    landscape_pl_video_view.pause()
                } else {
                    landscape_pl_video_view.start()
                }
                setPlayPauseIvBg(!landscape_pl_video_view.isPlaying)
            }
            else -> {
            }
        }
    }

    private fun setPlayPauseIvBg(playing: Boolean) {
        val resId = if (playing) {
            //暂停状态
            R.drawable.ic_baseline_play_arrow_24
        } else {
            //播放状态
            R.drawable.ic_baseline_pause_24
        }
        landscape_play_pause_iv.setImageResource(resId)
    }

}