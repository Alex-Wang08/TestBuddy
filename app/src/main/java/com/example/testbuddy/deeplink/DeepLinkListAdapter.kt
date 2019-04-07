package com.example.testbuddy.deeplink

import android.content.Context
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.testbuddy.deeplink.db.DeeplinkModel
import com.example.testbuddy.deeplink.deeplinkrow.DeepLinkRow
import com.example.testbuddy.utils.createClickListenerObservable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import java.util.ArrayList

class DeepLinkListAdapter constructor(
    private val context: Context,
    private var listener: Listener?
) : RecyclerView.Adapter<DeepLinkListAdapter.DeepLinkRowViewHolder>() {

    //region Listener
    interface Listener {
        fun onDeepLinkRowClick(url: String?)
    }
    //endregion

    //region Variables
    private var deepLinkList: ArrayList<DeeplinkModel>? = null
    private val compositeDisposable = CompositeDisposable()
    //endregion

    //region ViewHolder
    class DeepLinkRowViewHolder(deepLinkRow: DeepLinkRow) : RecyclerView.ViewHolder(deepLinkRow)
    //endregion

    //region Implements
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DeepLinkRowViewHolder {
        val view = DeepLinkRow(context)
        return DeepLinkRowViewHolder(view)
    }

    override fun getItemCount(): Int = deepLinkList?.size ?: 0

    override fun onBindViewHolder(holder: DeepLinkRowViewHolder, position: Int) {
        (holder.itemView as? DeepLinkRow)?.apply {
            updateDeepLinkInfo(deepLinkList?.get(position))
            if (position == (deepLinkList?.size ?: 0) - 1) {
                hideDivider()
            } else {
                showDivider()
            }

            createClickListenerObservable().subscribe { listener?.onDeepLinkRowClick(deepLinkList?.get(position)?.url) }.addTo(compositeDisposable)
        }
    }
    //endregion

    //region external operations
    fun removeItem(position: Int) {
        deepLinkList?.removeAt(position)
        notifyDataSetChanged()
    }

    fun updateDeepLinkList(deepLinkList: List<DeeplinkModel>?) {
        this.deepLinkList = deepLinkList as ArrayList<DeeplinkModel>
        notifyDataSetChanged()
    }

    fun restoreItem(position: Int, swipedItem: DeeplinkModel) {
        deepLinkList?.add(position, swipedItem)
        notifyDataSetChanged()
    }

    fun onDestroy() {
        compositeDisposable.dispose()
        listener = null
    }
    //endregion
}