package com.example.testbuddy.deeplink.adddeeplink

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProviders
import com.example.testbuddy.R
import com.example.testbuddy.deeplink.db.DeepLinkDatabase
import com.example.testbuddy.utils.createClickListenerObservable
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.activity_add_deeplink.*

class AddDeepLinkActivity : AppCompatActivity(), AddDeepLinkDelegate {

    //region Variables
    private lateinit var presenter: AddDeepLinkPresenter
    private val compositeDisposable = CompositeDisposable()
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

        compositeDisposable.addAll(
            addDeepLinkButton.createClickListenerObservable().subscribe { presenter.onAddDeepLinkClick() },
            addAndFireDeepLink.createClickListenerObservable().subscribe { presenter.onAddAndFireDeepLinkClick() }
        )
    }

    override fun onDestroy() {
        super.onDestroy()
        compositeDisposable.dispose()
    }
    //endregion

    //region Delegate Implementation
    override fun <T : ViewModel> getViewModel(clazz: Class<T>): ViewModel =  ViewModelProviders.of(this as FragmentActivity).get(clazz)

    override fun showDeepLinkEmptyToast() {

    }

    //endregion
}