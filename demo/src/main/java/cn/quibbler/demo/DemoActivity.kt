package cn.quibbler.demo

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import cn.quibbler.demo.databinding.ActivityDemoBinding

class DemoActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDemoBinding
    private lateinit var adapter: GridAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDemoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initView()
    }

    private fun initView() {
        adapter = GridAdapter(this)
        binding.gridView.adapter = adapter
    }

    override fun onStart() {
        super.onStart()
        adapter.startProgressAnimation()
    }

    override fun onPause() {
        super.onPause()
        adapter.stopProgressAnimation()
    }

}