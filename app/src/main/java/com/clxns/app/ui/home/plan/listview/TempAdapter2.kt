package com.clxns.app.ui.home.plan.listview

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
import com.clxns.app.data.model.UserDetails
import com.clxns.app.databinding.PlanListItemsBinding
import com.clxns.app.ui.home.plan.DetailsActivity
import com.clxns.app.utils.copyToClipBoard
import java.util.*

class TempAdapter2(
    private val context: Context,
    private val contactList: MutableList<UserDetails>
) :
    RecyclerView.Adapter<TempAdapter2.TempVH2>() {
    private lateinit var cal: Calendar


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TempVH2 {
        return TempVH2(PlanListItemsBinding.inflate(LayoutInflater.from(parent.context)))
    }

    override fun onBindViewHolder(holder: TempVH2, position: Int) {
        val details = contactList[position]

        holder.contactItemBinding.planContactNameTxt.text = details.name
        holder.contactItemBinding.planContactAmountTxt.text = details.amount
        holder.contactItemBinding.planContactBank.text = details.pinCodeCityDate
        holder.contactItemBinding.planStatusBagde.text = details.status
        holder.contactItemBinding.planContactAddress.text = details.address
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            holder.contactItemBinding.planCallBtn.tooltipText = details.mobileNumber
        }
        holder.contactItemBinding.planCallBtn.setOnLongClickListener {
            context.copyToClipBoard(details.mobileNumber.toString())
            Toast.makeText(context, "Number has been copied.", Toast.LENGTH_LONG)
                .show()
            false
        }
        holder.contactItemBinding.planCallBtn.setOnClickListener {
            val intent = Intent(Intent.ACTION_DIAL)
            intent.data = Uri.parse("tel:${details.mobileNumber}")
            context.startActivity(intent)
        }

        holder.contactItemBinding.planFollowUpBtn.setOnClickListener {
            openDatePickerDialog()
        }

        holder.contactItemBinding.planCheckInBtn.setOnClickListener {
            val intent = Intent(context, DetailsActivity::class.java)
            intent.putExtra("name", details.name)
            intent.putExtra("amount", details.amount)
            intent.putExtra("mobile_number", details.mobileNumber)
            intent.putExtra("address", details.address)
            intent.putExtra("status", details.status)
            intent.putExtra("bank_name", details.pinCodeCityDate)
            intent.putExtra("loan_id", details.loanID)
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
        return contactList.size
    }

    class TempVH2(itemView: PlanListItemsBinding) : RecyclerView.ViewHolder(itemView.root) {
        val contactItemBinding = itemView
    }
}