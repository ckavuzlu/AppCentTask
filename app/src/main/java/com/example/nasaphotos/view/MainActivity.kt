package com.example.nasaphotos.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.nasaphotos.databinding.ActivityMainBinding
import com.example.nasaphotos.util.Utils
import com.example.nasaphotos.view.photolist.PhotoListViewPagerAdapter
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    lateinit var mainActivityBinding : ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainActivityBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(mainActivityBinding.root)

        val adapter = PhotoListViewPagerAdapter(supportFragmentManager, lifecycle)
        mainActivityBinding.viewPagerMain.adapter = adapter

        TabLayoutMediator(mainActivityBinding.tabLayoutMain , mainActivityBinding.viewPagerMain) { tab, position ->
            tab.text = Utils.roverList[position]
        }.attach()
    }



}