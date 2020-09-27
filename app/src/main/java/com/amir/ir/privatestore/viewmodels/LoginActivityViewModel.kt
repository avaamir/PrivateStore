package com.amir.ir.privatestore.viewmodels

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.ViewModel
import com.amir.ir.privatestore.R
import java.util.*

class LoginActivityViewModel : ViewModel() {

    private val stack: Stack<Fragment> = Stack()


    fun addFragmentToStack(fm: FragmentManager, fragment: Fragment) {
        if (stack.size > 0) {
            fm.beginTransaction()
                .hide(stack.peek())
                .add(R.id.frame_fragment_container, fragment)
                .commit()
            stack.push(fragment)
        } else {
            fm.beginTransaction()
                .add(R.id.frame_fragment_container, fragment)
                .commit()
            stack.push(fragment)
        }
    }

    fun backPressed(fm: FragmentManager) {
        fm.beginTransaction()
            .remove(stack.pop())
            .show(stack.peek())
            .commit()
    }
}