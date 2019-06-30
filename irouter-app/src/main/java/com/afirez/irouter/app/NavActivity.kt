package com.afirez.irouter.app

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.afirez.spi.SPI

@SPI(path = "/irouter/activity/nav")
class NavActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_second)
        val user = intent.getParcelableExtra<User>("user")
        val users = intent.getParcelableArrayListExtra<User>("users")
        val tags = intent.getStringArrayExtra("tags")

        Log.w("IRouter", "" + user)
        Log.w("IRouter", "" + users)
        Log.w("IRouter", "" + tags)
    }
}
