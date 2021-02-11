package com.example.smartphonelrocker

import android.app.AlarmManager
import android.app.AlarmManager.AlarmClockInfo
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.net.Uri
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
import com.example.smartphonelrocker.utilities.*
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import android.provider.Settings
import java.util.*
import java.util.Objects.isNull
import kotlin.coroutines.CoroutineContext


class MainActivity : AppCompatActivity(), EditTimerDialog.NoticeDialogListener {

    companion object {
        /** ID for the runtime permission dialog */
        private const val OVERLAY_PERMISSION_REQUEST_CODE = 1
    }

    private val timerViewModel: TimerViewModel by viewModels {
        TimerViewModelFactory((application as TimersApplication).repository)
    }
    private var lastTimerId: Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        requestOverlayPermission()

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerview)
        val adapter = TimerListAdapter()
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        timerViewModel.allTimers.observe(owner = this) { timers ->
            timers.let { adapter.submitList(it) }
        }

        timerViewModel.lastTimer?.observe(owner = this) { timer ->
            this.lastTimerId = if (isNull(timer)) {0} else {timer.id}
        }

        findViewById<FloatingActionButton>(R.id.btnShowDialog).setOnClickListener {
            val dialog = EditTimerDialog()
            dialog.show(supportFragmentManager, "EditTimerDialog")
        }

        adapter.setOnItemClickListener(object : TimerListAdapter.OnItemClickListener {
            override fun onItemClickListener(view: View, position: Int, clickedTimer: MyTimer) {
                val bundle = Bundle().apply {
                    this.putInt("id", clickedTimer.id.toInt())
                    this.putInt("hour", clickedTimer.hour)
                    this.putInt("min", clickedTimer.min)
                    this.putString("name", clickedTimer.name)
                }
                val dialog = EditTimerDialog()
                dialog.arguments = bundle
                dialog.show(supportFragmentManager, "EditTimerDialog")
            }
        })
    }

    override fun onSaveClick(
        dialog: DialogFragment,
        alarmId: Int?,
        selectedName: String,
        selectedHour: Int,
        selectedMin: Int,
    ) {
        val time = "%02d:%02d".format(selectedHour, selectedMin)
        if (alarmId == null) {
            // add new alarm
            val myTimer: MyTimer = MyTimer(0, selectedName, selectedHour, selectedMin, time)
            timerViewModel.insertTimer(myTimer)
            val id = this.lastTimerId.toInt()
            setAlarm(this, id, selectedName, selectedHour, selectedMin, time)
            Log.d("MainActivity", "Add Alarm: id:%d, time:%s".format(id, time))
            Log.d("info", "this.lastTimerId.toInt():%d".format(this.lastTimerId.toInt()))
        } else {
            // update exist alarm
            val myTimer: MyTimer =
                MyTimer(alarmId.toLong(), selectedName, selectedHour, selectedMin, time)
            timerViewModel.updateTimer(myTimer)
            deleteAlarm(this, alarmId)
            setAlarm(this, alarmId, selectedName, selectedHour, selectedMin, time)
            Log.d("MainActivity", "Update Alarm: id:%d, time:%s".format(alarmId, time))
        }
        val toast =
            Toast.makeText(applicationContext, R.string.edit_timer_finish, Toast.LENGTH_LONG)
        toast.show()
    }

    override fun onDeleteClick(dialog: DialogFragment, alarmId: Int) {
        timerViewModel.deleteTimer(alarmId)
        try {
            val toast =
                Toast.makeText(applicationContext, R.string.delete_timer_finish, Toast.LENGTH_LONG)
            toast.show()
            Log.d("MainActivity", "Update Alarm: id:%d, time:%s".format(alarmId))
            Log.d("info", "this.lastTimerId.toInt():%d".format(this.lastTimerId.toInt()))
        } catch (e: Exception) {
            Log.d("warn", "Cannot delete Timer.")
            val toast =
                Toast.makeText(applicationContext, R.string.delete_timer_failure, Toast.LENGTH_LONG)
            toast.show()
        }
    }

    override fun onCancelClick(dialog: DialogFragment) {
        return
    }

    /** Requests an overlay permission to the user if needed. */
    private fun requestOverlayPermission() {
        if (isOverlayGranted()) return
        val intent = Intent(
            Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
            Uri.parse("package:$packageName")
        )
        startActivityForResult(intent, OVERLAY_PERMISSION_REQUEST_CODE)
    }

    /** Terminates the app if the user does not accept an overlay. */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == OVERLAY_PERMISSION_REQUEST_CODE) {
            if (!isOverlayGranted()) {
                finish()  // Cannot continue if not granted
            }
        }
    }

    /** Checks if the overlay is permitted. */
    private fun isOverlayGranted() =
        Build.VERSION.SDK_INT < Build.VERSION_CODES.M ||
                Settings.canDrawOverlays(this)


}