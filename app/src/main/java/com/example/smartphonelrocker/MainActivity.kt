package com.example.smartphonelrocker

import android.content.Intent
import android.content.res.Resources
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.observe
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.smartphonelrocker.timer.*
import com.example.smartphonelrocker.utilities.cancelAlarm
import com.example.smartphonelrocker.utilities.setAlarmByTimer
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.*
import java.util.Objects.isNull
import kotlin.coroutines.CoroutineContext


class MainActivity : AppCompatActivity(), EditTimerDialog.NoticeDialogListener, CoroutineScope {

    companion object {
        /** ID for the runtime permission dialog */
        private const val OVERLAY_PERMISSION_REQUEST_CODE = 1
    }

    private val job = Job()
    private val timerViewModel: TimerViewModel by viewModels {
        TimerViewModelFactory((application as TimersApplication).repository)
    }
    private var lastTimerId: Long = 0

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestOverlayPermission()

        setTheme(R.style.Theme_SmartphoneLRocker)
        setContentView(R.layout.activity_main)

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerview)
        val adapter = TimerListAdapter()
        recyclerView.adapter = adapter
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

        recyclerView.layoutManager = LinearLayoutManager(this)
        val itemDecoration = DividerItemDecoration(this, DividerItemDecoration.VERTICAL)
        recyclerView.addItemDecoration(itemDecoration)

        timerViewModel.allTimers.observe(owner = this) { timers ->
            timers.let { adapter.submitList(it) }
        }

        timerViewModel.lastTimer?.observe(owner = this) { timer ->
            if (!isNull(timer)) {
                this.lastTimerId = timer.id
            }
            Log.d("info", "this.lastTimerId:%d".format(this.lastTimerId))
        }

        findViewById<FloatingActionButton>(R.id.btnShowDialog).setOnClickListener {
            val dialog = EditTimerDialog()
            dialog.show(supportFragmentManager, "EditTimerDialog")
        }

//        findViewById<FloatingActionButton>(R.id.btnAllDelete).setOnClickListener {
//            timerViewModel.deleteAll()
//        }

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.my_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_alarm -> {
                super.onOptionsItemSelected(item)
            }
            R.id.action_log -> {
                val logIntent = Intent(this, LogActivity::class.java)
                startActivity(logIntent)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onSaveClick(
        dialog: DialogFragment,
        alarmId: Int?,
        selectedName: String,
        selectedHour: Int,
        selectedMin: Int,
    ) {
        val time = "%02d:%02d".format(selectedHour, selectedMin)
        val myTimer: MyTimer =
            MyTimer(alarmId?.toLong() ?: 0, selectedName, selectedHour, selectedMin, time)
        launch {
            saveAlarm(myTimer)
        }
    }

    override fun onDeleteClick(dialog: DialogFragment, alarmId: Int) {
        launch {
            deleteAlarm(alarmId)
        }
    }

    override fun onCancelClick(dialog: DialogFragment) {
        return
    }

    private suspend fun saveAlarm(myTimer: MyTimer) {
        val isEdit = myTimer.id.toInt() != 0
        coroutineScope {
            try {
                val insertedId = withContext(context = Dispatchers.IO) {
                    timerViewModel.insertTimer(myTimer)
                }
                myTimer.id = insertedId
                setAlarmByTimer(applicationContext, myTimer)
                Log.d(
                    "MainActivity:saveAlarm",
                    "%s alarm. id:%d, time:%s".format(
                        if (isEdit) "Edit" else "Add",
                        insertedId,
                        myTimer.time
                    )
                )
                Toast.makeText(
                    applicationContext,
                    if (isEdit) R.string.edit_timer_finish else R.string.add_timer_finish,
                    Toast.LENGTH_LONG
                ).show()
            } catch (e: Exception) {
                Log.d(
                    "MainActivity:saveAlarm",
                    "Cannot save alarm. id:%d, time:%s, %s".format(
                        isEdit,
                        myTimer.time,
                        e.toString()
                    )
                )
                Toast.makeText(applicationContext, R.string.edit_timer_failure, Toast.LENGTH_LONG)
                    .show()
            }
        }
    }

    private suspend fun deleteAlarm(id: Int) {
        val toast =
            Toast.makeText(applicationContext, R.string.delete_timer_finish, Toast.LENGTH_LONG)
        val failureToast =
            Toast.makeText(applicationContext, R.string.delete_timer_failure, Toast.LENGTH_LONG)
        coroutineScope {
            val result = runCatching {
                cancelAlarm(applicationContext, id)
            }.fold(
                onSuccess = {
                    it.also {
                        val deletedId = withContext(context = Dispatchers.IO) {
                            timerViewModel.deleteTimer(id)
                        }
                        if (deletedId > 0) {
                            Log.d(
                                "MainActivity:deleteAlarm",
                                "Alarm deleted. id:%d, deletedId:%d".format(id, deletedId)
                            )
                            toast.show()
                        } else {
                            Log.d(
                                "MainActivity:deleteAlarm",
                                "Cannot delete alarm. id:%d, deletedId:%d".format(id, deletedId)
                            )
                            failureToast.show()
                        }
                    }
                },
                onFailure = {
                    Log.d("MainActivity:deleteAlarm", "Cannot cancel alarm: %s".format(it))
                    failureToast.show()
                }
            )
        }
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
        Build.VERSION.SDK_INT < Build.VERSION_CODES.M || Settings.canDrawOverlays(this)

    override fun onDestroy() {
        job.cancel()
        super.onDestroy()
    }
}