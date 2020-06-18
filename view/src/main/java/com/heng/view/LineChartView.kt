package com.heng.view
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.TypedValue
import android.view.MotionEvent
import android.view.View
import com.heng.common.log.doPersonLog
import com.heng.view.entity.Dot

class LineChartView : View {

    private var mAxisPaint : Paint? = null
    private var mDotPaint : Paint? = null
    private var mLinePaint : Paint? = null
    private var mGradientPaint : Paint? = null

    private var mLineColor = 0
    private var mDotNormalColor = 0
    private var mDotSelectedColor = 0
    private var mSelectedIndex = -1

    /*默认扩散颜色*/
    private val defaultGradientColor = intArrayOf(Color.BLUE, Color.GREEN)
    /*数据集*/
    var dataSet = intArrayOf()
    /*最大值*/
    var mMax = 0
    /*文本值*/
    var mAxis = arrayListOf<String>()

    private var mStep = 0
    private var mDots = arrayListOf<Dot>()

    private var mRadius = 0

    private var mClickedRadius = 0
    private var mTextRect : Rect? = null
    private var mPath : Path? = null

    private var mGradientPath : Path? = null
    private var mGap = 0

    constructor(context: Context, attributeSet: AttributeSet) : super(context, attributeSet) {
        initColors(context,attributeSet)
        initPaint()
        initVariable(context)
    }

    constructor(context: Context,attributeSet: AttributeSet,defaultInt: Int)
            : super(context,attributeSet,defaultInt){
        initColors(context,attributeSet)
        initPaint()
        initVariable(context)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {

        super.onSizeChanged(w, h, oldw, oldh)
        mDots.clear()
        val mWidth = width - paddingLeft - paddingRight
        val mHeight = height - paddingTop - paddingBottom
        mStep = mWidth.div(dataSet.size - 1)

        mAxisPaint?.getTextBounds(mAxis[0], 0, mAxis[0].length, mTextRect)
        val mBarHeight = mHeight - mTextRect!!.height() - mGap
        val heightRadio = (mBarHeight.div(mMax)).toFloat()

        for (i in dataSet.indices) {

            val dot = Dot()
            dot.value = dataSet[i]
            dot.transformedValue = (dot.value * heightRadio).toInt()
            dot.x = paddingLeft + mStep.times(i)
            dot.y = paddingTop + mBarHeight - dot.transformedValue
            if (i == 0) {
                mPath?.moveTo(dot.x.toFloat(), dot.y.toFloat())
                mGradientPath?.moveTo(dot.x.toFloat(), dot.y.toFloat())
            } else {
                mPath?.lineTo(dot.x.toFloat(), dot.y.toFloat())
                mGradientPath?.lineTo(dot.x.toFloat(), dot.y.toFloat())
            }

            if (i == dataSet.size - 1) {

                val bottom = paddingBottom + mBarHeight
                mGradientPath?.lineTo(dot.x.toFloat(),bottom.toFloat())

                val mDot = mDots[0]
                mGradientPath?.lineTo(mDot.x.toFloat(),bottom.toFloat())

                mGradientPath?.lineTo(mDot.x.toFloat(),mDot.y.toFloat())
            }

            mDots.add(dot)
        }

        val shader = LinearGradient(0F,0F,0F,mHeight.toFloat(),
                                    defaultGradientColor,null,Shader.TileMode.CLAMP)
        mGradientPaint?.shader = shader
    }


    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        canvas?.drawPath(mPath!!, mLinePaint!!)
        canvas?.drawPath(mGradientPath!!, mGradientPaint!!)
        for (i in mAxis.indices) {

            val textX = paddingLeft + mStep.times(i)
            val textY = height - paddingBottom
            canvas?.drawText(mAxis[i], textX.toFloat(), textY.toFloat(), mAxisPaint!!)

            val mDot = mDots[i]
            if (i == mSelectedIndex) {

                mDotPaint?.color = mDotSelectedColor
                canvas?.drawText(dataSet[i].toString(),mDot.x.toFloat(),
                                 (mDot.y.minus(mRadius).minus(mGap)).toFloat(),mAxisPaint!!)
            } else {

                mDotPaint?.color = mDotNormalColor
            }

            mDotPaint?.let { canvas?.drawCircle(mDot.x.toFloat(),mDot.y.toFloat(),mRadius.toFloat(),it) }
        }
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        when (event?.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                performClick()
                mSelectedIndex = getClickDotIndex(event.x, event.y)
                doPersonLog("xy->MotionEvent.ACTION_DOWN->mSelectedIndex:$mSelectedIndex")
                invalidate()
            }
            MotionEvent.ACTION_UP -> {
                doPersonLog("xy->MotionEvent.ACTION_UP")
                mSelectedIndex = -1
                invalidate()
            }
            else -> {}
        }
        return true
    }

    override fun performClick(): Boolean = super.performClick()

    private fun getClickDotIndex(x : Float, y : Float): Int {
        var index = -1
        for (i in mDots.indices) {
            val mDot = mDots[i]
            val left = mDot.x.minus(mClickedRadius).toFloat()
            val top = mDot.y.minus(mClickedRadius).toFloat()
            val right = mDot.x.plus(mClickedRadius).toFloat()
            val bottom = mDot.y.plus(mClickedRadius).toFloat()
            doPersonLog("data->x:$x left:$left right:$right")
            doPersonLog("data->y:$y top:$top  bottom:$bottom")
            val xInBounds = x in left..right
            val yInBounds = y in top..bottom
            if (xInBounds && yInBounds) {
                index = i
                break
            }
        }
        return index
    }

    private fun initColors(context: Context, attributeSet: AttributeSet) {

        val typedArray = context.obtainStyledAttributes(attributeSet, R.styleable.LineChartView)
        mLineColor = typedArray.getColor(R.styleable.LineChartView_line_color, Color.BLACK)
        mDotNormalColor = typedArray.getColor(R.styleable.LineChartView_dot_normal_color,Color.BLACK)
        mDotSelectedColor = typedArray.getColor(R.styleable.LineChartView_dot_selected_color,Color.BLUE)
        typedArray.recycle()
    }

    private fun initPaint() {

        mAxisPaint = Paint()
        mAxisPaint?.isAntiAlias = true
        mAxisPaint?.textSize = 20F
        mAxisPaint?.color = Color.BLACK
        mAxisPaint?.textAlign = Paint.Align.CENTER

        mDotPaint = Paint()
        mDotPaint?.isAntiAlias = true

        mLinePaint = Paint()
        mLinePaint?.isAntiAlias = true
        mLinePaint?.strokeWidth = 3F
        mLinePaint?.style = Paint.Style.STROKE
        mLinePaint?.color = mLineColor

        mGradientPaint = Paint()
        mGradientPaint?.isAntiAlias = true
    }

    private fun initVariable(context: Context) {

        mPath = Path()
        mGradientPath = Path()

        mTextRect = Rect()

        mRadius = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,4F,context.resources.displayMetrics).toInt()
        mClickedRadius = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,10F,context.resources.displayMetrics).toInt()

        mGap = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,8F,context.resources.displayMetrics).toInt()
    }
}