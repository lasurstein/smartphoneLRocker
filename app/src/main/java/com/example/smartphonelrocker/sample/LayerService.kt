package com.example.smartphonelrocker.sample

import android.app.Service
import android.content.Context
import android.content.Intent
import android.util.Log

class LayerService : Service() {

    companion object {
        private const val ACTION_SHOW = "SHOW"
        private const val ACTION_HIDE = "HIDE"

        fun start(context: Context) {
            val intent = Intent(context, LayerService::class.java).apply {
                action = ACTION_SHOW
            }
            context.startService(intent)
        }

        fun stop(context: Context) {
            val intent = Intent(context, LayerService::class.java).apply {
                action = ACTION_HIDE
            }
            context.startService(intent)
        }

        // To control toggle button in MainActivity. This is not elegant but works.
        var isActive = false
            private set
    }

    private lateinit var overlayView: OverlayView

    override fun onCreate() {
        super.onCreate()
        overlayView = OverlayView.create(this)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        intent?.let {
            when (it.action) {
                ACTION_SHOW -> {
                    isActive = true
                    overlayView.show()
                }
                ACTION_HIDE -> {
                    isActive = false
                    overlayView.hide()
                    stopSelf()
                }
                else -> Log.d("item","Need action property to start ${LayerService::class.java.simpleName}")
            }
        }
        return super.onStartCommand(intent, flags, startId)
    }

    /** Cleans up views just in case. */
    override fun onDestroy() = overlayView.hide()

    /** This service does not support binding. */
    override fun onBind(intent: Intent?) = null
}