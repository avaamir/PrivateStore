package com.amir.ir.privatestore

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.amir.ir.privatestore.ui.activities.ProductDetailsActivity
import com.amir.ir.privatestore.ui.activities.ProfileActivity
import com.amir.ir.privatestore.utils.Constants
import com.amir.ir.privatestore.utils.fakeMessages
import com.amir.ir.privatestore.utils.millisToTimeString
import com.amir.ir.privatestore.utils.putParcelableExtra
import kotlinx.android.synthetic.main.activity_test.*


class TestActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_delivery_preparation)
        initViews()
    }

    var time = 360L

    private fun initViews() {


        if(true)
            return
        btn_main.setOnClickListener {
            //imageView.setColorFilter(Color.argb(255, 255, 0, 255)); // White Tint
            //imageView.setImageResource(R.color.black)

        }
        btn_product.setOnClickListener {
            val intent = Intent(this, ProductDetailsActivity::class.java)
            intent.putExtra(Constants.INTENT_PRODUCT_DETAILS_ACTIVITY_PID, 7)
            startActivity(intent)
        }
        btn_profile.setOnClickListener {
            val intent = Intent(this, ProfileActivity::class.java)
            intent.putParcelableExtra(
                Constants.INTENT_PROFILE_ACTIVITY_NEW_MESSAGE,
                fakeMessages()[0]
            )
            startActivity(intent)
        }

        btn_test.setOnClickListener {
            val timeStr = millisToTimeString(time * 1000)
            btn_main.text = timeStr
            time *= 3
            val units = timeStr.split(":")
            when (units.size) {
                1 -> {
                    second.text = units[0]
                    min.text = "00"
                    hour.text = "00"
                }
                2 -> {
                    second.text = units[1]
                    min.text = units[0]
                    hour.text = "00"
                }
                3 -> {
                    second.text = units[2]
                    min.text = units[1]
                    hour.text = units[0]
                }
            }
        }


    }


}