package com.example.testbuddy.deeplink

import android.app.Application
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.bluelinelabs.conductor.Controller
import com.example.testbuddy.R
import com.example.testbuddy.deeplink.adddeeplink.AddDeepLinkActivity
import com.example.testbuddy.deeplink.db.DeepLinkDatabase
import com.example.testbuddy.deeplink.db.DeeplinkModel
import com.example.testbuddy.utils.RequestCode
import com.example.testbuddy.utils.createClickListenerObservable
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.controller_deeplink_list.view.*
import java.util.ArrayList

class DeeplinkListController : Controller(), DeeplinkListDelegate {

    //region Variables
    private lateinit var presenter: DeeplinkListPresenter
    private var adapter: DeepLinkListAdapter? = null
    private var disposable: Disposable? = null
    //endregion

    //region Lifecycle
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup): View {
        val view = inflater.inflate(R.layout.controller_deeplink_list, container, false)
        view.run {
            adapter = DeepLinkListAdapter(context)
            val layoutManager = LinearLayoutManager(context)
            deepLinkList.layoutManager = layoutManager
            deepLinkList.adapter = adapter

            disposable = deepLinkAddDeepLink.createClickListenerObservable().subscribe { presenter.onAddDeepLinkClick() }
        }
        return view
    }

    override fun onAttach(view: View) {
        super.onAttach(view)
        applicationContext?.let {
            presenter = DeeplinkListPresenter(this, DeepLinkDatabase.get(it))
            presenter.onAttach()
        }
    }

    override fun onDestroyView(view: View) {
        super.onDestroyView(view)
        disposable?.dispose()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        presenter.onActivityResult(requestCode, resultCode, data)
    }

    //endregion

    //region Delegate
    override fun placeHolder() {

    }

    override fun <T : ViewModel> getViewModel(clazz: Class<T>): ViewModel {
        if (activity is FragmentActivity) {
            return ViewModelProviders.of(activity as FragmentActivity).get(clazz)
        } else {
            // This is a bit of a hack which will create a new instance of the view model if the activity is not attached
            val defaultFactory =
                ViewModelProvider.AndroidViewModelFactory.getInstance(Application())
            return defaultFactory.create(clazz)
        }
    }

    override fun updateDeepLinkList(deepLinkList: List<DeeplinkModel>?) {
        deepLinkList?.let {
            adapter?.deepLinkList = it
            adapter?.notifyDataSetChanged()
        }
    }

    override fun openAddDeepLinkActivity(requestCode: Int) {
        activity?.let {
            startActivityForResult(AddDeepLinkActivity.createIntent(it), requestCode)
        }
    }
    //endregion
}