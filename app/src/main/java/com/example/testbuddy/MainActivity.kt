package com.example.testbuddy

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.bluelinelabs.conductor.Conductor
import com.bluelinelabs.conductor.Router
import com.bluelinelabs.conductor.RouterTransaction
import com.example.testbuddy.deeplink.DeeplinkListController
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), MainDelegate {

    //region Variables
    private lateinit var presenter: MainPresenter
    private var router: Router? = null
    //endregion

    //region Lifecycle
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initializeRouter(savedInstanceState)
    }
    //endregion

    //region Delegate
    override fun placeHolder() {

    }
    //endregion

    //region Private Helpers
    private fun initializeRouter(savedInstanceState: Bundle?) {
        router = Conductor.attachRouter(this, contentLayout, savedInstanceState)

        router?.run {
            if (!hasRootController()) {
                setRoot(RouterTransaction.with(DeeplinkListController()))
            }
        }
    }
    //endregion
}