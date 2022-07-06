package com.yl.lib.privacy_ui.real_time

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Adapter
import com.yl.lib.privacy_ui.R
import com.yl.lib.sentry.hook.printer.PrivacyFunBean

/**
 * @author yulun
 * @since 2022-07-04 19:35
 */
class RealTimeAdapter : Adapter<ReplaceVieHolder>() {
    var mData: List<PrivacyFunBean>? = null

    fun setData(mData: List<PrivacyFunBean>) {
        this.mData = mData
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReplaceVieHolder {
        return ReplaceVieHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.real_tile_item_view, parent, false)
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
    var tvTime: TextView? = null
    var tvFunctionName: TextView? = null
    var data: PrivacyFunBean? = null

    constructor(itemView: View) : super(itemView) {
        tvTime = itemView.findViewById(R.id.time_tv)
        tvFunctionName = itemView.findViewById(R.id.function_name_tv)
        itemView.setOnClickListener {
            var builder = AlertDialog.Builder(itemView.context)
            builder.setMessage(data?.buildStackTrace())
            builder.setTitle("调用堆栈")
            builder.setPositiveButton("确定") { _, _ -> }
            builder.create().show()
        }
    }

    fun bindData(data: PrivacyFunBean) {
        this.data = data
        tvTime?.text = data.appendTime
        tvFunctionName?.text = data.funAlias
    }
}
