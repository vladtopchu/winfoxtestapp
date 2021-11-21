package ru.topchu.winfoxtestapp.presentation.auth

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

class AuthViewPagerAdapter(activity: FragmentActivity) : FragmentStateAdapter(activity) {

    lateinit var fragments: List<Fragment>

    override fun getItemCount(): Int = fragments.size

    override fun createFragment(position: Int) = fragments[position]
}