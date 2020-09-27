package com.amir.ir.privatestore.ui.listeners

import android.app.Activity
import android.content.Intent
import com.amir.ir.privatestore.models.Category
import com.amir.ir.privatestore.ui.activities.CategoryActivity
import com.amir.ir.privatestore.utils.Constants

class CategoryAdapterInteractionImpl(private val activity: Activity): Interaction<Category> {


    override fun onItemClicked(item: Category) {
        val intent = Intent(activity, CategoryActivity::class.java)
        intent.putExtra(Constants.INTENT_CATEGORY_ACTIVITY_CATEGORY, item)
        activity.startActivity(intent)
    }
}