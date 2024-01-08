package com.example.weatherapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.weatherapp.databinding.ActivityHistoryBinding

class HistoryActivity : AppCompatActivity() {
    private lateinit var binding: ActivityHistoryBinding
    private lateinit var searchHistoryAdapter: SearchHistoryAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityHistoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Retrieve the search history list from the intent
        val searchHistoryList = intent.getParcelableArrayListExtra<SearchHistory>("SEARCH_HISTORY_LIST")

        // Initialize the adapter
        searchHistoryAdapter = SearchHistoryAdapter(searchHistoryList!!)

        // Set up RecyclerView
        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(this@HistoryActivity)
            adapter = searchHistoryAdapter
        }

       //  Update the adapter with the search history list
        searchHistoryList?.let {
            searchHistoryAdapter.updateList(it)
        }

    }
}