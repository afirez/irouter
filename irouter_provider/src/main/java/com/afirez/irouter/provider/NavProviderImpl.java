package com.afirez.irouter.provider;

import com.afirez.irouter.provider.api.NavProvider;
import com.afirez.spi.SPI;

@SPI(path = "/irouter/provider/nav")
public class NavProviderImpl implements NavProvider {
    @Override
    public String provide() {
        return "IRouter Provider";
    }
}
