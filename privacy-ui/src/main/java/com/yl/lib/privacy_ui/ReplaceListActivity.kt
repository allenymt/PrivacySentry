package com.yl.lib.privacy_ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.RecyclerView
import com.yl.lib.privacy_ui.replace.ReplaceAdapter
import com.yl.lib.privacy_ui.replace.ReplaceItemList
import com.yl.lib.privacy_ui.replace.ReplaceViewModel

/**
 * 展示静态代理数据
 * @property searchBar SearchView?
 * @property recyclerView RecyclerView?
 */
class ReplaceListActivity : AppCompatActivity() {

    var searchBar: SearchView? = null

    var recyclerView: RecyclerView? = null

    var adapter: ReplaceAdapter? = null
    var viewModel: ReplaceViewModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_replace_list)
        searchBar = findViewById(R.id.search_bar)
        recyclerView = findViewById(R.id.content)
        viewModel = ViewModelProvider.AndroidViewModelFactory.getInstance(application).create(
            ReplaceViewModel::class.java
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
        adapter = ReplaceAdapter()
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
            Observer<ArrayList<ReplaceItemList>> {
                if (it != null) {
                    adapter?.setData(it)
                }
            })
        viewModel?.buildData(this)
    }
}