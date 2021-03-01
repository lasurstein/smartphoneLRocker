package com.example.smartphonelrocker

import android.content.Context
import android.graphics.PixelFormat
import android.util.AttributeSet
import android.view.View
import android.view.WindowManager
import android.widget.FrameLayout

/**
 * TODO: document your custom view class.
 */
class PlayRockView @JvmOverloads constructor(
    ctx: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
) : FrameLayout(ctx, attrs, defStyle) {

    companion object {
        /** Creates an instance of [OverlayView]. */
        fun create(context: Context) =
            View.inflate(context, R.layout.play_rock_view, null) as PlayRockView
    }

    private val windowManager: WindowManager =
        ctx.getSystemService(Context.WINDOW_SERVICE) as WindowManager

    /** Settings for overlay view */
    // TODO: layoutの調査：NO＿LIMITS外してxmlをwrap_contentとか
    private val layoutParams = WindowManager.LayoutParams(
        WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,  // Overlay レイヤに表示
        WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS  // 画面外への拡張を許可
                or WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON // screenON
                or WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL // 画面外のタッチイベントを背後に送る
                or WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE  // フォーカスを奪わない
                or WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
//                or WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,  // 画面外への拡張を許可
        PixelFormat.TRANSLUCENT
    )

    /** Starts displaying this view as overlay. */
    @Synchronized
    fun show() {
        if (!this.isShown) {
            windowManager.addView(this, layoutParams)
        }
    }

    /** Hide this view. */
    @Synchronized
    fun hide() {
        if (this.isShown) {
            windowManager.removeView(this)
        }
    }
}