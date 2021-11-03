package com.clxns.app.ui.main.cases.caseDetails.history

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
import com.clxns.app.utils.hide
import com.clxns.app.utils.show

class HistoryDetailsAdapter(
    private val context : Context,
    private val dataList : List<HistoryData>
) :
    RecyclerView.Adapter<HistoryDetailsAdapter.HistoryVH>() {
    class HistoryVH(itemBinding : HistoryItemBinding) :
        RecyclerView.ViewHolder(itemBinding.root) {
        var historyItemBinding : HistoryItemBinding = itemBinding

    }

    override fun onCreateViewHolder(parent : ViewGroup, viewType : Int) : HistoryVH {
        return HistoryVH(HistoryItemBinding.inflate(LayoutInflater.from(parent.context)))
    }

    override fun onBindViewHolder(holder : HistoryVH, position : Int) {
        val data = dataList[position]

        holder.historyItemBinding.updatedAtDateTv.text =
            data.updatedAt.convertServerDateToNormal("dd, MMM, yy")
        holder.historyItemBinding.itemTimeTv.text =
            data.updatedAt.convertServerDateToNormal("hh:mm a", true)

        if (data.dispositions != null) {
            holder.historyItemBinding.tvStatusTitle.text = data.dispositions.name
        }

        var followUpDate = ""
        if (data.followUp != null) {
            followUpDate =
                "Follow Up Date :  " + data.followUp.convertServerDateToNormal("dd, MMM yyyy") +
                        ", Time : " + data.followUp.convertServerDateToNormal("hh:mm a")
        }
        if (data.subDisposition != null) {
            var subStatus = data.subDisposition.name
            if (followUpDate.isNotBlank()) {
                subStatus += "\n$followUpDate"
            }
            holder.historyItemBinding.tvSubStatus.text = subStatus
        } else {
            if (followUpDate.isEmpty()) {
                holder.historyItemBinding.tvSubStatus.hide()
            } else {
                holder.historyItemBinding.tvSubStatus.text = followUpDate
            }
        }
        var additionalInfo = data.comments
        if (data.paymentData.isNotEmpty()) {
//            if (data.paymentData[0].supporting.isNotBlank()){
//                holder.historyItemBinding.viewFiles.show()
//            }
            if (data.paymentData[0].recoveryDate.isNotEmpty()) {
                val recoveryDate = "Recovery Date : " +
                        data.paymentData[0].recoveryDate.convertServerDateToNormal("dd, MMM yyyy")
                holder.historyItemBinding.tvSubStatus.show()
                holder.historyItemBinding.tvSubStatus.text = recoveryDate
            }
            var refNo = "-"
            when (data.paymentData[0].paymentMode) {
                "Online" -> refNo = "Reference No: ${data.paymentData[0].refNo}"
                "Cheque" -> refNo = "Cheque No: ${data.paymentData[0].chequeNo}"
                "Cash" -> refNo = "Cash Mode"
            }
            additionalInfo += "\n$refNo" +
                    ", Amount : ${data.paymentData[0].collectedAmt.convertToCurrency()}"
        }
        holder.historyItemBinding.itemRemarksTv.text = additionalInfo
        if (data.additionalField != null) {
            //Removing all the occurrences of backslash
            val prob = data.additionalField.replace("\\", "")
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

//        holder.historyItemBinding.viewFiles.setOnClickListener {
//            val imageUrl = Constants.SUPPORTING_IMAGE_URL+ data.paymentData[0].supporting
//            showImageDialog(imageUrl)
//        }
    }


    /*private fun showImageDialog(url:String) {

        val imageDialog = MaterialAlertDialogBuilder(context)
        val linearLayout = LinearLayoutCompat(context)
        linearLayout.apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            orientation = LinearLayoutCompat.VERTICAL
            setPadding(24,24,24,24)
            gravity = Gravity.CENTER
        }

        val imageView = ImageView(context)
        imageView.layoutParams = ViewGroup.LayoutParams(300,400)
        imageView.scaleType = ImageView.ScaleType.CENTER_INSIDE
        imageView.loadImage(url)
        linearLayout.addView(imageView)

        imageDialog.apply {
            setTitle("Uploaded Files")
            setView(linearLayout)
            setPositiveButton("Close", DialogInterface.OnClickListener { dialog, _ -> dialog.dismiss()  })
        }.create().show()
    }*/

    override fun getItemViewType(position : Int) : Int {
        return position
    }

    override fun getItemCount() : Int {
        return dataList.size
    }
}