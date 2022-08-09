package cn.quibbler.circleprogress

import android.content.Context

/**
 * convert dp value to px
 *
 * @param context [Context] to get density
 * @param dpValue dp value
 * @return Int px value
 */
public fun dip2px(context: Context, dpValue: Float): Int {
    val scale = context.resources.displayMetrics.density
    return (dpValue * scale + 0.5f).toInt()
}