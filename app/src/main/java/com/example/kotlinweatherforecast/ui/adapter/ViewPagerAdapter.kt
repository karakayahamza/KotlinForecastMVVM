package com.example.kotlinweatherforecast.ui.adapter

import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.viewpager.widget.PagerAdapter

class ViewPagerAdapter(fragmentManager: FragmentManager, FRAGMENT_TAG_ARG: String) : PagerAdapter() {
    private val pages = ArrayList<Fragment>()
    private val fragmentsPosition: HashMap<Fragment, Int> = HashMap()
    private var currentPrimaryItem: Fragment? = null
    private var fragmentManager: FragmentManager? = fragmentManager
    private var currentTransaction: FragmentTransaction? = null
    private var fragmentTagArg : String? = FRAGMENT_TAG_ARG


    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        if (currentTransaction == null) {
            currentTransaction = fragmentManager!!.beginTransaction()
        }
        val pageFragment: Fragment = pages[position]
        val tag = pageFragment.requireArguments().getString(fragmentTagArg)
        var fragment = fragmentManager!!.findFragmentByTag(tag)
        if (fragment != null) {
            if (fragment.id == container.id) {
                currentTransaction!!.attach(fragment)
            } else {
                fragmentManager!!.beginTransaction().remove(fragment).commit()
                fragmentManager!!.executePendingTransactions()
                currentTransaction!!.add(container.id, fragment, tag)
            }
        } else {
            fragment = pageFragment
            currentTransaction!!.add(container.id, fragment, tag)
        }
        if (fragment !== currentPrimaryItem) {
            fragment.setMenuVisibility(false)
            fragment.userVisibleHint = false
        }
        return fragment
    }

    override fun getCount(): Int {
        return pages.size
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        if (currentTransaction == null) {
            currentTransaction = fragmentManager?.beginTransaction()
        }
        val fragment = `object` as Fragment
        currentTransaction?.remove(fragment)
    }


    override fun setPrimaryItem(container: ViewGroup, position: Int, `object`: Any) {
        val fragment = `object` as Fragment
        if (fragment !== currentPrimaryItem) {
            if (currentPrimaryItem != null) {
                currentPrimaryItem!!.setMenuVisibility(false)
                currentPrimaryItem!!.userVisibleHint = false
            }
            fragment.setMenuVisibility(true)
            fragment.userVisibleHint = true
            currentPrimaryItem = fragment
        }
    }

   override fun finishUpdate(container: ViewGroup) {
        if (currentTransaction != null) {
            currentTransaction!!.commitAllowingStateLoss()
            currentTransaction = null
            fragmentManager!!.executePendingTransactions()
        }
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return (`object` as Fragment).view === view
    }

    override fun getItemPosition(`object`: Any): Int {
        val index = pages.indexOf(`object`)
        return if (index == -1) POSITION_NONE else index

    }

    // ---------------------------------- Page actions ----------------------------------
    fun addPage(fragment: Fragment?) {
        //fragmentsPosition
        if (fragment != null) {
            pages.add(fragment)
        }
        notifyDataSetChanged()
    }

    fun removePage(position: Int) {
        fragmentsPosition.clear()
        var pageFragment: Fragment = pages[position]
        var tag = pageFragment.requireArguments().getString(fragmentTagArg)
        var fragment = fragmentManager!!.findFragmentByTag(tag)
        if (fragment != null) {
            fragmentsPosition[fragment] = POSITION_NONE
        }
        for (i in position + 1 until pages.size) {
            pageFragment = pages[i]
            tag = pageFragment.requireArguments().getString(fragmentTagArg)
            fragment = fragmentManager!!.findFragmentByTag(tag)
            if (fragment != null) {
                fragmentsPosition[fragment] = i - 1
            }
        }
        pages.removeAt(position)
        notifyDataSetChanged()
    }
}