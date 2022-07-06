package com.yl.lib.privacy_ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.RecyclerView
import com.yl.lib.privacy_ui.real_time.RealTimeAdapter
import com.yl.lib.privacy_ui.real_time.RealTimeViewModel
import com.yl.lib.sentry.hook.printer.PrivacyFunBean

/**
 * 实时可见 敏感函数调用
 */
class RealTimePrivacyItemActivity : AppCompatActivity() {
    var searchBar: SearchView? = null

    var recyclerView: RecyclerView? = null

    var adapter: RealTimeAdapter? = null
    var viewModel: RealTimeViewModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_real_time_privacy_item)
        searchBar = findViewById(R.id.search_bar)
        recyclerView = findViewById(R.id.content)
        viewModel = ViewModelProvider.AndroidViewModelFactory.getInstance(application).create(
            RealTimeViewModel::class.java
        )
        searchBar?.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                viewModel?.search(query)
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                viewModel?.search(newText)
                return false
            }
        })
        buildData()
    }

    private fun buildData() {
        adapter = RealTimeAdapter()
        recyclerView?.adapter = adapter
        recyclerView?.layoutManager =
            androidx.recyclerview.widget.LinearLayoutManager(this)
        recyclerView?.addItemDecoration(
            DividerItemDecoration(
                this,
                DividerItemDecoration.VERTICAL
            )
        )

        viewModel?.observer()?.observe(this,
            Observer<ArrayList<PrivacyFunBean>> {
                if (it != null) {
                    adapter?.setData(it)
                }
            })
        viewModel?.buildData(this)
    }
}