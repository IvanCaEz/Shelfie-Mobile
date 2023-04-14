package com.example.shelfie_app.view.adapters

import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.lifecycle.Lifecycle
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.shelfie_app.view.PruebaFragment
import com.example.shelfie_app.view.ShelfFragment


class ProfileAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {

    override fun getItemCount(): Int {
        return 3 // return the number of tabs you want to display
    }

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> ShelfFragment()
            1 -> PruebaFragment()
            2 -> PruebaFragment()
            else -> throw IllegalArgumentException("Invalid position")
        }
    }
}
