package com.heng.video.widgets
import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import com.pili.pldroid.player.widget.PLVideoView

class DefinePLVideoView : PLVideoView {

    constructor(context: Context,attributeSet: AttributeSet) : super(context,attributeSet) {}

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {

        return super.dispatchTouchEvent(ev)
    }
}