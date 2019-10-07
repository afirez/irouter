package com.afirez.irouter.applike

import com.afirez.irouter.applike.api.AppLikeProvider
import com.afirez.spi.SPI

@SPI(path = "irouter/applike/provider")
class AppLikeProviderImpl :AppLikeProvider  {

    private var isInit = false

    override fun setInit(init: Boolean) {
        isInit = init
    }

    override fun isInit(): Boolean {
        return isInit
    }
}