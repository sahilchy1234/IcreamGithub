package com.roadpass.icecreamroll.activity.onboardactivity

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.roadpass.icecreamroll.fragment.onboardfragment.OnBoard3Fragment


class OnBoardFragmentAdapter(fragmentManager: FragmentActivity) : FragmentStateAdapter(fragmentManager) {
    private val arrayList = listOf<Fragment>(
//        OnBoardFragment(),
//        OnBoard2Fragment(),
        OnBoard3Fragment()
    )
    override fun getItemCount(): Int {
        return arrayList.size
    }

    override fun createFragment(position: Int): Fragment {
        return arrayList[position]
    }
}