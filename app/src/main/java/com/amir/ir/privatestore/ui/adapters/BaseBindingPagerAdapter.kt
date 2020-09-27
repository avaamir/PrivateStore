package com.amir.ir.privatestore.ui.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.viewpager.widget.PagerAdapter

abstract class BaseBindingPagerAdapter<T, B: ViewDataBinding>(context: Context, private val layoutId: Int, private val slides: List<T>) :
    PagerAdapter() {
    private var inflater: LayoutInflater = LayoutInflater.from(context)

    override fun isViewFromObject(view: View, obj: Any): Boolean {
        return view == obj
    }

    override fun getCount(): Int {
        return slides.size
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {

        val binding = DataBindingUtil.inflate<B>(
            inflater,
            layoutId,
            container,
            false
        )

        initView(binding, position)

        container.addView(binding.root)
        return binding.root
    }

    override fun destroyItem(container: ViewGroup, position: Int, obj: Any) {
        container.removeView(obj as View)
    }

    abstract fun initView(binding: B, position: Int)
}