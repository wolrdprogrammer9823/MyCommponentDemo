package com.heng.video
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.FrameLayout
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import com.heng.common.base.BaseFragment
import com.heng.common.log.*
import com.heng.common.util.CdTimeUtil
import com.heng.video.interfaces.ICommunication
import com.heng.video.widgets.DeMediaController
import com.heng.video.widgets.MediaController
import com.pili.pldroid.player.*
import com.pili.pldroid.player.widget.PLVideoView
import kotlinx.android.synthetic.main.video_fragment_video.*

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

//要播放的视频url
private const val path = "http://demo-videos.qnsdk.com/movies/qiniu.mp4"

class VideoFragment : BaseFragment(), ICommunication {

    private var param1: String? = null
    private var param2: String? = null

    private lateinit var mediaController: DeMediaController

    private var inPlayState = false
    private var mDisplayAspectRatio = PLVideoView.ASPECT_RATIO_FIT_PARENT

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            VideoFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    override fun initArguments(bundle: Bundle?) {
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun getContentLayoutId(): Int = R.layout.video_fragment_video

    override fun firstInit() {
        super.firstInit()
        play_pause_iv.setOnClickListener(onClickListener)
        video_back_iv.setOnClickListener(onClickListener)
    }

    override fun initData() {

        super.initData()

        //设置封面view
        pl_video_view.setCoverView(cover_iv)

        //设置视频相关的参数
        val avOptions = AVOptions()
        //每一帧超时时间
        avOptions.setInteger(AVOptions.KEY_PREPARE_TIMEOUT, 10 * 1000)
        //设置编码方式
        avOptions.setInteger(AVOptions.KEY_MEDIACODEC, AVOptions.MEDIA_CODEC_SW_DECODE)
        avOptions.setInteger(AVOptions.KEY_LIVE_STREAMING, 0)
        avOptions.setInteger(AVOptions.KEY_LOG_LEVEL, 0)

        pl_video_view.setAVOptions(avOptions)

        //设置相关的监听器
        pl_video_view.setOnVideoFrameListener(onVideoFrameListener)
        pl_video_view.setOnAudioFrameListener(onAudioFrameListener)
        pl_video_view.setOnBufferingUpdateListener(onBufferingUpdateListener)
        pl_video_view.setOnVideoSizeChangedListener(onVideoSizeChangedListener)
        pl_video_view.setOnErrorListener(onErrorListener)
        pl_video_view.setOnCompletionListener(onCompleteListener)

        pl_video_view.setVideoPath(path)

//        val mediaController = MediaController(requireContext(), true, false, pl_video_view)
//        mediaController?.setOnClickSpeedAdjustListener(onClickSpeedAdjustListener)
//        pl_video_view.setMediaController(mediaController)

        pl_video_view.displayAspectRatio = PLVideoView.ASPECT_RATIO_PAVED_PARENT

        mediaController = DeMediaController(requireContext(), pl_video_view, this)
        pl_video_view.setMediaController(mediaController)

        test_media_seek_bar.setOnSeekBarChangeListener(mSeekListener)
    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        doVideoLog("override fun onHiddenChanged : $hidden")
        //已经播放过视频了,Fragment切换之后才切换视频状态
        changeVideoStateWhenOnHiddenChanged(hidden)
    }

    override fun onResume() {
        super.onResume()
        doVideoLog("override fun onResume()")
        if (pl_video_view != null && inPlayState) {
            pl_video_view.start()
            setPlayPauseIvBg(false)
        }
    }

    override fun onPause() {
        super.onPause()
        doVideoLog("override fun onPause()")
        mediaController.mPopupWindow?.dismiss()
        if (pl_video_view != null && pl_video_view.isPlaying) {
            pl_video_view.pause()
            setPlayPauseIvBg(true)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (pl_video_view != null) {
            pl_video_view.stopPlayback()
        }
    }

    override fun onBackPressed(): Boolean {
        navigationToActivity(VIDEO_ZOOM_IN)
        return resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
    }

//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data)
//        doVideoLog("requestCode:$requestCode,resultCode:$resultCode")
//        when (requestCode) {
//            VIDEO_PLAY_PAUSE_CODE -> {
//                if (resultCode == VIDEO_PLAY_PAUSE_CODE) {
//                    if (data == null || "".equals(data)) {
//                        doVideoLog("data == null || “”.equals(data)")
//                        return
//                    }
//                    val mVideoIsPlayed = data.getBooleanExtra(VIDEO_IS_PLAYED, false)
//                    val mVideoCurrentPosition = data.getLongExtra(VIDEO_CURRENT_POSITION, 0L)
//                    doVideoLog("mVideoIsPlayed:${mVideoIsPlayed}")
//
//                    if (mVideoIsPlayed) {
//                        pl_video_view.start()
//                        if (mVideoCurrentPosition != 0L) {
//                            mediaController.seekToPosition(mVideoCurrentPosition)
//                        }
//                        pl_video_view.start()
//                    } else {
//                        pl_video_view.pause()
//                    }
//                    setPlayPauseIvBg(!mVideoIsPlayed)
//                }
//            }
//            else -> {
//            }
//        }
//    }

    override fun navigationToActivity(flag : Int) {
        when (flag) {
            VIDEO_ZOOM_OUT -> {

                //val dataIntent = Intent(requireContext(), VideoLandscapeActivity::class.java)
                //dataIntent.putExtra(VIDEO_PATH, path)
                //dataIntent.putExtra(VIDEO_CURRENT_POSITION, pl_video_view!!.currentPosition)
                //dataIntent.putExtra(VIDEO_IS_PLAYED, pl_video_view!!.isPlaying)
                //startActivityForResult(dataIntent, VIDEO_PLAY_PAUSE_CODE)

                //videoNotScaled = false

                doVideoLog("${VideoFragment::class.java.simpleName}:VIDEO_ZOOM_OUT->$VIDEO_ZOOM_OUT")

                val newLayoutParam = pl_video_view.layoutParams as FrameLayout.LayoutParams
                newLayoutParam.width = FrameLayout.LayoutParams.MATCH_PARENT
                newLayoutParam.height = FrameLayout.LayoutParams.MATCH_PARENT
                pl_video_view.layoutParams = newLayoutParam

                video_title_ll.visibility = View.VISIBLE

                requireActivity().window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN
                        or WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS or WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN)
                requireActivity().requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE
            }
            VIDEO_ZOOM_IN -> {

                doVideoLog("${VideoFragment::class.java.simpleName}:VIDEO_ZOOM_IN->$VIDEO_ZOOM_IN")

                mediaController.hidePopupWindow()

                val newLayoutParam = pl_video_view.layoutParams as FrameLayout.LayoutParams
                newLayoutParam.width = FrameLayout.LayoutParams.MATCH_PARENT
                newLayoutParam.height = resources.getDimension(R.dimen.video_dp_300).toInt()
                pl_video_view.layoutParams = newLayoutParam

                video_title_ll.visibility = View.GONE
                requireActivity().window.clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN or WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
                requireActivity().requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT
            }
            else -> {}
        }
    }

    //视频大小发生改变
    private val onVideoSizeChangedListener = PLOnVideoSizeChangedListener { width, height ->
        doVideoLog("${VideoFragment::class.java.simpleName}:onVideoSizeChangedListener->width:$width,height:$height")
    }

    //视频帧率发生改变
    private val onVideoFrameListener =
        PLOnVideoFrameListener { data, size, width, height, format, ts ->
            doVideoLog("${VideoFragment::class.java.simpleName}:onVideoFrameListener->data:${String(data)},size:$size,width:$width,height:$height,format:$format,ts:$ts")
        }

    //音频帧率发生改变
    private val onAudioFrameListener =
        PLOnAudioFrameListener { data, size, sampleRate, channels, dataWidth, ts ->
            doVideoLog("${VideoFragment::class.java.simpleName}:onAudioFrameListener->data:${String(data)},size:$size,sampleRate:$sampleRate,channels:$channels,dataWidth:$dataWidth,ts:$ts")
        }

    //缓冲更新
    private val onBufferingUpdateListener = PLOnBufferingUpdateListener { percent ->
        doVideoLog("${VideoFragment::class.java.simpleName}:onBufferingUpdateListener->percent:$percent")
    }

    //视频播放完成监听
    private val onCompleteListener = PLOnCompletionListener {
        doVideoLog("${VideoFragment::class.java.simpleName}:onCompleteListener->video is completed!!!")
        pl_video_view.pause()
        setPlayPauseIvBg(true)
    }

    //视频发生错误监听
    private val onErrorListener = PLOnErrorListener { errorCode ->
        when (errorCode) {
            PLOnErrorListener.ERROR_CODE_IO_ERROR -> {
                //IO错误  服务器会自动重连
                doVideoLog("PLOnErrorListener.ERROR_CODE_IO_ERROR")
                return@PLOnErrorListener false
            }
            PLOnErrorListener.ERROR_CODE_OPEN_FAILED -> {
                //打开失败
                doVideoLog("PLOnErrorListener.ERROR_CODE_OPEN_FAILED")
            }
            PLOnErrorListener.ERROR_CODE_SEEK_FAILED -> {
                //进度条定位到某一具体位置失败
                doVideoLog("PLOnErrorListener.ERROR_CODE_SEEK_FAILED")
                return@PLOnErrorListener true
            }
            PLOnErrorListener.ERROR_CODE_CACHE_FAILED -> {
                //缓冲失败
                doVideoLog("PLOnErrorListener.ERROR_CODE_CACHE_FAILED")
            }
            PLOnErrorListener.MEDIA_ERROR_UNKNOWN -> {
                //未知错误
                doVideoLog("PLOnErrorListener.MEDIA_ERROR_UNKNOWN")
            }
        }
        true
    }

    //切换视频速率(倍数)
    private val onClickSpeedAdjustListener = object : MediaController.OnClickSpeedAdjustListener {
        override fun onClickNormal() {
            pl_video_view.setPlaySpeed(0x00010001)
        }

        override fun onClickSlower() {
            pl_video_view.setPlaySpeed(0x00010002)
        }

        override fun onClickFaster() {
            pl_video_view.setPlaySpeed(0x00020001)
        }
    }

    private val onClickListener = View.OnClickListener { view ->
        when (view?.id) {
            R.id.play_pause_iv -> {
                val playing = pl_video_view.isPlaying
                if (playing) {
                    pl_video_view.pause()
                } else {
                    pl_video_view.start()
                    mediaController.showPopupWindow()
                }

                inPlayState = !playing
                setPlayPauseIvBg(playing)

                doVideoLog("duration:${CdTimeUtil.generateTime(pl_video_view.duration)}")
            }
            R.id.video_back_iv -> {

                navigationToActivity(VIDEO_ZOOM_IN)
            }
            else -> {
            }
        }
    }

    private fun changeVideoStateWhenOnHiddenChanged(hidden: Boolean) {
        if (hidden) {
            if (inPlayState) {
                if (pl_video_view != null) {
                    pl_video_view.pause()
                }
                setPlayPauseIvBg(true)
            }
        } else {
            if (inPlayState) {
                if (pl_video_view != null) {
                    pl_video_view.start()
                }
            } else {
                if (pl_video_view != null) {
                    pl_video_view.pause()
                }
            }
            setPlayPauseIvBg(!inPlayState)
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

        play_pause_iv?.setImageResource(resId)
    }


    private val mSeekListener = object : OnSeekBarChangeListener {
        override fun onStartTrackingTouch(bar: SeekBar) {

            doVideoLog("${VideoFragment::class.java.simpleName}:onStartTrackingTouch")
        }

        override fun onProgressChanged(bar: SeekBar, progress: Int, fromuser: Boolean) {
            if (!fromuser) {
                return
            }

            doVideoLog("${VideoFragment::class.java.simpleName}:onProgressChanged")
        }

        override fun onStopTrackingTouch(bar: SeekBar) {

            doVideoLog("${VideoFragment::class.java.simpleName}:onStopTrackingTouch")
        }
    }
}
