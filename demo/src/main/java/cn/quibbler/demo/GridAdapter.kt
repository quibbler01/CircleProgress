package cn.quibbler.demo

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
        //TODO("difference circle progress")
    }

}