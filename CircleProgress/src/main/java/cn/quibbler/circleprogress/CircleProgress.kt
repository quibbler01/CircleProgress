package cn.quibbler.circleprogress

import android.content.Context
import android.graphics.*
import android.os.Parcel
import android.os.Parcelable
import android.text.TextUtils
import android.util.AttributeSet
import android.view.View
import androidx.annotation.IntDef
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin

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

        canvas?.save()
        canvas?.rotate(mStartDegree.toFloat(), mCenterX, mCenterY)
        drawProgress(canvas)
        canvas?.restore()

        drawProgressText(canvas)
    }

    private fun drawProgressText(canvas: Canvas?) {
        val progressText: CharSequence = mProgressFormatter?.format(mProgress, mMax) ?: ""
        if (TextUtils.isEmpty(progressText)) return

        mProgressTextPaint.textSize = mProgressTextSize
        mProgressTextPaint.color = mProgressTextColor
        mProgressTextPaint.getTextBounds(progressText.toString(), 0, progressText.length, mProgressTextRect)
        canvas?.drawText(progressText, 0, progressText.length, mCenterX, mCenterY + mProgressTextRect.height() / 2, mProgressTextPaint)
    }

    private fun drawProgress(canvas: Canvas?) {
        when (mStyle) {
            SOLID -> {
                drawSolidProgress(canvas)
            }
            SOLID_LINE -> {
                drawSolidLineProgress(canvas)
            }
            LINE -> {
                drawLineProgress(canvas)
            }
            else -> {
                drawLineProgress(canvas)
            }
        }
    }

    private fun drawLineProgress(canvas: Canvas?) {
        val unitDegrees: Float = (2f * Math.PI / mLineCount).toFloat()
        val outerCircleRadius: Float = mRadius
        val interCircleRadius: Float = mRadius - mLineWidth

        val progressLineCount: Int = (mProgress.toFloat() / mMax.toFloat() * mLineCount).toInt()

        for (i in 0 until mLineCount) {
            val rotateDegrees: Float = i * unitDegrees

            val startX: Float = mCenterX + cos(rotateDegrees) * interCircleRadius
            val startY: Float = mCenterY + sin(rotateDegrees) * interCircleRadius

            val stopX: Float = mCenterX + cos(rotateDegrees) * outerCircleRadius
            val stopY: Float = mCenterY + sin(rotateDegrees) * outerCircleRadius

            if (mDrawBackgroundOutSideProgress) {
                if (i >= progressLineCount) {
                    canvas?.drawLine(startX, startY, stopX, stopY, mProgressBackgroundPaint)
                }
            } else {
                canvas?.drawLine(startX, startY, stopX, stopY, mProgressBackgroundPaint)
            }
            if (i < progressLineCount) {
                canvas?.drawLine(startX, startY, stopX, stopY, mProgressPaint)
            }
        }
    }

    private fun drawSolidLineProgress(canvas: Canvas?) {
        if (mDrawBackgroundOutSideProgress) {
            val startAngle: Float = MAX_DEGREE * mProgress / mMax
            val sweepAngle: Float = MAX_DEGREE - startAngle
            canvas?.drawArc(mProgressRectF, startAngle, sweepAngle, false, mProgressBackgroundPaint)
        } else {
            canvas?.drawArc(mProgressRectF, 0f, MAX_DEGREE, false, mProgressBackgroundPaint)
        }
        canvas?.drawArc(mProgressRectF, 0f, MAX_DEGREE * mProgress / mMax, false, mProgressPaint)
    }

    private fun drawSolidProgress(canvas: Canvas?) {
        if (mDrawBackgroundOutSideProgress) {
            val startAngle: Float = MAX_DEGREE * mProgress / mMax
            val sweepAngle: Float = MAX_DEGREE - startAngle
            canvas?.drawArc(mProgressRectF, startAngle, sweepAngle, true, mProgressBackgroundPaint)
        } else {
            canvas?.drawArc(mProgressRectF, 0f, MAX_DEGREE, true, mProgressBackgroundPaint)
        }
        canvas?.drawArc(mProgressRectF, 0f, MAX_DEGREE * mProgress / mMax, true, mProgressPaint)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        mBoundsRectF.left = paddingLeft.toFloat()
        mBoundsRectF.top = paddingTop.toFloat()
        mBoundsRectF.right = w - paddingRight.toFloat()
        mBoundsRectF.bottom = h - paddingBottom.toFloat()

        mCenterX = mBoundsRectF.centerX()
        mCenterY = mBoundsRectF.centerY()

        mRadius = min(mBoundsRectF.width(), mBoundsRectF.height()) / 2

        mProgressRectF.set(mBoundsRectF)

        updateProgressShader()

        mProgressRectF.inset(mProgressStrokeWidth / 2, mProgressStrokeWidth / 2)
    }

    override fun onSaveInstanceState(): Parcelable {
        val superState = super.onSaveInstanceState()
        val ss: SavedState = SavedState(superState)
        ss.progress = mProgress
        return ss
    }

    override fun onRestoreInstanceState(state: Parcelable?) {
        val ss = state as SavedState
        super.onRestoreInstanceState(state)
        mProgress = ss.progress
    }

    fun setProgressFormatter(progressFormatter: ProgressFormatter?) {
        mProgressFormatter = progressFormatter
        invalidate()
    }

    fun setProgressStrokeWidth(progressStrokeWidth: Float) {
        mProgressStrokeWidth = progressStrokeWidth
        mProgressRectF.set(mBoundsRectF)

        updateProgressShader()

        mProgressRectF.inset(mProgressStrokeWidth / 2, mProgressStrokeWidth / 2)

        invalidate()
    }

    fun setProgressTextSize(progressTextSize: Float) {
        mProgressTextSize = progressTextSize
        invalidate()
    }

    fun setProgressTextColor(progressTextColor: Int) {
        mProgressTextColor = progressTextColor
        invalidate()
    }

    fun setProgressStartColor(progressStartColor: Int) {
        mProgressStartColor = progressStartColor
        updateProgressShader()
        invalidate()
    }

    fun setProgressEndColor(progressEndColor: Int) {
        mProgressEndColor = progressEndColor
        updateProgressShader()
        invalidate()
    }

    fun setProgressBackgroundColor(progressBackgroundColor: Int) {
        mProgressBackgroundColor = progressBackgroundColor
        mProgressBackgroundPaint.color = mProgressBackgroundColor
        invalidate()
    }

    fun setLineCount(lineCount: Int) {
        mLineCount = lineCount
        invalidate()
    }

    fun setLineWidth(lineWidth: Float) {
        mLineWidth = lineWidth
        invalidate()
    }

    fun setStyle(@Style style: Int) {
        mStyle = style
        mProgressPaint.style = if (mStyle == SOLID) Paint.Style.FILL else Paint.Style.STROKE
        mProgressBackgroundPaint.style = if (mStyle == SOLID) Paint.Style.FILL else Paint.Style.STROKE
        invalidate()
    }

    fun setBlurRadius(blurRadius: Int) {
        mBlurRadius = blurRadius
        updateMaskBlurFilter()
        invalidate()
    }

    fun setShader(@ShaderMode shader: Int) {
        mShader = shader
        updateProgressShader()
        invalidate()
    }

    fun setCap(cap: Paint.Cap) {
        mCap = cap
        mProgressPaint.strokeCap = mCap
        mProgressBackgroundPaint.strokeCap = mCap
        invalidate()
    }

    fun setStartDegree(startDegree: Int) {
        mStartDegree = startDegree
        invalidate()
    }

    fun setDrawBackgroundOutSideProgress(drawBackgroundOutSideProgress: Boolean) {
        mDrawBackgroundOutSideProgress = drawBackgroundOutSideProgress
        invalidate()
    }

    fun getProgress(): Int = mProgress

    fun setProgress(progress: Int) {
        mProgress = progress
        invalidate()
    }

    fun getMax(): Int = mMax

    fun setMax(max: Int) {
        mMax = max
        invalidate()
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

    private class SavedState : BaseSavedState {

        companion object {
            @JvmField
            val CREATOR: Parcelable.Creator<SavedState> = object : Parcelable.Creator<SavedState> {
                override fun createFromParcel(p: Parcel): SavedState {
                    return SavedState(p)
                }

                override fun newArray(size: Int): Array<SavedState?> {
                    return arrayOfNulls<SavedState>(size)
                }
            }
        }

        var progress = 0

        constructor(source: Parcel) : this(source, null) {
            progress = source.readInt()
        }

        constructor(source: Parcel?, loader: ClassLoader?) : super(source, loader)

        constructor(superState: Parcelable?) : super(superState)

        override fun writeToParcel(out: Parcel, flags: Int) {
            super.writeToParcel(out, flags)
            out.writeInt(progress)
        }

    }

    @Retention(AnnotationRetention.SOURCE)
    @IntDef(LINE, SOLID, SOLID_LINE)
    private annotation class Style

    @Retention(AnnotationRetention.SOURCE)
    @IntDef(LINEAR, RADIAL, SOLID_LINE)
    private annotation class ShaderMode

}