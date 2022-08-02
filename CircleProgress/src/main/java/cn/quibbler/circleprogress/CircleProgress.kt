package cn.quibbler.circleprogress

import android.content.Context
import android.graphics.Canvas
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

    constructor(context: Context?) : this(context, null)
    constructor(context: Context?, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : this(context, attrs, defStyleAttr, 0)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) : super(context, attrs, defStyleAttr, defStyleRes) {

    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
    }

}