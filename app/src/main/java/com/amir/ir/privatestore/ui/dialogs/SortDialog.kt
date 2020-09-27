package com.amir.ir.privatestore.ui.dialogs

import android.content.Context
import android.view.View
import android.widget.CompoundButton
import android.widget.RadioButton
import android.widget.Switch
import androidx.appcompat.widget.SwitchCompat
import com.amir.ir.privatestore.R
import com.amir.ir.privatestore.models.enums.ListOrder
import com.amir.ir.privatestore.utils.exhaustive
import com.amir.ir.privatestore.utils.getRadioButtonsCheckChangedListener
import com.google.android.material.floatingactionbutton.FloatingActionButton

class SortDialog(
    context: Context,
    themeResId: Int,
    private var listOrder: ListOrder,
    private var isChecked: Boolean,
    private val onSubmit: (ListOrder, Boolean) -> Unit
) :
    MyBaseDialog(
        context, themeResId,
        R.layout.layout_sort_dialog
    ) {


    private var shouldShowNewestField = true

    private lateinit var radioNewest: RadioButton
    private lateinit var radioMostSell: RadioButton
    private lateinit var radioDsc: RadioButton
    private lateinit var radioAsc: RadioButton
    private lateinit var switchJustInStock: SwitchCompat
    private lateinit var frameNewest: View
    private lateinit var frameMostSell: View
    private lateinit var frameDsc: View
    private lateinit var frameAsc: View
    private lateinit var btnSubmit: FloatingActionButton


    override fun initViews() {
        initV()

        val map = mapOf<CompoundButton, (() -> Unit)>(
            Pair(radioAsc) {
                listOrder = ListOrder.PRICE_ASCENDING
            },
            Pair(radioDsc) {
                listOrder = ListOrder.PRICE_DESCENDING
            },
            Pair(radioMostSell) {
                listOrder = ListOrder.MOST_SELL
            },
            Pair(radioNewest) {
                listOrder = ListOrder.NEW
            }
        )

        val onCheckChangedListener = getRadioButtonsCheckChangedListener(map)
        //set listeners
        radioNewest.setOnCheckedChangeListener(onCheckChangedListener)
        radioAsc.setOnCheckedChangeListener(onCheckChangedListener)
        radioDsc.setOnCheckedChangeListener(onCheckChangedListener)
        radioMostSell.setOnCheckedChangeListener(onCheckChangedListener)

        frameAsc.setOnClickListener { radioAsc.isChecked = true }
        frameMostSell.setOnClickListener { radioMostSell.isChecked = true }
        frameNewest.setOnClickListener { radioNewest.isChecked = true }
        frameDsc.setOnClickListener { radioDsc.isChecked = true }

        switchJustInStock.setOnCheckedChangeListener { _, isChecked ->
            this.isChecked = isChecked
        }

        btnSubmit.setOnClickListener {
            onSubmit(listOrder, isChecked)
            dismiss()
        }
    }


    fun initV() {
        //init views
        radioNewest = findViewById(R.id.radioNewest)
        radioMostSell = findViewById(R.id.radioMostSell)
        radioDsc = findViewById(R.id.radioDscPrice)
        radioAsc = findViewById(R.id.radioAscPrice)
        switchJustInStock = findViewById(R.id.switch2)
        frameNewest = findViewById(R.id.frame_newest)
        frameMostSell = findViewById(R.id.frame_most_sell)
        frameDsc = findViewById(R.id.frame_price_dsc)
        frameAsc = findViewById(R.id.frame_price_asc)
        btnSubmit = findViewById(R.id.btnSubmit)
        //def values
        switchJustInStock.isChecked = isChecked
        when (listOrder) {
            ListOrder.UN_DEFINED -> throw Throwable("can not be undefined")
            ListOrder.PRICE_ASCENDING -> radioAsc.isChecked = true
            ListOrder.PRICE_DESCENDING -> radioDsc.isChecked = true
            ListOrder.NEW -> radioNewest.isChecked = true
            ListOrder.MOST_SELL -> radioMostSell.isChecked = true
        }.exhaustive()

        if (!shouldShowNewestField) {
            frameMostSell.visibility = View.GONE
            frameNewest.visibility = View.GONE
        }
    }


    fun donNotShowNewestField() {
        shouldShowNewestField = false
    }

}

