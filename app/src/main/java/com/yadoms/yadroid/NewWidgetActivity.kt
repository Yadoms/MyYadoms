package com.yadoms.yadroid

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import com.yadoms.yadroid.databinding.ActivityNewWidgetBinding
import com.yadoms.yadroid.databinding.ActivityScrollingBinding

class NewWidgetActivity : AppCompatActivity() {

    private lateinit var binding: ActivityNewWidgetBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityNewWidgetBinding.inflate(layoutInflater)
        val view = binding.root

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController

        setContentView(view)
    }
}