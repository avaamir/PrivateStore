package com.amir.ir.privatestore.ui.listeners

import android.content.Context
import android.content.Intent
import com.amir.ir.privatestore.models.Product
import com.amir.ir.privatestore.ui.activities.ProductDetailsActivity
import com.amir.ir.privatestore.utils.Constants

class ProductAdapterInteractionSimpleImpl(private val activity: Context) : ProductInteraction {

    override fun onListTop() {
    }

    override fun onListEnd() {
    }

    override fun onItemClicked(item: Product) {
        val intent = Intent(activity, ProductDetailsActivity::class.java)
        intent.putExtra(Constants.INTENT_PRODUCT_DETAILS_ACTIVITY_PRODUCT, item)
        activity.startActivity(intent)
    }
}