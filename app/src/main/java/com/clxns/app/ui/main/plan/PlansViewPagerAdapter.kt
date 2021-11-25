package com.clxns.app.ui.main.plan

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.clxns.app.ui.main.plan.mapview.MapFragment
import com.clxns.app.ui.main.plan.plannedLeads.MyPlanFragment

class PlansViewPagerAdapter(fragmentActivity : FragmentActivity) :
    FragmentStateAdapter(fragmentActivity) {


    override fun getItemCount() : Int {
        return 2
    }

    override fun createFragment(position : Int) : Fragment {

        var fragment = Fragment()

        when (position) {
            0 -> fragment = MyPlanFragment()
            1 -> fragment = MapFragment()
        }

        return fragment

    }

}