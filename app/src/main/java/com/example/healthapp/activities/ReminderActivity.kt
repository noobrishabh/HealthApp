package com.example.healthapp.activities

import android.app.Activity
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import com.example.healthapp.R
import com.example.healthapp.databinding.ActivityReminderBinding
import com.example.healthapp.dataSource.TaskDataSource

class ReminderActivity : BaseActivity() {
    private  var binding: ActivityReminderBinding?=null
    private val adapter by lazy { TaskListAdapter() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityReminderBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        binding?.recyclerTask?.adapter = adapter
        upadteList()

        insertListeners()

        setupActionBar()

    }

    private fun insertListeners() {
        binding?.fab?.setOnClickListener {
            startActivityForResult(Intent(this, AddTaskActivity::class.java), CREATE_NEW_TASK)
        }
        adapter.listenerEdit = {
            val intent = Intent(this, AddTaskActivity::class.java)
            intent.putExtra(AddTaskActivity.TASK_ID, it.id)
            startActivityForResult(intent, CREATE_NEW_TASK)
        }
        adapter.listenerDelete = {
            TaskDataSource.deleteTask(it)
            upadteList()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CREATE_NEW_TASK && resultCode == Activity.RESULT_OK) upadteList()

    }

    private fun upadteList() {
        val list = TaskDataSource.getList()
        if (list.isEmpty()) {
            binding?.emptyInclude?.stateEmptyCs?.visibility = View.VISIBLE
        } else {
            binding?.emptyInclude?.stateEmptyCs?.visibility = View.GONE
        }
        adapter.submitList(list)
    }

    companion object {
        private const val CREATE_NEW_TASK = 1000
    }

    private fun setupActionBar() {

        setSupportActionBar(binding?.toolbarBackRemActivity)

        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_black_color_back_24dp)
        }

        binding?.toolbarBackRemActivity?.setNavigationOnClickListener{
            onBackPressed()
        }

    }



}