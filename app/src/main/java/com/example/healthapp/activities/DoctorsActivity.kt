package com.example.healthapp.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.healthapp.R
import com.example.healthapp.databinding.ActivityDoctorsBinding

class DoctorsActivity : AppCompatActivity() {

    private var binding:ActivityDoctorsBinding?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityDoctorsBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        setupActionBar()

    }


    private fun setupActionBar() {

        setSupportActionBar(binding?.toolbarBackDocActivity)

        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_black_color_back_24dp)
        }

        binding?.toolbarBackDocActivity?.setNavigationOnClickListener{
            onBackPressed()
        }

    }
}