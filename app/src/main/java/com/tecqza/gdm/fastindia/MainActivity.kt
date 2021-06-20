package com.tecqza.gdm.fastindia

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private lateinit var banner: ImageView
    private lateinit var grocery1: View
    private lateinit var grocery2: View
    private lateinit var medicines: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        banner = findViewById(R.id.banner)
        grocery1 = findViewById(R.id.grocery1)
        grocery2 = findViewById(R.id.grocery2)
        medicines = findViewById(R.id.medicines)

        banner.setOnClickListener { startWeb("https://play.google.com/store/apps/details?id=nic.goi.aarogyasetu&hl=en_IN&gl=US") }
        grocery1.setOnClickListener { startWeb("https://shop.bigbazaar.com/") }
        grocery2.setOnClickListener { startWeb("https://www.jiomart.com/") }
        medicines.setOnClickListener { startWeb("https://www.apollopharmacy.in/") }

    }

    private fun startWeb(url: String) {
        val i = Intent(this, WebPage::class.java)
        i.putExtra("url", url)
        startActivity(i)
    }

}