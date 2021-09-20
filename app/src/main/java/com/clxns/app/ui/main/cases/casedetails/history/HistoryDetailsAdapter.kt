package com.clxns.app.ui.main.cases.casedetails.history

import android.content.Context
import android.graphics.PorterDuff
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.clxns.app.R
import com.clxns.app.databinding.HistoryItemBinding

class HistoryDetailsAdapter(var context: Context) :
    RecyclerView.Adapter<HistoryDetailsAdapter.MyViewHolder>() {
    class MyViewHolder(itemBinding: HistoryItemBinding) :
        RecyclerView.ViewHolder(itemBinding.root) {

        var historyItemBinding: HistoryItemBinding = itemBinding

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder(HistoryItemBinding.inflate(LayoutInflater.from(parent.context)))
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        /*var status: String = orderDetail.getOrderStatus().get(position).getStatus()
        when (status) {
            "New" -> {
                status = "Placed"
                holder.historyItemBinding.tvStatusTitle.setTextColor(context.getResources().getColor(R.color.red))
                holder.historyItemBinding.imageTrack.setColorFilter(ContextCompat.getColor(context, R.color.red),
                    PorterDuff.Mode.SRC_IN)
            }
            "In Process" -> {
                holder.historyItemBinding.tvStatusTitle.setTextColor(context.getResources()
                    .getColor(R.color.colorPrimary))
                holder.historyItemBinding.imageTrack.setColorFilter(ContextCompat.getColor(context,
                    R.color.colorPrimary), PorterDuff.Mode.SRC_IN)
            }
            "Others" -> {
                holder.historyItemBinding.tvStatusTitle.setTextColor(context.getResources()
                    .getColor(R.color.yellow_dark))
                holder.historyItemBinding.imageTrack.setColorFilter(ContextCompat.getColor(context,
                    R.color.yellow_dark), PorterDuff.Mode.SRC_IN)
            }
            "Cancelled" -> {
                holder.historyItemBinding.tvStatusTitle.setTextColor(context.getResources()
                    .getColor(R.color.text_black_cancelled))
                holder.imageTrack.setColorFilter(ContextCompat.getColor(context,
                    R.color.text_black_cancelled), PorterDuff.Mode.SRC_IN)
            }
            "Processed" -> {
                holder.historyItemBinding.tvStatusTitle.setTextColor(context.getResources()
                    .getColor(R.color.colorPrimary))
                holder.imageTrack.setColorFilter(ContextCompat.getColor(context,
                    R.color.colorPrimary), PorterDuff.Mode.SRC_IN)
            }
            "Delivered" -> {
                holder.historyItemBinding.tvStatusTitle.setTextColor(context.getResources()
                    .getColor(R.color.dark_green))
                holder.historyItemBinding.imageTrack.setColorFilter(ContextCompat.getColor(context,
                    R.color.dark_green), PorterDuff.Mode.SRC_IN)
            }
        }

        holder.historyItemBinding.tvStatusTitle.setText(status)*/



        when (position) {
            0 -> {
                holder.historyItemBinding.tvStatusTitle.setTextColor(
                    ContextCompat.getColor(
                        context,
                        R.color.green
                    )
                )
                holder.historyItemBinding.imageTrack.setColorFilter(
                    ContextCompat.getColor(context, R.color.green),
                    PorterDuff.Mode.SRC_IN
                )
                holder.historyItemBinding.tvStatusTitle.text = "Open"
                holder.historyItemBinding.tvDateDetailTrack.text = "12/06/2021"
                holder.historyItemBinding.statusDetails.text = "New case has been added"
                holder.historyItemBinding.tvSubStatus.text = "New"
            }
            1 -> {
                holder.historyItemBinding.tvStatusTitle.setTextColor(
                    ContextCompat.getColor(
                        context,
                        R.color.green
                    )
                )
                holder.historyItemBinding.imageTrack.setColorFilter(
                    ContextCompat.getColor(context, R.color.green),
                    PorterDuff.Mode.SRC_IN
                )
                holder.historyItemBinding.tvStatusTitle.text = "PTP"
                holder.historyItemBinding.tvDateDetailTrack.text = "15/06/2021"
                holder.historyItemBinding.statusDetails.text = "User promised to pay within a week"
                holder.historyItemBinding.tvSubStatus.text = "Active"
            }
            2 -> {
                holder.historyItemBinding.tvStatusTitle.setTextColor(
                    ContextCompat.getColor(
                        context,
                        R.color.green
                    )
                )
                holder.historyItemBinding.imageTrack.setColorFilter(
                    ContextCompat.getColor(context, R.color.green),
                    PorterDuff.Mode.SRC_IN
                )
                holder.historyItemBinding.tvStatusTitle.text = "Customer Not Found"
                holder.historyItemBinding.tvDateDetailTrack.text = "22/06/2021"
                holder.historyItemBinding.statusDetails.text = "The house was locked"
                holder.historyItemBinding.tvSubStatus.text = "House Locked"

            }
            3 -> {
                holder.historyItemBinding.tvStatusTitle.setTextColor(
                    ContextCompat.getColor(
                        context,
                        R.color.green
                    )
                )
                holder.historyItemBinding.imageTrack.setColorFilter(
                    ContextCompat.getColor(context, R.color.green),
                    PorterDuff.Mode.SRC_IN
                )
                holder.historyItemBinding.tvStatusTitle.text = "RTP"
                holder.historyItemBinding.tvDateDetailTrack.text = "25/06/2021"
                holder.historyItemBinding.statusDetails.text = "User has refused to pay due to financial problem."
                holder.historyItemBinding.tvSubStatus.text = "Active"

            }
            4 -> {
                holder.historyItemBinding.tvStatusTitle.setTextColor(
                    ContextCompat.getColor(
                        context,
                        R.color.green
                    )
                )
                holder.historyItemBinding.imageTrack.setColorFilter(
                    ContextCompat.getColor(context, R.color.green),
                    PorterDuff.Mode.SRC_IN
                )
                holder.historyItemBinding.tvStatusTitle.text = "Call Back"
                holder.historyItemBinding.tvDateDetailTrack.text = "30/06/2021"
                holder.historyItemBinding.statusDetails.text = "Revisited user to remind him about the payment."
                holder.historyItemBinding.tvSubStatus.text = "Revisit@05/09/2021 - 6:45 PM"

            }
            5 -> {
                holder.historyItemBinding.tvStatusTitle.setTextColor(
                    ContextCompat.getColor(
                        context,
                        R.color.green
                    )
                )
                holder.historyItemBinding.imageTrack.setColorFilter(
                    ContextCompat.getColor(context, R.color.green),
                    PorterDuff.Mode.SRC_IN
                )
                holder.historyItemBinding.tvStatusTitle.text = "Customer Not Found"
                holder.historyItemBinding.tvDateDetailTrack.text = "03/07/2021"
                holder.historyItemBinding.statusDetails.text = "The user has been shifted to different location. PS - Address updated."
                holder.historyItemBinding.tvSubStatus.text = "Residence Shifted"
            }
            6 -> {
                holder.historyItemBinding.tvStatusTitle.setTextColor(
                    ContextCompat.getColor(
                        context,
                        R.color.green
                    )
                )
                holder.historyItemBinding.imageTrack.setColorFilter(
                    ContextCompat.getColor(context, R.color.green),
                    PorterDuff.Mode.SRC_IN
                )
                holder.historyItemBinding.tvStatusTitle.text = "Customer Not Found"
                holder.historyItemBinding.tvDateDetailTrack.text = "07/07/2021"
                holder.historyItemBinding.statusDetails.text = "The user has left a message to family member that he'll be returning in a week.The user has left a message to family member that he'll be returning in a week.The user has left a message to family member that he'll be returning in a week.The user has left a message to family member that he'll be returning in a week.The user has left a message to family member that he'll be returning in a week.The user has left a message to family member that he'll be returning in a week.The user has left a message to family member that he'll be returning in a week.The user has left a message to family member that he'll be returning in a week.The user has left a message to family member that he'll be returning in a week.The user has left a message to family member that he'll be returning in a week.The user has left a message to family member that he'll be returning in a week.The user has left a message to family member that he'll be returning in a week.The user has left a message to family member that he'll be returning in a week."
                holder.historyItemBinding.tvSubStatus.text = "Left Message with the Family"
            }
            7 -> {
                holder.historyItemBinding.tvStatusTitle.setTextColor(
                    ContextCompat.getColor(
                        context,
                        R.color.green
                    )
                )
                holder.historyItemBinding.imageTrack.setColorFilter(
                    ContextCompat.getColor(context, R.color.green),
                    PorterDuff.Mode.SRC_IN
                )
                holder.historyItemBinding.tvStatusTitle.text = "Dispute"
                holder.historyItemBinding.tvDateDetailTrack.text = "15/07/2021"
                holder.historyItemBinding.statusDetails.text = "Amount dispute, the user has not paid full amount."
                holder.historyItemBinding.tvSubStatus.text = "Amount Dispute"
            }
            8 -> {
                holder.historyItemBinding.tvStatusTitle.setTextColor(
                    ContextCompat.getColor(
                        context,
                        R.color.green
                    )
                )
                holder.historyItemBinding.imageTrack.setColorFilter(
                    ContextCompat.getColor(context, R.color.green),
                    PorterDuff.Mode.SRC_IN
                )
                holder.historyItemBinding.tvStatusTitle.text = "Call Back"
                holder.historyItemBinding.tvDateDetailTrack.text = "18/07/2021"
                holder.historyItemBinding.statusDetails.text = "Revisited the user for the reminder and the warning."
                holder.historyItemBinding.tvSubStatus.text = "Revisit@10/09/2021 - 12:45 PM"
            }
            9 -> {
                holder.historyItemBinding.tvStatusTitle.setTextColor(
                    ContextCompat.getColor(
                        context,
                        R.color.green
                    )
                )
                holder.historyItemBinding.imageTrack.setColorFilter(
                    ContextCompat.getColor(context, R.color.green),
                    PorterDuff.Mode.SRC_IN
                )
                holder.historyItemBinding.tvStatusTitle.text = "Recovered"
                holder.historyItemBinding.tvDateDetailTrack.text = "03/07/2021"
                holder.historyItemBinding.statusDetails.text = "The user has paid full amount in cash. The case is settled."
                holder.historyItemBinding.tvSubStatus.text = "Receipt Generation"
            }
        }

    }

    override fun getItemCount(): Int {
        return 10
    }
}