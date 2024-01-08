package com.example.weatherapp

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.weatherapp.databinding.ItemSearchHistoryBinding

class SearchHistoryAdapter(private var searchHistoryList: List<SearchHistory>) :
    RecyclerView.Adapter<SearchHistoryAdapter.SearchHistoryViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchHistoryViewHolder {
        val binding =
            ItemSearchHistoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SearchHistoryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SearchHistoryViewHolder, position: Int) {
        val searchHistory = searchHistoryList[position]
        holder.bind(searchHistory)
    }

    override fun getItemCount(): Int {
        return searchHistoryList.size
    }
    fun updateList(newList: List<SearchHistory>) {
        searchHistoryList = newList
        notifyDataSetChanged()
    }
    inner class SearchHistoryViewHolder(private val binding: ItemSearchHistoryBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(searchHistory: SearchHistory) {
            binding.apply {
                // Bind your data to the views using View Binding
                cityTextView.text = searchHistory.city
                dateTextView.text = searchHistory.date
                timeTextView.text = searchHistory.time
                tempTextView.text = searchHistory.temp
            }
        }
    }
}

