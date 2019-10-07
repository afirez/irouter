package com.afirez.irouter.fragment


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.afirez.spi.SPI


/**
 * https://github.com/afirez/irouter
 */
@SPI(path = "/irouter/fragment/nav")
class NavFragment : Fragment() {

    var whoami = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        whoami = arguments?.getString("whoami", "https://github.com/afirez/irouter")
            ?: "https://github.com/afirez/irouter"
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_nav, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.findViewById<TextView>(R.id.tvMsg).text = whoami
    }
}
