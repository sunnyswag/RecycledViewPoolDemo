package com.example.recycledviewpooldemo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.recycledviewpooldemo.adapter.ViewPagerAdapter
import com.example.recycledviewpooldemo.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.vpTest.adapter = ViewPagerAdapter(this)
    }
}