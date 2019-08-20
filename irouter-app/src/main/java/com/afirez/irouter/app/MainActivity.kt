package com.afirez.irouter.app

import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.afirez.irouter.IRouter

/**
 * https://github.com/afirez/irouter
 */
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        findViewById<TextView>(R.id.tvH).setOnClickListener {
            //            IRouter.with(RouterApi::class.java).navNavActivity()
//            IRouter.with(RouterApi::class.java).navActivity("/irouter/activity/nav")

//            val users = arrayListOf<User>().apply {
//                add(User("Hello"))
//                add(User("Afirez"))
//            }
//
//            IRouter.with(RouterApi::class.java).navNavActivity(User("afirez"), users, arrayOf("https://github.com/afirez/irouter"))

            val msg = IRouter.with(RouterApi::class.java).navService().service() ?: ""
            Toast.makeText(applicationContext, msg, Toast.LENGTH_SHORT).show()

//            IRouter.with(RouterApi::class.java).navNavFragmentActivity()
            IRouter.with(RouterApi::class.java).navNavFragmentActivity2()
                .subscribe({

                }, {

                }, {

                })
        }
    }
}
