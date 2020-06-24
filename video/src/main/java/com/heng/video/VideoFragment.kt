package com.heng.video
import android.os.Bundle
import com.heng.common.base.BaseFragment
import com.heng.common.log.doVideoLog
import com.heng.video.widgets.MediaController
import com.pili.pldroid.player.*
import kotlinx.android.synthetic.main.video_fragment_video.*

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class VideoFragment : BaseFragment() {

    private var param1: String? = null
    private var param2: String? = null

    private var mediaController : MediaController? = null

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

    override fun initData() {

        super.initData()

        //要播放的视频url
        val path = "http://demo-videos.qnsdk.com/movies/qiniu.mp4"

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

        mediaController = MediaController(requireContext(), true, false)
        mediaController?.setOnClickSpeedAdjustListener(onClickSpeedAdjustListener)

        pl_video_view.setMediaController(mediaController)

        pl_video_view.start()
    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        doVideoLog("override fun onHiddenChanged : $hidden")
        if (hidden) {
            if (pl_video_view != null) {
                pl_video_view.pause()
            }
        } else {
            if (pl_video_view != null) {
                pl_video_view.start()
            }
        }
    }

    override fun onPause() {
        super.onPause()
        mediaController?.window?.dismiss()
        if (pl_video_view != null) {
            pl_video_view.pause()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (pl_video_view != null) {
            pl_video_view.stopPlayback()
        }
    }

    //视频大小发生改变
    private val onVideoSizeChangedListener = PLOnVideoSizeChangedListener { width, height ->
        doVideoLog("width:$width,height:$height")
    }

    //视频帧率发生改变
    private val onVideoFrameListener = PLOnVideoFrameListener{ data, size, width, height, format, ts->
        doVideoLog("data:${String(data)},size:$size,width:$width,height:$height,format:$format,ts:$ts")
    }
   
    //音频帧率发生改变
    private val onAudioFrameListener = PLOnAudioFrameListener { data, size, sampleRate, channels, dataWidth, ts ->
        doVideoLog("data:${String(data)},size:$size,sampleRate:$sampleRate,channels:$channels,dataWidth:$dataWidth,ts:$ts")
    }

    //缓冲更新
    private val onBufferingUpdateListener = PLOnBufferingUpdateListener{percent->
        doVideoLog("percent:$percent")
    }

    //视频播放完成监听
    private val onCompleteListener = PLOnCompletionListener{
        doVideoLog("video is completed!!!")
    }
     
    //视频发生错误监听
    private val onErrorListener = PLOnErrorListener { errorCode->
        when (errorCode) {
           PLOnErrorListener.ERROR_CODE_IO_ERROR->{
               //IO错误  服务器会自动重连
               doVideoLog("PLOnErrorListener.ERROR_CODE_IO_ERROR")
               return@PLOnErrorListener false
           }
           PLOnErrorListener.ERROR_CODE_OPEN_FAILED->{
               //打开失败
               doVideoLog("PLOnErrorListener.ERROR_CODE_OPEN_FAILED")
           }
           PLOnErrorListener.ERROR_CODE_SEEK_FAILED->{
               //进度条定位到某一具体位置失败
               doVideoLog("PLOnErrorListener.ERROR_CODE_SEEK_FAILED")
               return@PLOnErrorListener true
           }
           PLOnErrorListener.ERROR_CODE_CACHE_FAILED->{
               //缓冲失败
               doVideoLog("PLOnErrorListener.ERROR_CODE_CACHE_FAILED")
           }
           PLOnErrorListener.MEDIA_ERROR_UNKNOWN->{
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

}
