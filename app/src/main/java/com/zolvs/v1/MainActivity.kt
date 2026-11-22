package com.zolvs.v1

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast

class MainActivity : Activity() {
    private val pickElf = 41

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(32, 48, 32, 32)
            setBackgroundColor(0xFF101014.toInt())
        }

        val title = TextView(this).apply {
            text = "ZOLVS V1"
            textSize = 28f
            setTextColor(0xFFFF334F.toInt())
        }
        val subtitle = TextView(this).apply {
            text = "ANDROID 10–16  •  NON-ROOT  •  ARM64"
            textSize = 13f
            setTextColor(0xFFBBBBBB.toInt())
            setPadding(0, 8, 0, 28)
        }

        val overlay = makeButton("ENABLE FLOATING MENU") {
            if (Settings.canDrawOverlays(this)) {
                startService(Intent(this, OverlayService::class.java))
                Toast.makeText(this, "Overlay started", Toast.LENGTH_SHORT).show()
            } else {
                val intent = Intent(
                    Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:$packageName")
                )
                startActivity(intent)
            }
        }

        val analyzer = makeButton("SELECT libil2cpp.so") {
            val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
                addCategory(Intent.CATEGORY_OPENABLE)
                type = "*/*"
            }
            startActivityForResult(intent, pickElf)
        }

        val status = TextView(this).apply {
            text = "Analyzer: ready\nNo root access required."
            textSize = 14f
            setTextColor(0xFFE0E0E0.toInt())
            setPadding(0, 24, 0, 0)
        }

        layout.addView(title)
        layout.addView(subtitle)
        layout.addView(overlay)
        layout.addView(analyzer)
        layout.addView(status)
        setContentView(layout)
    }

    private fun makeButton(label: String, action: () -> Unit): Button =
        Button(this).apply {
            text = label
            setTextColor(0xFFFFFFFF.toInt())
            setBackgroundColor(0xFFB51F38.toInt())
            setOnClickListener { action() }
        }

    @Deprecated("Deprecated in Android API; retained for broad device compatibility")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == pickElf && resultCode == RESULT_OK) {
            val uri = data?.data ?: return
            contentResolver.openFileDescriptor(uri, "r")?.use { pfd ->
                val result = NativeAnalyzer.inspectFd(pfd.fd)
                Toast.makeText(this, result, Toast.LENGTH_LONG).show()
            } ?: Toast.makeText(this, "Could not open file", Toast.LENGTH_LONG).show()
        }
    }
}
