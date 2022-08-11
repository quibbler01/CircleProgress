package cn.quibbler.demo

import android.animation.ValueAnimator
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.LinearInterpolator
import android.widget.BaseAdapter
import cn.quibbler.circleprogress.CircleProgress

class GridAdapter(private val context: Context) : BaseAdapter() {

    /**
     * show 8 different CircleProgress styles
     *
     * @return Int num of items
     */
    override fun getCount(): Int = 8

    override fun getItem(position: Int): Int = position

    override fun getItemId(position: Int): Long = 1L

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view = LayoutInflater.from(context).inflate(R.layout.cricle_item, parent, false)
        val progress: CircleProgress = view.findViewById(R.id.circle_progress)
        renderCircleProgress(progress, position)
        return view
    }

    private fun renderCircleProgress(circleProgress: CircleProgress, position: Int) {
        when (position) {
            1 -> {

            }
            2 -> {

            }
            3 -> {

            }
            4 -> {

            }
            5 -> {

            }
            6 -> {

            }
            7 -> {

            }
            else -> {
                //empty
            }
        }
    }

    fun startProgressAnimation() {
        val valueAnimator: ValueAnimator = ValueAnimator.ofInt(0, 100)
        valueAnimator.duration = 2000L
        valueAnimator.interpolator = LinearInterpolator()
        valueAnimator.addUpdateListener {
            val int = it.animatedValue as Int
            //TODO
        }
        valueAnimator.start()
    }

    fun stopProgressAnimation() {
        //TODO
    }

}