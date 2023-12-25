package com.example.healthapp.activities

import android.app.Activity
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import android.os.Bundle
import com.example.healthapp.dataSource.TaskDataSource
import com.example.healthapp.databinding.ActivityAddTaskBinding
import com.example.healthapp.extensions.format
import com.example.healthapp.extensions.text
import com.example.healthapp.models.Task
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import java.util.*

class AddTaskActivity : BaseActivity() {

    private  var binding: ActivityAddTaskBinding?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityAddTaskBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        if (intent.hasExtra(TASK_ID)) {
            val taskId = intent.getIntExtra(TASK_ID, 0)
            TaskDataSource.findById(taskId)?.let {
                binding?.tilTitle?.text = it.title
                binding?.tilDate?.text = it.date
                binding?.tilTimer?.text = it.hour
            }
        }

        insertListeners()
    }

    private fun insertListeners() {
        binding?.tilDate?.editText?.setOnClickListener {
            val datePicker = MaterialDatePicker.Builder.datePicker().build()
            datePicker.addOnPositiveButtonClickListener {
                val timeZone = TimeZone.getDefault()
                val offset = timeZone.getOffset(Date().time) * -1
                binding?.tilDate?.text = Date(it + offset).format()
            }
            datePicker.show(supportFragmentManager, "DATE_PICKER_TAG")

        }
        binding?.tilTimer?.editText?.setOnClickListener {
            val timerPiker =
                MaterialTimePicker.Builder().setTimeFormat(TimeFormat.CLOCK_24H).build()
            timerPiker.addOnPositiveButtonClickListener {
                val minute =
                    if (timerPiker.minute in 0..9) "0${timerPiker.minute}" else timerPiker.minute
                val hour = if (timerPiker.hour in 0..9) "0${timerPiker.hour}" else timerPiker.hour

                binding?.tilTimer?.text = "${hour}:${minute}"
            }

            timerPiker.show(supportFragmentManager, null)
        }

        binding?.buttonCancel?.setOnClickListener {
            finish()
        }

        binding?.buttonNewTask?.setOnClickListener {
            val task = binding?.tilTitle?.text?.let { it1 ->
                binding?.tilDate?.text?.let { it2 ->
                    binding?.tilTimer?.text?.let { it3 ->
                        Task(
                            title = it1,
                            hour = it3,
                            date = it2,
                            id = intent.getIntExtra(TASK_ID, 0)
                        )
                    }
                }
            }
            if (task != null) {
                TaskDataSource.insertTask(task)
            }

            setResult(Activity.RESULT_OK)
            finish()
        }
    }

    companion object {
        const val TASK_ID = "task_id"
    }


}