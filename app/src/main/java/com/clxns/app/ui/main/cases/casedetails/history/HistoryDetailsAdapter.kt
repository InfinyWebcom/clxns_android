package com.clxns.app.ui.main.cases.casedetails.history

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.clxns.app.data.model.HistoryData
import com.clxns.app.databinding.HistoryItemBinding
import com.clxns.app.utils.convertServerDateToNormal
import com.clxns.app.utils.convertToCurrency
import com.clxns.app.utils.hide
import com.clxns.app.utils.show
import com.google.gson.JsonParser

class HistoryDetailsAdapter(private val context: Context, private val dataList: List<HistoryData>) :
    RecyclerView.Adapter<HistoryDetailsAdapter.HistoryVH>() {
    class HistoryVH(itemBinding: HistoryItemBinding) :
        RecyclerView.ViewHolder(itemBinding.root) {
        var historyItemBinding: HistoryItemBinding = itemBinding

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryVH {
        return HistoryVH(HistoryItemBinding.inflate(LayoutInflater.from(parent.context)))
    }

    override fun onBindViewHolder(holder: HistoryVH, position: Int) {
        val data = dataList[position]

        holder.historyItemBinding.followUpDateTv.text = data.followUp
        holder.historyItemBinding.itemTimeTv.text =
            data.updatedAt.convertServerDateToNormal("hh:mm a")

        if (data.dispositions != null) {
            holder.historyItemBinding.tvStatusTitle.text = data.dispositions.name
        }

        if (data.subDisposition != null) {
            holder.historyItemBinding.tvSubStatus.text = data.subDisposition.name
        }
        holder.historyItemBinding.itemRemarksTv.text = data.comments
        if (data.additionalField.contains("recoveredAmount")) {
            holder.historyItemBinding.itemAmountTv.show()
            val jsonParser = JsonParser()
            val asString = jsonParser.parse(data.additionalField).asString
            val amountAsObject = jsonParser.parse(asString).asJsonObject
            holder.historyItemBinding.itemAmountTv.text =
                if (amountAsObject.get("recoveredAmount").asString == ""||amountAsObject.get("recoveredAmount").asString == null) "-" else
                    amountAsObject.get("recoveredAmount").asString.toInt().convertToCurrency()
        } else {
            holder.historyItemBinding.itemAmountTv.hide()
        }


    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    override fun getItemCount(): Int {
        return dataList.size
    }
}