package com.amir.ir.privatestore.ui.activities

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.amir.ir.privatestore.R
import kotlinx.android.synthetic.main.activity_about_us.*

class AboutUsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about_us)
        initViews();
    }


    private fun initViews() {
        btnWebsite.setOnClickListener {
            val browserIntent =
                Intent(Intent.ACTION_VIEW, Uri.parse(resources.getString(R.string.website_url)))
            browserIntent.flags = Intent.FLAG_ACTIVITY_NO_HISTORY
            startActivity(browserIntent)
        }

        btnBugReport.setOnClickListener {
            val browserIntent =
                Intent(Intent.ACTION_VIEW, Uri.parse(resources.getString(R.string.website_contact_us_url)))
            browserIntent.flags = Intent.FLAG_ACTIVITY_NO_HISTORY
            startActivity(browserIntent)
        }

        btnCallUs.setOnClickListener {
            val callIntent = Intent(Intent.ACTION_DIAL)
            callIntent.data = Uri.parse("tel:09362163813")
            startActivity(callIntent)
        }


        ivClose.setOnClickListener { finish() }
    }
}
