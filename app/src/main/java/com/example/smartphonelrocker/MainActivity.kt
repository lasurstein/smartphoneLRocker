package com.example.smartphonelrocker

import android.app.AlarmManager
import android.app.AlarmManager.AlarmClockInfo
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.observe
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.smartphonelrocker.timer.*
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.util.*
import kotlin.coroutines.CoroutineContext


class MainActivity : AppCompatActivity(), AddTimerDialog.NoticeDialogListener {

    private val timerViewModel: TimerViewModel by viewModels {
        TimerViewModelFactory((application as TimersApplication).repository)
    }
    private var lastTimerId: Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerview)
        val adapter = TimerListAdapter()
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        timerViewModel.allTimers.observe(owner = this) { timers ->
            timers.let {adapter.submitList(it)}
        }

        timerViewModel.lastTimer?.observe(owner = this) { timer ->
            this.lastTimerId = timer.id
        }

        findViewById<FloatingActionButton>(R.id.btnShowDialog).setOnClickListener {
            val dialog = AddTimerDialog()
            dialog.show(supportFragmentManager, "AddTimerDialog")
        }
    }

    override fun onPositiveClick(
        dialog: DialogFragment,
        selectedHour: Int,
        selectedMin: Int,
        alarmName: String
    ) {
//        TODO:新規・編集をダイアログのリザルトで分ける
        val id = this.lastTimerId.toInt() + 1
        val time = "%02d:%02d".format(selectedHour, selectedMin)
        val myTimer: MyTimer = MyTimer(0, alarmName, selectedHour, selectedMin, time)
        timerViewModel.insertTimer(myTimer)
        setAlarm(id, selectedHour, selectedMin)
        val toast = Toast.makeText(applicationContext, R.string.add_timer_finish, Toast.LENGTH_LONG)
        toast.show()
        Log.d("item", "main get: %02d:%02d, %s".format(selectedHour, selectedMin, alarmName))
    }

    override fun onNegativeClick(dialog: DialogFragment) {
        return
    }

    private fun setAlarm(id: Int, hour: Int, min: Int) {
        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(this, MyReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            this, id, intent, PendingIntent.FLAG_UPDATE_CURRENT
        )
        val calendar: Calendar = Calendar.getInstance().apply {
            timeInMillis = System.currentTimeMillis()
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, min)
        }
        Log.d("item", "set alarm: %d, id: %d".format(calendar.timeInMillis, id))

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            alarmManager.setAlarmClock(AlarmClockInfo(calendar.timeInMillis, null), pendingIntent)
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, pendingIntent)
        } else {
            alarmManager[AlarmManager.RTC_WAKEUP, calendar.timeInMillis] = pendingIntent
        }
    }


    fun sendMessage(view: View) {

//        val editText = findViewById<EditText>(R.id.editText)
//        val message = editText.text.toString()
//
//        val editTime = findViewById<EditText>(R.id.editTime)

//        val intent = Intent(this, DisplayMessageActivity::class.java).apply {
//            putExtra(EXTRA_MESSAGE, message)
//        }
//        startActivity(intent)
    }
}