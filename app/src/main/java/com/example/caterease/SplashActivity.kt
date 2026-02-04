package com.example.caterease
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.widget.Button

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        val btnGetStarted = findViewById<Button>(R.id.btnGetStarted)

        btnGetStarted.setOnClickListener {
            // Intent to move to the next screen (e.g., MainActivity)
            val intent = Intent(this, MenuActivity::class.java)
            startActivity(intent)
            finish() // Closes splash so user can't "go back" to it
        }
    }
}