package com.example.testbuddy.deeplink.adddeeplink

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.testbuddy.R

class AddDeepLinkActivity : AppCompatActivity() {

    companion object {
        fun createIntent(context: Context): Intent = Intent(context, AddDeepLinkActivity::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_deeplink)

    }
}