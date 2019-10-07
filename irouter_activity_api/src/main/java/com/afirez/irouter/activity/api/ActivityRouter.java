package com.afirez.irouter.activity.api;

import com.afirez.irouter.Result;
import com.afirez.irouter.router.Path;
import com.afirez.irouter.router.Query;
import io.reactivex.Observable;

import java.util.ArrayList;

public interface ActivityRouter {

    @Path("/irouter/activity/nav")
    void navNavActivity();

    @Path("/irouter/activity/nav")
    void navNavActivity(@Query("user") User user, @Query("users") ArrayList<User> users, @Query("tags") String[] tags);

    @Path("/irouter/activity/nav")
    Observable<Result> navNavActivityForResult();

    @Path("{url}")
    void navActivityByPath(@Path("url") String url);
}
