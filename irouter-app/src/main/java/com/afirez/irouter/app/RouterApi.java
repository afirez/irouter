package com.afirez.irouter.app;

import androidx.fragment.app.Fragment;
import com.afirez.irouter.router.Path;
import com.afirez.irouter.router.Query;

import java.util.ArrayList;

public interface RouterApi {

    @Path("{url}")
    void navActivity(@Path("url") String url);

    @Path("/irouter/activity/nav")
    void navNavActivity();

    @Path("/irouter/activity/nav")
    void navNavActivity(@Query("user") User user, @Query("users") ArrayList<User> users, @Query("tags") String[] tags);

    @Path("/irouter/service/nav")
    NavService navService();

    @Path("/irouter/activity/nav/fragment")
    void navNavFragmentActivity();

    @Path("/irouter/fragment/nav")
    Fragment navFragment(@Query("whoami") String whoami);
}
