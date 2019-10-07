package com.afirez.irouter.app

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.afirez.irouter.IRouter
import com.afirez.irouter.fragment.api.FragmentRouter
import com.afirez.spi.SPI

/**
 * load fragment form module irouter_fragment
 */
@SPI(path = "/irouter/activity/nav/fragment")
class NavFragmentActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_nav_fragment)

        val tag = "fragmentTag"
        var fragment = supportFragmentManager.findFragmentByTag(tag)
        if (fragment == null) {
            fragment = IRouter.with(FragmentRouter::class.java).navFragment("IRouter Fragment\n https://github.com/afirez/irouter")
            if (fragment != null) {
                val transaction = supportFragmentManager.beginTransaction()
                transaction.replace(R.id.flContainer, fragment, tag)
                transaction.commitAllowingStateLoss()
            }
        }
    }
}
