package com.clxns.app.ui.main.plan.bfrag

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.clxns.app.data.model.MyPlanDataItem
import com.clxns.app.databinding.PlanListItemsBinding
import com.clxns.app.ui.main.cases.casedetails.DetailsActivity
import com.clxns.app.utils.convertToCurrency
import com.clxns.app.utils.copyToClipBoard
import com.clxns.app.utils.makeFirstLetterCapital

class MyPlanAdapter(
    private val context: Context,
    private val contactList: List<MyPlanDataItem?>?
) : RecyclerView.Adapter<MyPlanAdapter.MyPlanVH>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyPlanVH {
        return MyPlanVH(PlanListItemsBinding.inflate(LayoutInflater.from(parent.context)))
    }

    override fun onBindViewHolder(holder: MyPlanVH, position: Int) {
        val details = contactList?.get(position)

        val name = details?.lead?.name?.lowercase()?.makeFirstLetterCapital()
        holder.contactItemBinding.planItemNameTxt.text = name
        val amount = details?.lead?.totalDueAmount?.convertToCurrency()
        holder.contactItemBinding.planItemAmountTxt.text = amount
        var bankNameAndLoanId = " | " + details?.lead?.loanAccountNo.toString()
        bankNameAndLoanId = if (!details?.lead?.fiData?.name.isNullOrEmpty()) {
            details?.lead?.fiData?.name + bankNameAndLoanId
        } else {
            "-$bankNameAndLoanId"
        }

        holder.contactItemBinding.planBankNameAndLoanId.text = bankNameAndLoanId
        var status = "New Lead"
        if (details?.lead?.dispositionId != null) {
            status = details.lead.dispositionId.toString()
            if (details.lead.subDispositionId != null) {
                status += "[" + details.lead.subDispositionId.toString() + "]"
            }
        }
        holder.contactItemBinding.planStatusBagde.text = status

        holder.contactItemBinding.planContactAddress.text =
            details?.lead?.address?.lowercase()?.makeFirstLetterCapital()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            holder.contactItemBinding.planCallBtn.tooltipText =
                details?.lead?.applicantAlternateMobile1
        }
        holder.contactItemBinding.planCallBtn.setOnLongClickListener {
            context.copyToClipBoard(details?.lead?.phone.toString())
            Toast.makeText(context, "Number has been copied.", Toast.LENGTH_LONG)
                .show()
            false
        }
        holder.contactItemBinding.planCallBtn.setOnClickListener {
            val intent = Intent(Intent.ACTION_DIAL)
            intent.data = Uri.parse("tel:${details?.lead?.phone}")
            context.startActivity(intent)
        }

        holder.contactItemBinding.map.setOnClickListener {
//            openDatePickerDialog()
            val map = "http://maps.google.co.in/maps?q=${details?.lead?.address}"
            // val uri = String.format(Locale.ENGLISH, "geo:%f,%f", details?.lead?.address)
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(map))
            context.startActivity(intent)
        }

        holder.contactItemBinding.planCheckInBtn.setOnClickListener {
            val intent = Intent(context, DetailsActivity::class.java)
            intent.putExtra("loan_account_number", details?.lead?.loanAccountNo.toString())
            intent.putExtra("isPlanned", true)
            intent.putExtra("status", status)
            intent.putExtra("name", name)
            context.startActivity(intent)
        }
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    override fun getItemCount(): Int {
        return contactList?.size!!
    }

    class MyPlanVH(itemView: PlanListItemsBinding) : RecyclerView.ViewHolder(itemView.root) {
        val contactItemBinding = itemView
    }
}