package com.amir.ir.privatestore.ui.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.ViewModelProvider
import com.amir.ir.privatestore.R
import com.amir.ir.privatestore.repository.apiservice.ApiService
import com.amir.ir.privatestore.ui.dialogs.NoNetworkDialog
import com.amir.ir.privatestore.ui.fragments.login.LoginFragment
import com.amir.ir.privatestore.viewmodels.LoginActivityViewModel
import java.util.*


class LoginActivity : AppCompatActivity(), ApiService.InternetConnectionListener {
    private lateinit var viewModel: LoginActivityViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        viewModel = ViewModelProvider(this).get(LoginActivityViewModel::class.java)

        Fm.setFragmentManager(supportFragmentManager)
        Fm.addFragmentToStack(LoginFragment())
    }

    override fun onBackPressed() {
        val count = supportFragmentManager.backStackEntryCount
        if (count == 1) {
            finish()
        } else {
            supportFragmentManager.popBackStack()
        }
    }

    override fun onInternetUnavailable() {
        NoNetworkDialog(this, R.style.my_alert_dialog).show()
    }

}


object Fm {
    private var fm: FragmentManager? = null

    fun setFragmentManager(fm: FragmentManager) {
        if (this.fm == null || this.fm != fm)
            this.fm = fm
    }

    fun addFragmentToStack(fragment: Fragment) {
        fm!!.beginTransaction()
            .add(R.id.frame_fragment_container, fragment)
            .addToBackStack(null)
            //.setCustomAnimations(R.anim.fragment_fade_enter, R.anim.fragment_fade_exit)
            .commit()
    }


}
