package cn.quibbler.circleprogress

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import androidx.annotation.IntDef

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

    private var mProgressFormatter: ProgressFormatter? = DefaultProgressFormatter()

    @Style
    private var mStyle = LINE

    @ShaderMode
    private var mShader = LINEAR

    private var mCap = Paint.Cap.BUTT

    private var mBlurRadius = 0

    private var mBlurStyle = BlurMaskFilter.Blur.INNER

    constructor(context: Context) : this(context, null)

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : this(context, attrs, defStyleAttr, 0)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) : super(context, attrs, defStyleAttr, defStyleRes) {
        initFromAttribute(context, attrs)
        initPaint()
    }

    private fun initFromAttribute(context: Context, attrs: AttributeSet?) {
        val a = context.obtainStyledAttributes(attrs, R.styleable.CircleProgress)

        mLineCount = a.getInt(R.styleable.CircleProgress_line_count, DEFAULT_LINE_COUNT)
        mLineWidth = a.getDimensionPixelSize(R.styleable.CircleProgress_line_width, dip2px(context, DEFAULT_LINE_WIDTH)).toFloat()

        mStyle = a.getInt(R.styleable.CircleProgress_progress_style, LINE)
        mShader = a.getInt(R.styleable.CircleProgress_progress_shader, LINEAR)
        mCap = if (a.hasValue(R.styleable.CircleProgress_progress_stroke_cap)) Paint.Cap.values()[a.getInt(R.styleable.CircleProgress_progress_stroke_cap, 0)] else Paint.Cap.BUTT

        mProgressTextSize = a.getDimensionPixelSize(R.styleable.CircleProgress_progress_text_size, dip2px(context, DEFAULT_PROGRESS_TEXT_SIZE)).toFloat()
        mProgressStrokeWidth = a.getDimensionPixelSize(R.styleable.CircleProgress_progress_stroke_width, dip2px(context, DEFAULT_PROGRESS_STROKE_WIDTH)).toFloat();

        mProgressStartColor = a.getColor(R.styleable.CircleProgress_progress_start_color, COLOR_FFF2A670)
        mProgressEndColor = a.getColor(R.styleable.CircleProgress_progress_end_color, COLOR_FFF2A670)
        mProgressTextColor = a.getColor(R.styleable.CircleProgress_progress_text_color, COLOR_FFF2A670)
        mProgressBackgroundColor = a.getColor(R.styleable.CircleProgress_progress_background_color, COLOR_FFD3D3D5)

        mStartDegree = a.getInt(R.styleable.CircleProgress_progress_start_degree, DEFAULT_START_DEGREE)
        mDrawBackgroundOutSideProgress = a.getBoolean(R.styleable.CircleProgress_drawBackgroundOutSideProgress, false)

        mBlurRadius = a.getDimensionPixelOffset(R.styleable.CircleProgress_progress_blur_radius, 0)
        val blueStyle: Int = a.getInt(R.styleable.CircleProgress_progress_blur_style, 0)
        mBlurStyle = when (blueStyle) {
            1 -> BlurMaskFilter.Blur.SOLID
            2 -> BlurMaskFilter.Blur.OUTER
            3 -> BlurMaskFilter.Blur.INNER
            else -> BlurMaskFilter.Blur.NORMAL
        }

        a.recycle()
    }

    private fun initPaint() {
        mProgressTextPaint.textAlign = Paint.Align.CENTER
        mProgressTextPaint.textSize = mProgressTextSize

        mProgressPaint.style = if (mStyle == SOLID) Paint.Style.FILL else Paint.Style.STROKE
        mProgressPaint.strokeWidth = mProgressStrokeWidth
        mProgressPaint.color = mProgressStartColor
        mProgressPaint.strokeCap = mCap
        updateMaskBlurFilter()

        mProgressBackgroundPaint.style = if (mStyle == SOLID) Paint.Style.FILL else Paint.Style.STROKE
        mProgressBackgroundPaint.strokeWidth = mProgressStrokeWidth
        mProgressBackgroundPaint.color = mProgressBackgroundColor
        mProgressBackgroundPaint.strokeCap = mCap
    }

    private fun updateMaskBlurFilter() {
        if (mBlurRadius > 0) {
            setLayerType(LAYER_TYPE_SOFTWARE, mProgressPaint)
            mProgressPaint.maskFilter = BlurMaskFilter(mBlurRadius.toFloat(), mBlurStyle)
        } else {
            mProgressPaint.maskFilter = null
        }
    }

    /**
     *
     */
    private fun updateProgressShader() {
        if (mProgressStartColor != mProgressEndColor) {
            var shader: Shader? = null
            when (mShader) {
                LINEAR -> {
                    shader = LinearGradient(mProgressRectF.left, mProgressRectF.top, mProgressRectF.left, mProgressRectF.bottom, mProgressStartColor, mProgressEndColor, Shader.TileMode.CLAMP)
                    val matrix: Matrix = Matrix()
                    matrix.setRotate(LINEAR_START_DEGREE, mCenterX, mCenterY)
                    shader.setLocalMatrix(matrix)
                }
                RADIAL -> {
                    shader = RadialGradient(mCenterX, mCenterY, mRadius, mProgressStartColor, mProgressEndColor, Shader.TileMode.CLAMP)
                }
                SWEEP -> {
                    val radian = (mProgressStrokeWidth / Math.PI * 2.0f / mRadius)
                    val rotateDegrees: Float = -(if (mCap == Paint.Cap.BUTT && mStyle == SOLID_LINE) 0f else Math.toDegrees(radian).toFloat())
                    shader = SweepGradient(mCenterX, mCenterY, intArrayOf(mProgressStartColor, mProgressEndColor), floatArrayOf(0.0f, 1.0f))
                    val matrix: Matrix = Matrix()
                    matrix.setRotate(rotateDegrees, mCenterX, mCenterY)
                    shader.setLocalMatrix(matrix)
                }
            }
            mProgressPaint.shader = shader
        } else {
            mProgressPaint.shader = null
            mProgressPaint.color = mProgressStartColor
        }
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
    }

    /**
     *
     */
    interface ProgressFormatter {
        fun format(progress: Int, max: Int): CharSequence
    }

    /**
     * Default ProgressFormatter
     */
    class DefaultProgressFormatter : ProgressFormatter {
        companion object {
            private const val DEFAULT_PATTERN = "%d%%"
        }

        override fun format(progress: Int, max: Int): CharSequence {
            return String.format(DEFAULT_PATTERN, (progress.toFloat() / max.toFloat() * 100).toInt())
        }
    }

    @Retention(AnnotationRetention.SOURCE)
    @IntDef(LINE, SOLID, SOLID_LINE)
    private annotation class Style

    @Retention(AnnotationRetention.SOURCE)
    @IntDef(LINEAR, RADIAL, SOLID_LINE)
    private annotation class ShaderMode

}