package com.example.testbuddy.deeplink

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.util.ArrayMap
import com.example.testbuddy.base.BasePresenter
import com.example.testbuddy.deeplink.db.DeepLinkDatabase
import com.example.testbuddy.deeplink.db.DeeplinkModel
import com.example.testbuddy.utils.RequestCode
import io.reactivex.Completable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import java.util.ArrayList

class DeeplinkListPresenter constructor(
    private val delegate: DeeplinkListDelegate,
    private val deepLinkDatabase: DeepLinkDatabase
) : BasePresenter() {

    //region Variables
    private val viewModel: DeeplinkViewModel by lazy { delegate.getViewModel(DeeplinkViewModel::class.java) as DeeplinkViewModel }
    private var disposable: Disposable? = null
    private val indexMapFromFilteredListToOriginalList = ArrayMap<Int, Int>()
    //endregion

    //region Lifecycle
    override fun onReattach() {

    }

    override fun onInitialAttach() {
        getDeepLinkListFromDb()
    }

    fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == RequestCode.ADD_DEEP_LINK && resultCode == RESULT_OK) {
            refreshDeepLinkList()
        }
    }

    fun onDestroy() {
        onSnackbarDismissed()
    }
    //endregion

    //region Clicks
    fun onAddDeepLinkClick() {
        delegate.openAddDeepLinkActivity(RequestCode.ADD_DEEP_LINK)
    }

    fun onItemSwiped(position: Int) {
        val positionInOriginalList = indexMapFromFilteredListToOriginalList[position] ?: 0

        if (viewModel.swipedItem != null) {
            deletePreviousDeepLinkFromDb(positionInOriginalList)
        } else {
            viewModel.swipedItemPosition = positionInOriginalList
            viewModel.swipedItem = viewModel.deeplinkList?.get(positionInOriginalList)

            (viewModel.deeplinkList as? ArrayList)?.removeAt(positionInOriginalList)
            updateDeepLinkList()
            delegate.showSnackbar()
            viewModel.isSnackbarShowing = true
        }
    }

    fun onUndoClick() {
        viewModel.swipedItem?.let {
            (viewModel.deeplinkList as ArrayList).add(viewModel.swipedItemPosition, it)
            viewModel.isDeleteUndone = true
            updateDeepLinkList()
        }
    }

    fun onSnackbarDismissed() {
        if (!viewModel.isDeleteUndone) {
            deleteDeepLinkFromDb()
        } else {
            viewModel.isDeleteUndone = false
        }
        viewModel.isSnackbarShowing = false
    }

    // search bar will restore the search text itself and call its text watcher
    fun onSearchTextChanged(searchText: String?) {
        viewModel.searchText = searchText
        updateDeepLinkList()
    }
    //endregion

    //region Private Helpers
    private fun deleteDeepLinkFromDb() {
        viewModel.swipedItem?.let {
            if (viewModel.deleteDeepLinkCompletable == null) {
                viewModel.deleteDeepLinkCompletable = Completable.fromAction {
                    deepLinkDatabase.deepLinkDao().deleteDeepLink(it)
                }.subscribeOn(Schedulers.io())
            }

            viewModel.deleteDeepLinkCompletable
                ?.observeOn(AndroidSchedulers.mainThread())
                ?.subscribe {
                    viewModel.deleteDeepLinkCompletable = null

                    viewModel.swipedItem = null
                    delegate.showDeepLinkDeleteToast()
                }
        }
    }

    private fun deletePreviousDeepLinkFromDb(currentPosition: Int) {
        viewModel.swipedItem?.let {
            if (viewModel.deleteDeepLinkCompletable == null) {
                viewModel.deleteDeepLinkCompletable = Completable.fromAction {
                    deepLinkDatabase.deepLinkDao().deleteDeepLink(it)
                }.subscribeOn(Schedulers.io())
            }

            viewModel.deleteDeepLinkCompletable
                ?.observeOn(AndroidSchedulers.mainThread())
                ?.subscribe {
                    viewModel.deleteDeepLinkCompletable = null

                    viewModel.swipedItemPosition = currentPosition
                    viewModel.swipedItem = viewModel.deeplinkList?.get(currentPosition)

                    (viewModel.deeplinkList as? ArrayList)?.removeAt(currentPosition)
                    delegate.updateDeepLinkList(viewModel.deeplinkList, viewModel.searchText)
                    delegate.showSnackbar()
                    viewModel.isSnackbarShowing = true
                }
        }
    }

    private fun getDeepLinkListFromDb() {
        if (viewModel.deeplinkList.isNullOrEmpty() && viewModel.searchText.isNullOrBlank()) {
            if (viewModel.getDeepLinkListSingle == null) {
                viewModel.getDeepLinkListSingle = getDeepLinksFromDb()
            }

            disposable = viewModel.getDeepLinkListSingle
                ?.observeOn(AndroidSchedulers.mainThread())
                ?.subscribe(
                    {
                        viewModel.deeplinkList = it
                        if (it.isEmpty()) {
                            delegate.openAddDeepLinkActivity(RequestCode.ADD_DEEP_LINK)
                        } else {
                            updateDeepLinkList()
                        }

                        disposable?.dispose()
                    },
                    {
                        disposable?.dispose()
                    }
                )
        }
    }

    private fun refreshDeepLinkList() {
        if (viewModel.getDeepLinkListSingle == null) {
            viewModel.getDeepLinkListSingle = getDeepLinksFromDb()
        }

        disposable = viewModel.getDeepLinkListSingle
            ?.observeOn(AndroidSchedulers.mainThread())
            ?.subscribe(
                {
                    viewModel.deeplinkList = it
                    delegate.updateSearchText(null)
                    disposable?.dispose()
                },
                {
                    disposable?.dispose()
                }
            )
    }

    private fun getDeepLinksFromDb(): Single<List<DeeplinkModel>> =
        deepLinkDatabase.deepLinkDao()
            .getDeepLinkList()
            .subscribeOn(Schedulers.io())


    private fun updateDeepLinkList() {
        viewModel.deeplinkList?.apply {
            val filteredDeepLinkList = this.filter { it.url.contains(viewModel.searchText ?: "", ignoreCase = true) || it.description?.contains(viewModel.searchText ?: "") == true }
            calculateIndexMap(filteredDeepLinkList, this)
            delegate.updateDeepLinkList(filteredDeepLinkList, viewModel.searchText)
        }
    }

    private fun calculateIndexMap(filteredList: List<DeeplinkModel>, originalList: List<DeeplinkModel>) {
        indexMapFromFilteredListToOriginalList.clear()
        filteredList.forEach {
            if (originalList.contains(it)) {
                indexMapFromFilteredListToOriginalList[filteredList.indexOf(it)] = originalList.indexOf(it)
            }
        }
    }
    //endregion
}