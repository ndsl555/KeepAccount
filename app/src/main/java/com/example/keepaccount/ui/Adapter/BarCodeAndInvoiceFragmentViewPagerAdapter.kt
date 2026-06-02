package com.example.keepaccount.ui.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.keepaccount.BarCodeFragment
import com.example.keepaccount.LotteryCheckFragment

private const val NUM_TABS = 2

class BarCodeAndInvoiceFragmentViewPagerAdapter(fragmentManager: FragmentManager, lifecycle: Lifecycle) :
    FragmentStateAdapter(fragmentManager, lifecycle) {
    override fun getItemCount(): Int = NUM_TABS

    override fun createFragment(position: Int): Fragment {
        return if (position == 0) {
            BarCodeFragment()
        } else {
            LotteryCheckFragment()
        }
    }
}
