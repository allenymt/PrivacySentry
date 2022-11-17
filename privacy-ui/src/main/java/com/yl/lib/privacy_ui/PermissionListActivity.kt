package com.yl.lib.privacy_ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.RecyclerView
import com.yl.lib.privacy_ui.permission.PermissionAdapter
import com.yl.lib.privacy_ui.permission.PermissionItem
import com.yl.lib.privacy_ui.permission.PermissionViewModel
import com.yl.lib.privacy_ui.replace.ReplaceAdapter
import com.yl.lib.privacy_ui.replace.ReplaceItemList
import com.yl.lib.privacy_ui.replace.ReplaceViewModel

/**
 * @author yulun
 * @since 2022-11-17 14:45
 */
class PermissionListActivity : AppCompatActivity() {

    var searchBar: SearchView? = null

    var recyclerView: RecyclerView? = null

    var adapter: PermissionAdapter? = null
    var viewModel: PermissionViewModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_permission_list)
        searchBar = findViewById(R.id.search_bar)
        recyclerView = findViewById(R.id.content)
        viewModel = ViewModelProvider.AndroidViewModelFactory.getInstance(application).create(
            PermissionViewModel::class.java
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
        adapter = PermissionAdapter()
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
            Observer<ArrayList<PermissionItem>> {
                if (it != null) {
                    adapter?.setData(it)
                }
            })
        viewModel?.buildData(this)
    }
}