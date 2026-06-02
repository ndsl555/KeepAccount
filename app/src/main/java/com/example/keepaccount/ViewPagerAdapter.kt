package com.example.keepaccount

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter

class ViewPagerAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {
    override fun getItemCount(): Int = 2

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> LotteryManualCheckFragment()
            1 -> LotteryQRScanFragment()
            else -> throw IllegalStateException("Invalid position: $position")
        }
    }
}
