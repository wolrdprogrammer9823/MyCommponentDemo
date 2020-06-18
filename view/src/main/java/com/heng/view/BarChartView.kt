package com.heng.view
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.TypedValue
import android.view.MotionEvent
import android.view.View
import com.heng.common.log.doPersonLog
import com.heng.view.entity.Bar

//延迟时间
const val DELAY = 10L
//增长步进
const val BAR_STEP_GROW = 15

open class BarChartView : View {

    private var mBarPaint : Paint? = null
    private var mAxisPaint : Paint? = null

    //柱状图数据源
    var mDataList = floatArrayOf()
    //坐标值数据源
   var mHorizontalAxis = arrayListOf<String>()

    private var mBarWidth = 0.0F
    //数据集合的最大值
    var mMax = 0

    //柱状图与文本的间距
    private var mGap = 0
    private var mRadius = 0
    private var mSelectedIndex = -1
    //是否允许柱状图值增长(动画)
    var mEnableGrowIncrement = false

    private var mTextRect : Rect? = null
    private var mTempRectF : RectF? = null

    private var mBars = arrayListOf<Bar>()

    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attributeSet: AttributeSet) : super(context, attributeSet){
        init()
    }

    constructor(context: Context,attributeSet: AttributeSet,defaultInt: Int) : super(context,attributeSet,defaultInt) {
        init()
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {

        super.onSizeChanged(w, h, oldw, oldh)
        mBars.clear()

        //去除padding 计算柱状图所占的宽和高
        val width = width - paddingLeft - paddingRight
        val height = height - paddingBottom - paddingTop

        val step = width.div(mDataList.size)

        mRadius = mBarWidth.div(2).toInt()

        //计算第一条柱状图左边的位置
        var barLeft = paddingLeft + step.div(2) - mRadius

        //通过坐标文本画笔计算第一个文本所占的矩形边界 获取其高度  为计算mMaxBarHeight提供数据
        mAxisPaint?.getTextBounds(mHorizontalAxis[0], 0, mHorizontalAxis[0].length, mTextRect)

        //计算柱状图高度的最大大小
        val mMaxBarHeight = height - mTextRect?.height()!! - mGap

        //计算柱状图高度最大大小与最大数值的比例
        val heightRatio = mMaxBarHeight.div(mMax).toFloat()

        for (data in mDataList) {

            val bar = Bar()
            bar.value = data
            //计算原始数据对应的高度像素大小
            bar.transformedValue = data.times(heightRatio)
            bar.left = barLeft
            bar.right = barLeft.plus(mBarWidth).toInt()
            bar.top = (paddingTop.plus(mMaxBarHeight).minus(bar.transformedValue)).toInt()
            bar.bottom = paddingTop.plus(mMaxBarHeight)

            //初始化绘制柱状图当时的top值  用作动画
            bar.currentTop = bar.bottom

            mBars.add(bar)

            barLeft = barLeft.plus(step)
        }
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        if (mEnableGrowIncrement) {
           drawBarsWithAnimation(canvas)
        } else {
            drawBars(canvas)
        }
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        /*正在执行动画中 不允许执行触摸事件*/
        if (mEnableGrowIncrement) {
            return false
        }
        when (event?.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                performClick()
                for (i in mBars.indices) {
                    val bar = mBars[i]
                    if (bar.isInside(event.x, event.y)) {
                        mEnableGrowIncrement = false
                        mSelectedIndex = i
                        invalidate()
                        break
                    }
                }
            }

            MotionEvent.ACTION_UP -> {
                mEnableGrowIncrement = false
                mSelectedIndex = -1
                invalidate()
            }
            else->{}
        }
        return true
    }

    override fun performClick(): Boolean {
        return super.performClick()
    }

    /*绘制柱状图*/
    private fun drawBars(canvas: Canvas?) {
        for (i in mBars.indices) {

            val bar = mBars[i]

            //绘制底部坐标文本
            val text = mHorizontalAxis[i]
            val textX = bar.left.plus(mRadius).toFloat()
            val textY = height.minus(paddingBottom).toFloat()
            mAxisPaint?.let { canvas?.drawText(text, textX, textY, it) }

            //绘制当前选中点的颜色
            if (i == mSelectedIndex) {
                mBarPaint?.color = Color.RED
                val selectX = bar.left.minus(mRadius).toFloat()
                val selectY = bar.top.minus(mGap).toFloat()
                mAxisPaint?.let { canvas?.drawText(bar.value.toString(),selectX,selectY,it) }
            } else {
                mBarPaint?.color = Color.BLUE
            }

            //设置条形柱状图
            mTempRectF?.set(bar.left.toFloat(),bar.top.toFloat(),bar.right.toFloat(),bar.bottom.toFloat())
            mBarPaint?.let { canvas?.drawRoundRect(mTempRectF!!, mRadius.toFloat(), mRadius.toFloat(), it) }
        }
    }

    private fun drawBarsWithAnimation(canvas: Canvas?) {
        for (i in mBars.indices) {

            val bar = mBars[i]

            //绘制底部坐标文本
            val text = mHorizontalAxis[i]
            val textX = bar.left.plus(mRadius).toFloat()
            val textY = height.minus(paddingBottom).toFloat()
            mAxisPaint?.let { canvas?.drawText(text, textX, textY, it) }

            //更新当前柱状图顶部位置
            bar.currentTop = bar.currentTop - BAR_STEP_GROW
            doPersonLog("mEnableGrowIncrement - > $i -> ${bar.currentTop}")
            //doPersonLog("mEnableGrowIncrement - > $i -> currentTop -> ${bar.currentTop <= bar.top}")
            //计算出来的currentTop小于top 说明越界了
            if (bar.currentTop <= bar.top) {
                bar.currentTop = bar.top
                //高度最高的柱状图顶部位置为paddingTop 若currentTop==paddingTop 说明动画已经完成
                //需要重置动画变量
                //doPersonLog("mEnableGrowIncrement - > $i -> ${bar.currentTop} -> $paddingTop")
                //doPersonLog("mEnableGrowIncrement->${bar.currentTop == paddingTop}")
                if (bar.currentTop == paddingTop) {
                    mEnableGrowIncrement = false
                }
            }

            //设置条形柱状图
            mTempRectF?.set(bar.left.toFloat(),bar.currentTop.toFloat(),bar.right.toFloat(),bar.bottom.toFloat())
            mBarPaint?.let { canvas?.drawRoundRect(mTempRectF!!, mRadius.toFloat(), mRadius.toFloat(), it) }
        }

        //doPersonLog("mEnableGrowIncrement $mEnableGrowIncrement")

        //延时触发机制 调用onDraw方法
        if (mEnableGrowIncrement) {
            postInvalidateDelayed(DELAY)
        }
    }


    /*初始化方法*/
    private fun init() {

        mAxisPaint = Paint()
        mAxisPaint?.isAntiAlias = true
        mAxisPaint?.color = Color.BLUE
        mAxisPaint?.textSize = 20F
        mAxisPaint?.textAlign = Paint.Align.CENTER

        mBarPaint = Paint()
        mBarPaint?.isAntiAlias = true
        mBarPaint?.color = Color.BLUE

        mTextRect = Rect()
        mTempRectF = RectF()

        /*柱状图默认宽为8dp*/
        mBarWidth = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,8F,context.resources.displayMetrics)
        /*默认间距*/
        mGap = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,8f,context.resources.displayMetrics).toInt()
    }

}