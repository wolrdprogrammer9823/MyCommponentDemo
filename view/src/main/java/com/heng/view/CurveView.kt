package com.heng.view
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.TypedValue
import android.view.View
import com.heng.common.define.Array2D
import com.heng.view.entity.Dot

const val SMOOTHNESS_RATIO = 0.16F

open class CurveView : View {

    private var mDotPaint : Paint? = null
    private var mAxisPaint : Paint? = null
    private var mCurvePaint : Paint? = null

    private var mGap = 0
    private var mRadius = 0

    var mMax = 0
    var dataSet = intArrayOf()
    var mAxisTexts = arrayListOf<String>()

    private var mCurvePath : Path? = null

    private var mDots = arrayListOf<Dot>()

    private var mTextRect : Rect? = null
    private var mStep = 0

    private var mControlDots = Array2D<Float>(2, 2)

    constructor(context: Context,attributeSet: AttributeSet) : super(context,attributeSet) {
        initPaint()
        initVariable(context)
    }

    constructor(context: Context,attributeSet: AttributeSet,defaultInt: Int)
                : super(context,attributeSet,defaultInt) {
        initPaint()
        initVariable(context)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        mDots.clear()
        val mWidth = width - paddingStart - paddingEnd
        val mHeight = height - paddingTop - paddingBottom
        mStep = mWidth.div(dataSet.size - 1)
        mAxisPaint?.getTextBounds(mAxisTexts[0], 0, mAxisTexts[0].length, mTextRect)
        val mMaxHeight = mHeight.minus(mTextRect!!.height()).minus(mGap)
        val heightRatio = mMaxHeight.div(mMax.times(1.0f))

        //初始化点
        for (i in dataSet.indices) {
            val dot = Dot()
            dot.value = dataSet[i]
            dot.transformedValue = dot.value.times(heightRatio).toInt()
            dot.x = paddingStart.plus(mStep.times(i))
            dot.y = paddingTop.plus(mMaxHeight).minus(dot.transformedValue)
            mDots.add(dot)
        }

        //规划曲线路径
        for (i in 0.until(dataSet.size - 1)) {
            if (i == 0) {
                mCurvePath?.moveTo(mDots[0].x.toFloat(), mDots[0].y.toFloat())
            }
            //计算三阶贝塞尔曲线的控制点
            calculateControlDots(i)
            //使用三阶贝塞尔曲线连接下一个点
            mCurvePath?.cubicTo(
                mControlDots[0, 0]!!.toFloat(),
                mControlDots[0, 1]!!.toFloat(),
                mControlDots[1, 0]!!.toFloat(),
                mControlDots[1, 1]!!.toFloat(),
                mDots[i+1].x.toFloat(),
                mDots[i+1].y.toFloat()
            )
        }
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        //绘制路径
        canvas?.drawPath(mCurvePath!!, mCurvePaint!!)
        for (i in mDots.indices) {
            val textX = paddingStart.plus(mStep.times(i))
            val textY = height.minus(paddingBottom)
            canvas?.drawText(mAxisTexts[i], textX.toFloat(), textY.toFloat(), mAxisPaint!!)
            canvas?.drawCircle(mDots[i].x.toFloat(),mDots[i].y.toFloat(),mRadius.toFloat(),mDotPaint!!)
        }
    }

    private fun calculateControlDots(i: Int) {

        //第一个控制点
        val x1: Float
        val y1: Float
        //第二个控制点
        val x2: Float
        val y2: Float

        //当前点
        val currentDot = mDots[i]
        //上一个点
        val previousDot: Dot?
        //下一个点
        val nextDot: Dot?
        //下下个点
        val nextNextDot: Dot?

        //当前不是第一个点时获取上一点,否则上一点等于当前点
        previousDot = if (i > 0) {
            mDots[i - 1]
        } else {
            currentDot
        }

        //当前点不是最后一个点时获取下一点,否则下一点等于当前点
        nextDot = if (i < mDots.size - 1) {
            mDots[i + 1]
        } else {
            currentDot
        }

        //当前点不是倒数第二个点时获取下一点,否则下一点等于下下一点
        nextNextDot = if (i < mDots.size - 2) {
            mDots[i + 2]
        } else {
            nextDot
        }

        x1 = currentDot.x.plus(SMOOTHNESS_RATIO.times(nextDot.x.minus(previousDot.x)))
        y1 = currentDot.y.plus(SMOOTHNESS_RATIO.times(nextDot.x.minus(previousDot.x)))

        x2 = nextDot.x.minus(SMOOTHNESS_RATIO.times(nextNextDot.x.minus(currentDot.x)))
        y2 = nextDot.y.minus(SMOOTHNESS_RATIO.times(nextNextDot.y.minus(currentDot.y)))

        mControlDots[0,0] = x1
        mControlDots[0,1] = y1
        mControlDots[1,0] = x2
        mControlDots[1,1] = y2
    }

    /*初始化画笔*/
    private fun initPaint() {

        mAxisPaint = Paint()
        mAxisPaint?.isAntiAlias = true
        mAxisPaint?.textSize = 20F
        mAxisPaint?.color = Color.BLACK
        mAxisPaint?.textAlign = Paint.Align.CENTER

        mCurvePaint = Paint()
        mCurvePaint?.isAntiAlias = true
        mCurvePaint?.style = Paint.Style.STROKE
        mCurvePaint?.strokeWidth = 3F
        mCurvePaint?.color = Color.BLUE

        mDotPaint = Paint()
        mDotPaint?.color = Color.GREEN
        mDotPaint?.isAntiAlias = true
    }

    /*初始化变量*/
    private fun initVariable(context: Context) {

        mTextRect = Rect()
        mCurvePath = Path()

        mGap = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,8F,context.resources.displayMetrics).toInt()
        mRadius = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,4F,context.resources.displayMetrics).toInt()
    }

}