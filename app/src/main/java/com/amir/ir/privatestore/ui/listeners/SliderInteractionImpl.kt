package com.amir.ir.privatestore.ui.listeners

import android.app.Activity
import android.content.Intent
import android.net.Uri
import com.amir.ir.privatestore.models.Slide
import com.amir.ir.privatestore.models.enums.SlideType
import com.amir.ir.privatestore.ui.activities.CategoryActivity
import com.amir.ir.privatestore.ui.activities.ProductDetailsActivity
import com.amir.ir.privatestore.ui.adapters.SliderAdapter
import com.amir.ir.privatestore.ui.adapters.ViewPagerAdapter
import com.amir.ir.privatestore.utils.Constants
import com.amir.ir.privatestore.utils.exhaustive


class SliderInteractionImpl(private val activity: Activity) : SliderAdapter.SliderInteractions {
    override fun onItemClicked(slide: Slide) {
        when (slide.type) {
            SlideType.NOT_DEFINED -> {
                val intent = Intent(Intent.ACTION_VIEW)
                intent.data = Uri.parse(slide.link)
                activity.startActivity(intent)
                println("debug: SliderInteractionImpl: NOT_Defined Slide Type -> update app")
            }
            SlideType.PRODUCT -> {
                val intent = Intent(activity, ProductDetailsActivity::class.java)
                intent.putExtra(Constants.INTENT_PRODUCT_DETAILS_ACTIVITY_PID, slide.targetId)
                activity.startActivity(intent)
            }
            SlideType.CATEGORY -> {
                val intent = Intent(activity, CategoryActivity::class.java)
                intent.putExtra(Constants.INTENT_CATEGORY_ACTIVITY_CATEGORY, slide.targetId)
                activity.startActivity(intent)
            }
        }.exhaustive()
    }

}