package com.example.shelfie_app.view.adapters

import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.lifecycle.Lifecycle
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.shelfie_app.view.*


class ProfileAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {

    override fun getItemCount(): Int {
        return 3
    }

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> UserBookHistoryListFragment()
            1 -> UserReviewListFragment()
            2 -> UserLoansListFragment()
            else -> throw IllegalArgumentException("Invalid position")
        }
    }
}
