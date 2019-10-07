package com.afirez.irouter.applike.api;

import com.afirez.irouter.router.Path;

public interface AppLikeRouter {
    @Path("irouter/applike/provider")
    AppLikeProvider navAppLikeProvider();
}
