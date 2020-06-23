package com.burakkarakoca.weatherapp.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.fragment.app.FragmentStatePagerAdapter

@Suppress("DEPRECATION")
class SlidePagerAdapter(manager: FragmentManager) : FragmentStatePagerAdapter(manager){

    private val fragmentList : MutableList<Fragment> = ArrayList()
    private val titleList : MutableList<String> = ArrayList()

    override fun getItem(position: Int): Fragment {
        return fragmentList[position]
    }

    override fun getCount(): Int {
        return fragmentList.size
    }

    fun addFragment(fragment: Fragment){
        fragmentList.add(fragment)
    }

    fun removeFragment(fragment: Fragment){
        fragmentList.remove(fragment)
    }


    override fun getPageTitle(position: Int): CharSequence? {
        return titleList[position]
    }

}