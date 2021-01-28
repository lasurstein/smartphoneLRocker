package com.example.smartphonelrocker

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.smartphonelrocker.timer.TimerListAdapter
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.util.*;

class MainActivity : AppCompatActivity(), AddTimerDialog.NoticeDialogListener {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerview)
        val adapter = TimerListAdapter()
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        findViewById<FloatingActionButton>(R.id.btnShowDialog).setOnClickListener {
            val dialog = AddTimerDialog()
            dialog.show(supportFragmentManager, "AddTimerDialog")
        }
    }

    override fun onNumberPickerDialogPositiveClick(
        dialog: DialogFragment,
        selectedHour: Int,
        selectedMin: Int
    ) {
        Log.d("item", "%s:%s".format(selectedHour, selectedMin))
    }

    override fun onNumberPickerDialogNegativeClick(dialog: DialogFragment) {
        return
    }


    fun sendMessage(view: View) {

//        val editText = findViewById<EditText>(R.id.editText)
//        val message = editText.text.toString()
//
//        val editTime = findViewById<EditText>(R.id.editTime)
        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val calendar = Calendar.getInstance()
        val intent = Intent(this, MyReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT
        )
        calendar.add(Calendar.SECOND, 10)
        alarmManager.setExact(
            AlarmManager.RTC_WAKEUP,
            calendar.timeInMillis,
            pendingIntent
        )

//        val intent = Intent(this, DisplayMessageActivity::class.java).apply {
//            putExtra(EXTRA_MESSAGE, message)
//        }
//        startActivity(intent)
    }
}