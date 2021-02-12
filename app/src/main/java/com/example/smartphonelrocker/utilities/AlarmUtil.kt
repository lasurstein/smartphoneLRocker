package com.example.smartphonelrocker.utilities

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import com.example.smartphonelrocker.AlarmReceiver
import java.util.*

fun setAlarm(context: Context, id: Int, name: String, hour: Int, min: Int, time: String) {
    val bundle = Bundle().apply {
        this.putInt("id", id)
        this.putString("name", name)
        this.putInt("hour", hour)
        this.putInt("min", min)
        this.putString("time", time)
    }
    val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    val intent = Intent(context, AlarmReceiver::class.java).let { intent ->
        intent.putExtra("TIMER_DETAIL", bundle)
        PendingIntent.getBroadcast(context, id, intent, PendingIntent.FLAG_UPDATE_CURRENT)
    }

    val calendar: Calendar = Calendar.getInstance().apply {
        timeInMillis = System.currentTimeMillis()
        set(Calendar.HOUR_OF_DAY, hour)
        set(Calendar.MINUTE, min)
        set(Calendar.SECOND, 0)
    }
    val diffCalendar = Calendar.getInstance()

    // 設定する時刻が現在時刻より前の場合，1日後に設定
    val diff = calendar.compareTo(diffCalendar)
    if (diff <= 0) {
        calendar.add(Calendar.DATE, 1)
    }

    alarmManager.setExactAndAllowWhileIdle(
        AlarmManager.RTC_WAKEUP,
        calendar.timeInMillis,
        intent
    )
//    Log.d("setAlarm", "alarm date: %s".format(calendar.time))
//    Log.d("setAlarm", "now date: %s".format(diffCalendar.time))

    Log.d(
        "setAlarm", "set alarm: %s, %02d:%02d, id: %d"
            .format(calendar.get(Calendar.DATE), hour, min, id)
    )
}

fun deleteAlarm(context: Context, id: Int) {
    val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    val intent = Intent(context, AlarmReceiver::class.java).let { intent ->
        PendingIntent.getBroadcast(context, id, intent, PendingIntent.FLAG_UPDATE_CURRENT)
    }

    alarmManager.cancel(intent)
    Log.d("deleteAlarm", "delete alarm: id: %d".format(id))
}