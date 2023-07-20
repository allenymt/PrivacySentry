package com.yl.lib.privacy_ui.replace

import android.app.AlertDialog
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Adapter
import com.yl.lib.privacy_ui.R

/**
 * @author yulun
 * @since 2022-07-04 19:35
 */
class ReplaceAdapter : Adapter<ReplaceVieHolder>() {
    var mData: List<ReplaceItemList>? = null

    fun setData(mData: List<ReplaceItemList>) {
        this.mData = mData
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReplaceVieHolder {
        return ReplaceVieHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.replace_item_view, parent, false)
        )
    }

    override fun onBindViewHolder(holder: ReplaceVieHolder, position: Int) {
        holder.bindData(mData!![position])
    }

    override fun getItemCount(): Int {
        return mData?.count() ?: 0
    }

}

class ReplaceVieHolder : RecyclerView.ViewHolder {
    var tvOriginMethodName: TextView? = null
    var tvCount: TextView? = null
    var data: ReplaceItemList? = null

    constructor(itemView: View) : super(itemView) {
        tvOriginMethodName = itemView.findViewById(R.id.origin_method_tv)
        tvCount = itemView.findViewById(R.id.origin_count_tv)
        itemView.setOnClickListener {
            var builder = AlertDialog.Builder(itemView.context)
            builder.setItems((data?.originMethodList?.map { it.toString() }
                ?: listOf()).toTypedArray()
            ) { _, _ ->  }
            builder.setTitle("代理列表")
            builder.setPositiveButton("确定") { _, _ -> }
            builder.create().show()
        }
    }

    fun bindData(data: ReplaceItemList) {
        this.data = data
        tvOriginMethodName?.text = data.proxyMethodName
        tvCount?.text = "代理次数:${data.count},点击查看详情"
    }
}
