package com.afirez.irouter.provider.api;

import com.afirez.irouter.router.Path;

public interface ProviderRouter {

    @Path("/irouter/provider/nav")
    NavProvider navProvider();
}
