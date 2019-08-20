package com.afirez.irouter.app

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.afirez.irouter.IRouter
import com.afirez.spi.SPI


@SPI(path = "/irouter/activity/nav/fragment")
class NavFragmentActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_nav_fragment)

        val tag = "fragmentTag"
        var fragment = supportFragmentManager.findFragmentByTag(tag)
        if (fragment == null) {
            fragment = IRouter.with(RouterApi::class.java).navFragment("https://github.com/afirez/irouter")
            if (fragment != null) {
                val transaction = supportFragmentManager.beginTransaction()
//            if (fragment.isAdded) {
//                transaction.show(fragment)
//            } else {
//                transaction.add(R.id.flContainer, fragment, tag)
//            }
                transaction.replace(R.id.flContainer, fragment, tag)
                transaction.commitAllowingStateLoss()
            }
        }
//        if (fragment != null) {
//            val transaction = supportFragmentManager.beginTransaction()
//            if (fragment.isAdded) {
//                transaction.show(fragment)
//            } else {
//                transaction.add(R.id.flContainer, fragment, tag)
//            }
//            transaction.commitAllowingStateLoss()
//        }
    }
}
