package com.example.nasaphotos.view.photolist

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.nasaphotos.util.Constant.ARG_ROVER_TYPE
import com.example.nasaphotos.util.Utils

class PhotoListViewPagerAdapter(fragmentManager: FragmentManager, lifecycle: Lifecycle) :
    FragmentStateAdapter(fragmentManager, lifecycle) {

    override fun getItemCount(): Int {
        return Utils.roverList.size
    }

    override fun createFragment(position: Int): Fragment {
        val photoListFragment = PhotoListFragment()
        photoListFragment.arguments = Bundle().apply {
            putString(ARG_ROVER_TYPE, Utils.roverList[position])
        }
             return photoListFragment
    }
}