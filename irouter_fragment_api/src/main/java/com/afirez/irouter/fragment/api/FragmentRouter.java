package com.afirez.irouter.fragment.api;

import androidx.fragment.app.Fragment;
import com.afirez.irouter.router.Path;
import com.afirez.irouter.router.Query;

public interface FragmentRouter {
    @Path("/irouter/fragment/nav")
    Fragment navFragment(@Query("whoami") String whoami);
}
