package com.amir.ir.privatestore.ui.listeners

import com.amir.ir.privatestore.models.Product

interface Interaction<T> {
    fun onItemClicked(item: T)
}

interface ProductInteraction: Interaction<Product> {
    fun onListTop()
    fun onListEnd()
}