package com.example.testbuddy.deeplink.adddeeplink

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.testbuddy.R
import com.example.testbuddy.deeplink.DeeplinkListDelegate
import com.example.testbuddy.deeplink.db.DeepLinkDatabase

class AddDeepLinkActivity : AppCompatActivity(), AddDeepLinkDelegate {

    //region Variables
    private lateinit var presenter: AddDeepLinkPresenter
    //endregion

    //region Intent
    companion object {
        fun createIntent(context: Context): Intent = Intent(context, AddDeepLinkActivity::class.java)
    }
    //endregion

    //region Lifecycle
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_deeplink)

        presenter = AddDeepLinkPresenter(this, DeepLinkDatabase.get(this))
    }
    //endregion

    //region Delegate Implementation




    //endregion



}