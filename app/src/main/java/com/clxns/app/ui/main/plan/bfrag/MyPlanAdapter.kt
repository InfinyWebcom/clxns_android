package com.clxns.app.ui.main.plan.bfrag

import android.app.DatePickerDialog
import android.content.Context
import android.content.DialogInterface
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
import com.clxns.app.utils.copyToClipBoard
import java.util.*

class MyPlanAdapter(
    private val context: Context,
    private val contactList: List<MyPlanDataItem?>?
) : RecyclerView.Adapter<MyPlanAdapter.TempVH2>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TempVH2 {
        return TempVH2(PlanListItemsBinding.inflate(LayoutInflater.from(parent.context)))
    }

    override fun onBindViewHolder(holder: TempVH2, position: Int) {
        val details = contactList?.get(position)

        holder.contactItemBinding.planContactNameTxt.text = details?.lead?.name
        holder.contactItemBinding.planContactAmountTxt.text =
            "₹ ${details?.lead?.totalDueAmount.toString()}"
        holder.contactItemBinding.planContactBank.text = details?.lead?.chequeBank
        holder.contactItemBinding.planStatusBagde.text = details?.lead?.paymentStatus
        holder.contactItemBinding.planContactAddress.text = details?.lead?.address
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            holder.contactItemBinding.planCallBtn.tooltipText =
                details?.lead?.applicantAlternateMobile1
        }
        holder.contactItemBinding.planCallBtn.setOnLongClickListener {
            context.copyToClipBoard(details?.lead?.applicantAlternateMobile1.toString())
            Toast.makeText(context, "Number has been copied.", Toast.LENGTH_LONG)
                .show()
            false
        }
        holder.contactItemBinding.planCallBtn.setOnClickListener {
            val intent = Intent(Intent.ACTION_DIAL)
            intent.data = Uri.parse("tel:${details?.lead?.applicantAlternateMobile1}")
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
            context.startActivity(intent)
        }
    }

    private fun openDatePickerDialog() {
        val cal = Calendar.getInstance()
        val dateSetListener =
            DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
                cal.set(Calendar.YEAR, year)
                cal.set(Calendar.MONTH, monthOfYear)
                cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            }
        val datePicker = DatePickerDialog(
            context,
            dateSetListener,
            // set DatePickerDialog to point to today's date when it loads up
            cal.get(Calendar.YEAR),
            cal.get(Calendar.MONTH),
            cal.get(Calendar.DAY_OF_MONTH)
        )

        datePicker.setButton(
            DialogInterface.BUTTON_POSITIVE,
            "Follow Up"
        ) { _, _ ->
            Toast.makeText(context, "Follow up has been sent.", Toast.LENGTH_LONG).show()
        }
        datePicker.show()
    }


    override fun getItemCount(): Int {
        return contactList?.size!!
    }

    class TempVH2(itemView: PlanListItemsBinding) : RecyclerView.ViewHolder(itemView.root) {
        val contactItemBinding = itemView
    }
}