package com.yl.lib.privacy_ui.permission

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.text.toSpannable
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Adapter
import com.yl.lib.privacy_ui.R

/**
 * @author yulun
 * @since 2022-07-04 19:35
 */
class PermissionAdapter : Adapter<PermissionVieHolder>() {
    var mData: List<PermissionItem>? = null

    fun setData(mData: List<PermissionItem>) {
        this.mData = mData
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PermissionVieHolder {
        return PermissionVieHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.permission_item_view, parent, false)
        )
    }

    override fun onBindViewHolder(holder: PermissionVieHolder, position: Int) {
        holder.bindData(mData!![position])
    }

    override fun getItemCount(): Int {
        return mData?.count() ?: 0
    }

}

class PermissionVieHolder : RecyclerView.ViewHolder {
    var tvPermissionName: TextView? = null
    var tvPermissionDesc: TextView? = null
    var data: PermissionItem? = null

    constructor(itemView: View) : super(itemView) {
        tvPermissionName = itemView.findViewById(R.id.permission_tv)
        tvPermissionDesc = itemView.findViewById(R.id.permission_desc_tv)
    }

    fun bindData(data: PermissionItem) {
        this.data = data
        tvPermissionName?.text = data.name
        tvPermissionDesc?.text = data.desc
    }
}
