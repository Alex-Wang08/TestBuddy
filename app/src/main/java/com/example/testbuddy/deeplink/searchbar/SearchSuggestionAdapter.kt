package com.example.testbuddy.deeplink.searchbar

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.testbuddy.deeplink.db.DeeplinkModel
import com.example.testbuddy.deeplink.deeplinkrow.DeepLinkSuggestionRow
import com.mancj.materialsearchbar.adapter.SuggestionsAdapter

class SearchSuggestionAdapter(
    inflater: LayoutInflater
) : SuggestionsAdapter<DeeplinkModel, SearchSuggestionAdapter.SearchSuggestionViewHolder>(inflater) {


    //region Implementation
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchSuggestionViewHolder = SearchSuggestionViewHolder(DeepLinkSuggestionRow(parent.context))

    override fun getSingleViewHeight(): Int {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onBindSuggestionHolder(p0: DeeplinkModel?, p1: SearchSuggestionViewHolder?, p2: Int) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
    //endregion





    class SearchSuggestionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
}