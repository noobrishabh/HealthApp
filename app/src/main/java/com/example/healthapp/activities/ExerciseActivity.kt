package com.example.healthapp.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.healthapp.R
import com.example.healthapp.databinding.ActivityExerciseBinding

class ExerciseActivity : BaseActivity() {
    private var binding:ActivityExerciseBinding?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityExerciseBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        //val flStartButton:FrameLayout=findViewById(R.id.flStart)
        binding?.flStart?.setOnClickListener{

            val intent= Intent(this@ExerciseActivity,WorkoutActivity::class.java)
            startActivity(intent)


        }

        binding?.flBmi?.setOnClickListener{

            val intent= Intent(this@ExerciseActivity,BMIActivity::class.java)
            startActivity(intent)


        }

        setupActionBar()
    }

    private fun setupActionBar() {

        setSupportActionBar(binding?.toolbarBackActivity)

        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_black_color_back_24dp)
        }

        binding?.toolbarBackActivity?.setNavigationOnClickListener{
            onBackPressed()
        }

    }

    override fun onDestroy() {
        super.onDestroy()

        binding=null
    }
}