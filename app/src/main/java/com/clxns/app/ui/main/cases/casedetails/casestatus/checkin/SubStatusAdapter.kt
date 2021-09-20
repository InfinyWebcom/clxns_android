package com.clxns.app.ui.main.cases.casedetails.casestatus.checkin

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.clxns.app.data.model.SubStatusModel
import com.clxns.app.databinding.SubStatusItemBinding

class SubStatusAdapter(
    private val context: Context,
    private val subStatusList: ArrayList<SubStatusModel>,
    private val actionListener: OnSubStatusActionListener
) :
    RecyclerView.Adapter<SubStatusAdapter.SubStatusVH>() {

    interface OnSubStatusActionListener {
        fun openSubStatusActionBottomSheet()
    }
    class SubStatusVH(itemView: SubStatusItemBinding) : RecyclerView.ViewHolder(itemView.root) {
        val subStatusItemBinding = itemView
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SubStatusVH {
        return SubStatusVH(SubStatusItemBinding.inflate(LayoutInflater.from(parent.context)))
    }

    override fun onBindViewHolder(holder: SubStatusVH, position: Int) {
        holder.subStatusItemBinding.subStatusText.text = subStatusList[position].status

        holder.subStatusItemBinding.subStatusItemParent.setOnClickListener {
            actionListener.openSubStatusActionBottomSheet()
        }
    }

    override fun getItemCount(): Int {
        return subStatusList.size
    }
}