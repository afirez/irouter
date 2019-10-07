package com.afirez.irouter.app

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.afirez.irouter.IRouter
import com.afirez.irouter.activity.api.ActivityRouter
import com.afirez.irouter.activity.api.User
import com.afirez.irouter.applike.api.AppLikeRouter
import com.afirez.irouter.provider.api.ProviderRouter
import io.reactivex.android.schedulers.AndroidSchedulers
import kotlinx.android.synthetic.main.activity_main.*

/**
 * https://github.com/afirez/irouter
 *
 * irouter usecases
 */
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // 0、IRouter Init
        tv00Init.setOnClickListener {
            Toast.makeText(applicationContext, "@See com.afirez.irouter.app.IRouterApp and gradle config", Toast.LENGTH_LONG).show()
        }

        // 1、Activity without Arg
        tv01Activity.setOnClickListener {
            IRouter.with(ActivityRouter::class.java).navNavActivity()
        }

        // 2、Activity with Arg
        tv02Activity.setOnClickListener {
            val users = arrayListOf<User>().apply {
                add(User("Hello"))
                add(User("Afirez"))
            }
            IRouter.with(ActivityRouter::class.java)
                .navNavActivity(User("afirez"), users, arrayOf("https://github.com/afirez/irouter"))
        }

        // 3、Activity for Result
        tv03Activity.setOnClickListener {
            IRouter.with(ActivityRouter::class.java).navNavActivityForResult()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ result ->
                    Toast.makeText(applicationContext, "Activity Result: $result", Toast.LENGTH_SHORT).show()
                }, {

                }, {

                })
        }

        // 4、Activity by path
        tv04Activity.setOnClickListener {
            IRouter.with(ActivityRouter::class.java).navActivityByPath("/irouter/activity/nav")
        }

        // 5、Fragment
        tv05Fragment.setOnClickListener {
            startActivity(Intent(this@MainActivity, NavFragmentActivity::class.java))
        }

        // 6、Provider
        tv06Provider.setOnClickListener {
            val msg = IRouter.with(ProviderRouter::class.java).navProvider().provide() ?: ""
            Toast.makeText(applicationContext, msg, Toast.LENGTH_SHORT).show()
        }

        // 7、Interceptor
        tv07Interceptor.setOnClickListener {
            Toast.makeText(
                applicationContext,
                "@See com.afirez.irouter.interceptor.IRouterInterceptor",
                Toast.LENGTH_SHORT
            ).show()
            IRouter.with(ActivityRouter::class.java).navActivityByPath("/irouter/activity/nav/fragment")

        }

        // 8、AppLike
        tv08AppLike.setOnClickListener {
            Toast.makeText(applicationContext, "@See com.afirez.irouter.applike.IRouterAppLike", Toast.LENGTH_SHORT)
                .show()
            val navAppLikeProvider = IRouter.with(AppLikeRouter::class.java).navAppLikeProvider()
            val msg = navAppLikeProvider?.isInit() ?: false
            Toast.makeText(applicationContext, "AppLike is Init: $msg", Toast.LENGTH_SHORT).show()
        }

    }
}
