package com.clxns.app.ui.main.cases.caseDetails.caseStatus.checkIn

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.clxns.app.data.model.StatusModel
import com.clxns.app.databinding.StatusItemsBinding
import com.clxns.app.utils.preventDoubleClick


class StatusAdapter(
    private val context: Context,
    private val statusList: ArrayList<StatusModel>,
    private val onStatusListener: OnStatusListener
) :
    RecyclerView.Adapter<StatusAdapter.StatusVH>() {

    interface OnStatusListener {
        fun openAddDetailsBottomSheet(isMobile: Boolean)
//        fun openSubStatusBottomSheet()
        fun openSubStatusActionBottomSheet(isPTPAction: Boolean, dispositionType: String)
        fun openPaymentScreen(dispositionType: String)
    }

    class StatusVH(itemView: StatusItemsBinding) : RecyclerView.ViewHolder(itemView.root) {
        val itemsBinding = itemView
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StatusVH {
        return StatusVH(StatusItemsBinding.inflate(LayoutInflater.from(parent.context)))
    }

    override fun onBindViewHolder(holder: StatusVH, position: Int) {
        val statusDetails = statusList[position]
        holder.itemsBinding.statusCard.setOnClickListener {
            it.preventDoubleClick()
            when {
                holder.itemsBinding.statusTxt.text.equals("Add Mobile") -> {
                    onStatusListener.openAddDetailsBottomSheet(true)
                }
                holder.itemsBinding.statusTxt.text.equals("Add Address") -> {
                    onStatusListener.openAddDetailsBottomSheet(false)
                }
                holder.itemsBinding.statusTxt.text.equals("PTP") -> {
                    onStatusListener.openSubStatusActionBottomSheet(
                        true,
                        holder.itemsBinding.statusTxt.text.toString()
                    )
                }
                holder.itemsBinding.statusTxt.text.equals("Call Back") ||
                        holder.itemsBinding.statusTxt.text.equals("Dispute") ||
                        holder.itemsBinding.statusTxt.text.equals("Broken PTP") ||
                        holder.itemsBinding.statusTxt.text.equals("RTP") ||
                        holder.itemsBinding.statusTxt.text.equals("Customer Not Found") ||
                        holder.itemsBinding.statusTxt.text.equals("Customer Deceased") ||
                        holder.itemsBinding.statusTxt.text.equals("Settlement/Foreclosure")
                -> {
                    onStatusListener.openSubStatusActionBottomSheet(
                        false,
                        holder.itemsBinding.statusTxt.text.toString()
                    )
                }
                holder.itemsBinding.statusTxt.text.equals("Collect") ||
                        holder.itemsBinding.statusTxt.text.equals("Partially Collect")
                -> {
                    onStatusListener.openPaymentScreen(holder.itemsBinding.statusTxt.text.toString())
                }
//                holder.itemsBinding.statusTxt.text.equals("Customer Not Found") -> {
//                    onStatusListener.openSubStatusBottomSheet()
//                }
            }
        }
        holder.itemsBinding.statusTxt.text = statusDetails.status
        holder.itemsBinding.statusTxt.setCompoundDrawablesRelativeWithIntrinsicBounds(
            null,
            statusDetails.icon,
            null,
            null
        )
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    override fun getItemCount(): Int {
        return statusList.size
    }
}