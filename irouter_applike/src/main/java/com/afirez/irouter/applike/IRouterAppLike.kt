package com.afirez.irouter.applike

import android.app.Application
import android.content.Context
import android.util.Log
import com.afirez.applike.AppLike
import com.afirez.irouter.IRouter
import com.afirez.irouter.applike.api.AppLikeRouter
import com.afirez.spi.SPI

@SPI
class IRouterAppLike : AppLike {
    override fun attachBaseContext(app: Application, base: Context?) {
        Log.w("AppLike", "attachBaseContext")
    }

    override fun onCreate(app: Application) {
        Log.w("AppLike", "onCreate")
        val navAppLikeProvider = IRouter.with(AppLikeRouter::class.java).navAppLikeProvider()
        if (navAppLikeProvider != null) {
            navAppLikeProvider.isInit = true
        }
    }

    override fun onTerminate(app: Application) {
        Log.w("AppLike", "onTerminate")
    }
}