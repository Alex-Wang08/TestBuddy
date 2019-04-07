package com.example.testbuddy.deeplink

import android.app.Application
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bluelinelabs.conductor.Controller
import com.example.testbuddy.R
import com.example.testbuddy.deeplink.adddeeplink.AddDeepLinkActivity
import com.example.testbuddy.deeplink.db.DeepLinkDatabase
import com.example.testbuddy.deeplink.db.DeeplinkModel
import com.example.testbuddy.utils.RequestCode
import com.example.testbuddy.utils.createClickListenerObservable
import com.google.android.material.snackbar.Snackbar
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.controller_deeplink_list.view.*
import java.util.ArrayList

class DeeplinkListController : Controller(), DeeplinkListDelegate {

    //region Variables
    private lateinit var presenter: DeeplinkListPresenter
    private var adapter: DeepLinkListAdapter? = null
    private var disposable: Disposable? = null
    private var snackBar: Snackbar? = null
    //endregion

    //region Lifecycle
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup): View {
        val view = inflater.inflate(R.layout.controller_deeplink_list, container, false)
        view.run {
            initializeRecyclerView(deepLinkList, context)
            initializeSnackbar(this as CoordinatorLayout)
            disposable = deepLinkAddDeepLink.createClickListenerObservable().subscribe { presenter.onAddDeepLinkClick() }
        }
        return view
    }

    private fun initializeRecyclerView(recyclerView: RecyclerView, context: Context) {
        adapter = DeepLinkListAdapter(context)
        val layoutManager = LinearLayoutManager(context)
        recyclerView.layoutManager = layoutManager
        recyclerView.adapter = adapter

        val swipeToDeleteCallback = createSwipeToDeleteCallback()
        ItemTouchHelper(swipeToDeleteCallback).attachToRecyclerView(recyclerView)
    }

    private fun initializeSnackbar(coordinatorLayout: CoordinatorLayout) {
        snackBar = Snackbar.make(
            coordinatorLayout,
            "Item was removed from the list.",
            Snackbar.LENGTH_LONG
        ).apply {
            setAction("UNDO") {
                presenter.onUndoClick()
            }

            setActionTextColor(Color.YELLOW)
        }
    }

    private fun createSwipeToDeleteCallback(): SwipeToDeleteCallback = object : SwipeToDeleteCallback() {
        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
            presenter.onItemSwiped(viewHolder.adapterPosition)
        }
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
        adapter?.updateDeepLinkList(deepLinkList)
    }

    override fun openAddDeepLinkActivity(requestCode: Int) {
        activity?.let {
            startActivityForResult(AddDeepLinkActivity.createIntent(it), requestCode)
        }
    }

    override fun removeItem(position: Int) {
        adapter?.apply {
            removeItem(position)
            snackBar?.show()
        }
    }

    override fun restoreItem(position: Int, swipedItem: DeeplinkModel) {
        adapter?.restoreItem(position, swipedItem)
    }

    //endregion
}