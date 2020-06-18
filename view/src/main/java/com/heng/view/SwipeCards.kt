package com.heng.view
import android.content.Context
import android.util.AttributeSet
import android.util.TypedValue
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.customview.widget.ViewDragHelper
import com.heng.common.log.doCommonLog
import kotlin.math.abs

const val MAX_DEGREE = 60
const val MAX_ALPHA_RANGE = 0.5F

class SwipeCards : ViewGroup {

    private var mCenterX = 0
    private var mCenterY = 0
    private var mCardGap = 0

    private var dismissChildCount = 0

    private var viewDragHelper : ViewDragHelper? = null

    constructor(context: Context, attributeSet: AttributeSet)
               : super(context, attributeSet) {
        initObject(context)
    }

    constructor(context: Context,attributeSet: AttributeSet,defaultInt: Int)
               : super(context,attributeSet,defaultInt) {
        initObject(context)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        mCenterX = w / 2
        mCenterY = h / 2
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        measureChildren(widthMeasureSpec, heightMeasureSpec)
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        for (i in 0.until(childCount + dismissChildCount)) {
            val child = getChildAt(i) ?: continue
            val mLeft = mCenterX.minus(child.measuredWidth shr 1)
            val mTop = mCenterY.minus(child.measuredHeight shr 1) + mCardGap.times(childCount - i + dismissChildCount)
            val mRight = mLeft.plus(child.measuredWidth)
            val mBottom = mTop.plus(child.measuredHeight)
            child.layout(mLeft, mTop, mRight, mBottom)
        }
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        performClick()
        viewDragHelper?.processTouchEvent(event!!)
        return true
    }

    override fun computeScroll() {
        super.computeScroll()
        //滚动完成 刷新view
        if (viewDragHelper?.continueSettling(false)!!) {
            invalidate()
        }
    }

    override fun performClick(): Boolean = super.performClick()

    private fun initObject(context: Context) {

        mCardGap = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,10F,
             context.resources.displayMetrics).toInt()

        viewDragHelper = ViewDragHelper.create(this,object : ViewDragHelper.Callback() {
            override fun tryCaptureView(child: View, pointerId: Int): Boolean {
                /*每次只允许滑动最上层的那个view*/
                val mSelectedIndex = indexOfChild(child)
                doCommonLog("cards","mSelectedIndex $mSelectedIndex")
                return mSelectedIndex != -1 && mSelectedIndex == childCount - 1
            }
            override fun clampViewPositionHorizontal(child: View, left: Int, dx: Int): Int = left
            override fun clampViewPositionVertical(child: View, top: Int, dy: Int): Int = top
            override fun onViewPositionChanged(changedView: View, left: Int, top: Int, dx: Int, dy: Int) {
                super.onViewPositionChanged(changedView, left, top, dx, dy)
                //计算位置改变后  与原来的位置中心变化
                val diff = left.plus(changedView.width shr 1).minus(mCenterX)
                val ratio = diff.times(1.0f).div(width)
                val degree = MAX_DEGREE.times(ratio)
                changedView.rotation = degree
                val alpha = 1.minus(abs(ratio) * MAX_ALPHA_RANGE)
                changedView.alpha = alpha
            }

            override fun onViewReleased(releasedChild: View, xvel: Float, yvel: Float) {
                super.onViewReleased(releasedChild, xvel, yvel)
                val left = releasedChild.left
                val right = releasedChild.right
                when{
                    left > mCenterX ->{
                        //向右滑出
                        animateToRight(releasedChild)
                    }

                    right < mCenterX ->{
                        //向左滑出
                        animateToLeft(releasedChild)
                    }

                    else ->{
                        //复原
                        animateToCenter(releasedChild)
                    }
                }
            }
        })
    }

    private fun animateToRight(releasedView : View) {
        val left = width.plus(releasedView.height)
        val top = releasedView.top
        viewDragHelper?.smoothSlideViewTo(releasedView, left, top)
        this.removeViewAt(childCount - 1)
        dismissChildCount++
        invalidate()
    }

    private fun animateToLeft(releaseView: View) {
        val left = -width
        val top = 0
        viewDragHelper?.smoothSlideViewTo(releaseView, left, top)
        this.removeViewAt(childCount - 1)
        dismissChildCount++
        invalidate()
    }

    private fun animateToCenter(releaseView: View) {
        val left = mCenterX.minus(releaseView.width shr 1)
        val childIndex = indexOfChild(releaseView)
        val top = mCenterY.minus(releaseView.height shr 1).plus(mCardGap.times(childCount - childIndex))
        viewDragHelper?.smoothSlideViewTo(releaseView, left, top)
        invalidate()
    }
}