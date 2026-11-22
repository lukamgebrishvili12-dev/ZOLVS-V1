package com.zolvs.v1

import android.app.Service
import android.content.Intent
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.PixelFormat
import android.os.IBinder
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import kotlin.math.roundToInt

class OverlayService : Service() {
    private lateinit var wm: WindowManager
    private var panel: OverlayPanel? = null

    override fun onCreate() {
        super.onCreate()
        wm = getSystemService(WINDOW_SERVICE) as WindowManager
        panel = OverlayPanel()
        val params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
                WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN,
            PixelFormat.TRANSLUCENT
        ).apply {
            gravity = Gravity.TOP or Gravity.START
            x = 30
            y = 140
        }
        wm.addView(panel, params)
    }

    override fun onDestroy() {
        panel?.let { runCatching { wm.removeView(it) } }
        panel = null
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? = null

    inner class OverlayPanel : View(this@OverlayService) {
        private val p = Paint(Paint.ANTI_ALIAS_FLAG)
        private var fov = 50
        private var range = 100
        private var dragging = false
        private var downX = 0f
        private var downY = 0f

        init { setLayerType(View.LAYER_TYPE_SOFTWARE, null) }

        override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
            setMeasuredDimension(dp(280), dp(240))
        }

        override fun onDraw(c: Canvas) {
            super.onDraw(c)
            p.color = 0xEE101014.toInt()
            c.drawRoundRect(0f, 0f, width.toFloat(), height.toFloat(), dp(14).toFloat(), dp(14).toFloat(), p)
            p.color = 0xFFFF334F.toInt()
            c.drawRoundRect(0f, 0f, width.toFloat(), dp(42).toFloat(), dp(14).toFloat(), dp(14).toFloat(), p)
            p.color = 0xFFFFFFFF.toInt()
            p.textSize = dp(16).toFloat()
            c.drawText("ZOLVS  •  NON-ROOT", dp(14).toFloat(), dp(27).toFloat(), p)

            drawSlider(c, "FOV", fov, 20, 120, 78)
            drawSlider(c, "RANGE", range, 10, 300, 148)

            p.color = 0xFFAAAAAA.toInt()
            p.textSize = dp(10).toFloat()
            c.drawText("UI DEMO — values are local settings only", dp(12).toFloat(), dp(220).toFloat(), p)
        }

        private fun drawSlider(c: Canvas, label: String, value: Int, min: Int, max: Int, y: Int) {
            p.color = 0xFFEAEAEA.toInt()
            p.textSize = dp(13).toFloat()
            c.drawText("$label: $value", dp(14).toFloat(), dp(y).toFloat(), p)
            val left = dp(14).toFloat()
            val right = width - dp(14).toFloat()
            val barY = dp(y + 20).toFloat()
            p.color = 0xFF45454D.toInt()
            p.strokeWidth = dp(5).toFloat()
            c.drawLine(left, barY, right, barY, p)
            val fraction = (value - min).toFloat() / (max - min)
            p.color = 0xFFFF334F.toInt()
            c.drawLine(left, barY, left + (right - left) * fraction, barY, p)
            c.drawCircle(left + (right - left) * fraction, barY, dp(7).toFloat(), p)
        }

        override fun onTouchEvent(e: MotionEvent): Boolean {
            when (e.action) {
                MotionEvent.ACTION_DOWN -> {
                    downX = e.x
                    downY = e.y
                    dragging = true
                    return true
                }
                MotionEvent.ACTION_MOVE -> if (dragging) {
                    if (downY < dp(42)) {
                        val lp = layoutParams as WindowManager.LayoutParams
                        lp.x += (e.rawX - (lp.x + downX)).roundToInt()
                        lp.y += (e.rawY - (lp.y + downY)).roundToInt()
                        wm.updateViewLayout(this, lp)
                    } else {
                        val fraction = (e.x / width.toFloat()).coerceIn(0f, 1f)
                        if (downY < dp(120)) fov = (20 + fraction * 100).roundToInt()
                        else range = (10 + fraction * 290).roundToInt()
                        invalidate()
                    }
                    return true
                }
                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                    dragging = false
                    return true
                }
            }
            return true
        }

        private fun dp(v: Int) = (v * resources.displayMetrics.density).roundToInt()
    }
}
