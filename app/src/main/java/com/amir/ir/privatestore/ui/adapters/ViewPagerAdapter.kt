package com.amir.ir.privatestore.ui.adapters

import android.content.Context
import com.amir.ir.privatestore.R
import com.amir.ir.privatestore.databinding.LayoutSlideBinding
import com.amir.ir.privatestore.models.Slide

class ViewPagerAdapter(
    context: Context,
    private val slides: List<Slide>,
    private val interactions: SliderInteractions? = null
) : BaseBindingPagerAdapter<Slide, LayoutSlideBinding>(context, R.layout.layout_slide, slides) {

    override fun initView(binding: LayoutSlideBinding, position: Int) {
        binding.slide = slides[position]
        binding.root.setOnClickListener {
            interactions?.onItemClicked(slides[position], position)
        }
    }

    interface SliderInteractions {
        fun onItemClicked(slide: Slide, position: Int)
    }

}