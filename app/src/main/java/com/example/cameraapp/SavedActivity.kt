package com.example.cameraapp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback
import com.example.cameraapp.adapter.ViewPagerAdapter
import com.example.cameraapp.databinding.ActivitySavedBinding
import com.google.android.material.tabs.TabLayoutMediator


class SavedActivity : AppCompatActivity() {
    lateinit var binding: ActivitySavedBinding

    private val item= arrayOf("Image","Video")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivitySavedBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val adapter = ViewPagerAdapter(supportFragmentManager, lifecycle)
        binding.vpTypeContent.adapter= adapter
        binding.vpTypeContent.registerOnPageChangeCallback(object : OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                    adapter.notifyItemChanged(position)
            }
        })
        TabLayoutMediator(binding.tlType, binding.vpTypeContent) { tab, position ->
            tab.text = item[position]
        }.attach()
    }
}