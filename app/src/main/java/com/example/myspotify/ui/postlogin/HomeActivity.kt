package com.example.myspotify.ui.postlogin

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.myspotify.R
import com.example.myspotify.databinding.ActivityHomeBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeActivity : AppCompatActivity() {

    private var _binding: ActivityHomeBinding? = null
    val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        _binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()

        binding.activityHomeBottomNavigationView.setupWithNavController((supportFragmentManager.findFragmentById(R.id.activity_home_nav_host_fragment) as NavHostFragment).navController)

    }

}