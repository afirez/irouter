package com.afirez.irouter.app;

import com.afirez.spi.SPI;

@SPI(path = "/irouter/service/nav")
public class NavServiceImpl implements NavService {
    @Override
    public String service() {
        return "afirez IRouter Service";
    }
}
