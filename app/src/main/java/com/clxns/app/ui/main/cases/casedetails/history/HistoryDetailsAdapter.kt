package com.clxns.app.ui.main.cases.casedetails.history

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.clxns.app.R
import com.clxns.app.data.model.HistoryData
import com.clxns.app.databinding.HistoryItemBinding
import com.clxns.app.utils.convertServerDateToNormal
import com.clxns.app.utils.convertToCurrency
import timber.log.Timber

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

        holder.historyItemBinding.followUpDateTv.text =
            data.updatedAt.convertServerDateToNormal("dd, MMM, yy")
        holder.historyItemBinding.itemTimeTv.text =
            data.updatedAt.convertServerDateToNormal("hh:mm a")

        if (data.dispositions != null) {
            holder.historyItemBinding.tvStatusTitle.text = data.dispositions.name
        }

        var followUpDate = ""
        if (data.followUp != null) {
            followUpDate =
                "Follow Up Date :  " + data.followUp.convertServerDateToNormal("dd, MMM yyyy")
        }
        if (data.subDisposition != null) {
            var subStatus = data.subDisposition.name
            if (followUpDate.isNotBlank()) {
                subStatus += "\n$followUpDate"
            }
            holder.historyItemBinding.tvSubStatus.text = subStatus
        } else {
            holder.historyItemBinding.tvSubStatus.text = followUpDate
        }
        var additionalInfo = data.comments
        if (data.paymentData.isNotEmpty()) {
            additionalInfo += "\n${data.paymentData[0].refNo} ${data.paymentData[0].collectedAmt.convertToCurrency()}"
        }
        holder.historyItemBinding.itemRemarksTv.text = additionalInfo
        if (data.additionalField != null) {

            //Removing all the occurrences of backslash
            val prob = data.additionalField.replace("\\", "")
            Timber.i(prob)
            when {
                prob.contains("80% >") || prob.contains("80% u003e") -> {
                    holder.historyItemBinding.ptpProbabilityDot.imageTintList =
                        ContextCompat.getColorStateList(context, R.color.green)
                }
                prob.contains("50% - 80%") -> {
                    holder.historyItemBinding.ptpProbabilityDot.imageTintList =
                        ContextCompat.getColorStateList(context, R.color.quantum_orange)
                }
                prob.contains("50% <") || prob.contains("50% u003c") -> {
                    holder.historyItemBinding.ptpProbabilityDot.imageTintList =
                        ContextCompat.getColorStateList(context, R.color.light_red)
                }
            }
        }
        /*if (data.additionalField?.contains("ptpProbability") == true) {
            val jsonParser = JsonParser()
            val asString = jsonParser.parse(data.additionalField).asString

            val amountAsObject = jsonParser.parse(asString).asJsonObject

            val prob = amountAsObject.get("ptpProbability").asString
            when {
                prob.equals("80% >") -> {
                    holder.historyItemBinding.ptpProbabilityDot.imageTintList =
                        ContextCompat.getColorStateList(context, R.color.green)
                }
                prob.equals("50% - 80%") -> {
                    holder.historyItemBinding.ptpProbabilityDot.imageTintList =
                        ContextCompat.getColorStateList(context, R.color.quantum_orange)
                }
                prob.equals("50% <") -> {
                    holder.historyItemBinding.ptpProbabilityDot.imageTintList =
                        ContextCompat.getColorStateList(context, R.color.light_red)
                }
            }
        }*/


    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    override fun getItemCount(): Int {
        return dataList.size
    }
}