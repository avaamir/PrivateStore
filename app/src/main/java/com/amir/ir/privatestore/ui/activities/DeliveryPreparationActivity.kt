package com.amir.ir.privatestore.ui.activities

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.amir.ir.privatestore.R
import com.amir.ir.privatestore.utils.Constants
import kotlinx.android.synthetic.main.activity_delivery_preparation.*

class DeliveryPreparationActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_delivery_preparation)

        initViews()
    }

    private fun initViews() {
        iv_back.setOnClickListener { finish() }
        btnMain.setOnClickListener { finish() }
        btnOrder.setOnClickListener {
            startActivity(Intent(this, ProfileActivity::class.java).apply {
                putExtra(Constants.INTENT_PROFILE_ACTIVITY_PAGE,1)
            })
            finish()
        }
        buttonCall1.setOnClickListener {
            val callIntent = Intent(Intent.ACTION_DIAL)
            callIntent.data = Uri.parse("tel:09035232486")
            startActivity(callIntent)
        }
        buttonCall2.setOnClickListener {
            val callIntent = Intent(Intent.ACTION_DIAL)
            callIntent.data = Uri.parse("tel:09362163813")
            startActivity(callIntent)
        }
        //bounce(ivLogo, 2000)


    }
}
