package cn.quibbler.circleprogress

import android.content.Context
import android.graphics.BlurMaskFilter
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View

/**
 *
 * Package:        cn.quibbler.circleprogress
 * ClassName:      CircleProgress
 * Description:    Customizable circular progress bar
 * Author:         Quibbler
 * CreateDate:     2022/08/01 10:09
 */
class CircleProgress : View {

    companion object {
        const val LINE = 0
        const val SOLID = 1
        const val SOLID_LINE = 2

        /**
         * Shader
         */
        const val LINEAR = 0
        const val RADIAL = 1
        const val SWEEP = 2

        private const val DEFAULT_MAX = 100
        private const val MAX_DEGREE = 360f
        private const val LINEAR_START_DEGREE = 90f

        private const val DEFAULT_START_DEGREE = -90
        private const val DEFAULT_LINE_COUNT = 45
        private const val DEFAULT_LINE_WIDTH = 4f
        private const val DEFAULT_PROGRESS_TEXT_SIZE = 12f
        private const val DEFAULT_PROGRESS_STROKE_WIDTH = 1f

        private val COLOR_FFF2A670 = Color.parseColor("#fff2a670")
        private val COLOR_FFD3D3D5 = Color.parseColor("#ffe3e3e5")
    }

    private val mProgressRectF = RectF()
    private val mBoundsRectF = RectF()
    private val mProgressTextRect = Rect()

    private val mProgressPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val mProgressBackgroundPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val mProgressTextPaint = Paint(Paint.ANTI_ALIAS_FLAG)

    private var mRadius: Float = 0f
    private var mCenterX: Float = 0f
    private var mCenterY: Float = 0f

    private var mProgress: Int = 0

    private var mMax: Int = DEFAULT_MAX

    /**
     * only work well in the Line Style,represents the line count of the rings include
     */
    private var mLineCount: Int = DEFAULT_LINE_COUNT

    /**
     * only work well in the Line Style,Height of the line of the  progress bar.
     */
    private var mLineWidth: Float = DEFAULT_LINE_WIDTH

    /**
     *
     */
    private var mProgressStrokeWidth = DEFAULT_PROGRESS_STROKE_WIDTH

    private var mProgressTextColor = COLOR_FFF2A670
    private var mProgressTextSize = DEFAULT_PROGRESS_TEXT_SIZE

    private var mProgressStartColor = COLOR_FFF2A670
    private var mProgressEndColor = COLOR_FFD3D3D5

    private var mProgressBackgroundColor = Color.TRANSPARENT

    private var mStartDegree = DEFAULT_START_DEGREE
    private var mDrawBackgroundOutSideProgress = false
    
    private var mStyle = LINE

    private var mShader = LINEAR

    private var mCap = Paint.Cap.BUTT

    private var mBlurRadius = 0

    private var mBlurStyle = BlurMaskFilter.Blur.INNER

    constructor(context: Context?) : this(context, null)

    constructor(context: Context?, attrs: AttributeSet?) : this(context, attrs, 0)

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : this(context, attrs, defStyleAttr, 0)

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) : super(context, attrs, defStyleAttr, defStyleRes) {
        initFromAttribute(attrs)
        initPaint()
    }

    private fun initFromAttribute(attrs: AttributeSet?) {
        val a = context.obtainStyledAttributes(attrs, R.styleable.CircleProgress)

        a.recycle()
    }

    private fun initPaint() {

    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
    }

}