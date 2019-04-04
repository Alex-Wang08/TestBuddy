package com.example.testbuddy.deeplink

import android.content.Context
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.testbuddy.deeplink.db.DeeplinkModel
import com.example.testbuddy.deeplink.deeplinkrow.DeepLinkRow

class DeepLinkListAdapter constructor(
    private val context: Context
) : RecyclerView.Adapter<DeepLinkListAdapter.DeepLinkRowViewHolder>() {

    var deepLinkList: List<DeeplinkModel>? = null

    class DeepLinkRowViewHolder(deepLinkRow: DeepLinkRow) : RecyclerView.ViewHolder(deepLinkRow)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DeepLinkRowViewHolder {
        val view = DeepLinkRow(context)
        return DeepLinkRowViewHolder(view)
    }

    override fun getItemCount(): Int = deepLinkList?.size ?: 0

    override fun onBindViewHolder(holder: DeepLinkRowViewHolder, position: Int) {
        (holder.itemView as? DeepLinkRow)?.apply {
            updateDeepLinkInfo(deepLinkList?.get(position))
            if (position == itemCount - 1 ) {
                hideDivider()
            }
        }
    }
}