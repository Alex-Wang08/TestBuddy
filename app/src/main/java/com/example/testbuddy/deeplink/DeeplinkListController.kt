package com.example.testbuddy.deeplink

import android.app.Application
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.testbuddy.R
import com.example.testbuddy.base.BaseController
import com.example.testbuddy.base.BasePresenter
import com.example.testbuddy.deeplink.adddeeplink.AddDeepLinkActivity
import com.example.testbuddy.deeplink.db.DeepLinkDatabase
import com.example.testbuddy.deeplink.db.DeeplinkModel
import com.example.testbuddy.utils.createClickListenerObservable
import com.google.android.material.snackbar.Snackbar
import com.jakewharton.rxbinding3.material.dismisses
import com.mancj.materialsearchbar.MaterialSearchBar
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.controller_deeplink_list.view.*

class DeeplinkListController : BaseController(), DeeplinkListDelegate, DeepLinkListAdapter.Listener {

    //region Variables
    private lateinit var presenter: DeeplinkListPresenter
    private var adapter: DeepLinkListAdapter? = null
    private var disposable: Disposable? = null
    private var snackBar: Snackbar? = null
    private val snackBarDismissCallback = object : Snackbar.Callback() {
        override fun onDismissed(transientBottomBar: Snackbar?, event: Int) {
            presenter.onSnackbarDismissed()
        }
    }
    private val searchTextWatcher = object : TextWatcher {
        override fun afterTextChanged(s: Editable?) {

        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {


        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            presenter.onSearchTextChanged(s.toString())
        }
    }
    private var rootView: View? = null
    //endregion

    //region Lifecycle
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup): View {
        rootView = inflater.inflate(R.layout.controller_deeplink_list, container, false)
        rootView?.run {
            initializeSearchBar(searchBar)
            initializeRecyclerView(deepLinkList, context)
            initializeSnackbar(this as CoordinatorLayout)
            disposable = deepLinkAddDeepLink.createClickListenerObservable().subscribe { presenter.onAddDeepLinkClick() }
        }

        applicationContext?.let {
            presenter = DeeplinkListPresenter(this@DeeplinkListController, DeepLinkDatabase.get(it))
        }

        return rootView!!
    }

    override fun onDestroyView(view: View) {
        super.onDestroyView(view)
        presenter.onDestroy()

        disposable?.dispose()
        snackBar?.removeCallback(snackBarDismissCallback)
        rootView?.searchBar?.searchEditText?.removeTextChangedListener(searchTextWatcher)
        adapter?.onDestroy()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        presenter.onActivityResult(requestCode, resultCode, data)
    }
    //endregion

    //region Listener
    override fun onDeepLinkRowClick(url: String?) {
        activity?.let {
            val intent = Intent(Intent.ACTION_VIEW).apply {
                data = Uri.parse(url)
            }
            startActivity(intent)
        }
    }
    //endregion

    //region Delegate
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

    override fun updateDeepLinkList(deepLinkList: List<DeeplinkModel>?, searchText: String?) {
        adapter?.updateDeepLinkList(deepLinkList, searchText)
    }

    override fun openAddDeepLinkActivity(requestCode: Int) {
        activity?.let {
            startActivityForResult(AddDeepLinkActivity.createIntent(it), requestCode)
        }
    }

    override fun showDeepLinkDeleteToast() {
        Toast.makeText(applicationContext, "Deleted", Toast.LENGTH_SHORT).show()
    }

    override fun dismissSnackbar() {
        snackBar?.dismiss()
    }

    override fun showSnackbar() {
        snackBar?.show()
    }

    override fun removeSnackbarCallback() {
        snackBar?.removeCallback(snackBarDismissCallback)
    }

    override fun addSnackbarCallback() {
        snackBar?.addCallback(snackBarDismissCallback)
    }

    override fun updateSearchText(searchText: String?) {
        rootView?.searchBar?.searchEditText?.apply {
            setText(searchText)
        }
    }
    //endregion

    //region BaseController
    override fun getPresenter(): BasePresenter = presenter
    //endregion

    //region Private Helpers
    private fun initializeSearchBar(searchBar: MaterialSearchBar) {
        searchBar.apply {
            setCardViewElevation(10)
            addTextChangeListener(searchTextWatcher)
        }
    }

    private fun initializeRecyclerView(recyclerView: RecyclerView, context: Context) {
        adapter = DeepLinkListAdapter(context, this)
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

        snackBar?.addCallback(snackBarDismissCallback)
    }

    private fun createSwipeToDeleteCallback(): SwipeToDeleteCallback = object : SwipeToDeleteCallback() {
        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
            presenter.onItemSwiped(viewHolder.adapterPosition)
        }
    }
    //endregion
}