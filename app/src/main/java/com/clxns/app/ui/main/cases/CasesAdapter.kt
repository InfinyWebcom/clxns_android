package com.clxns.app.ui.main.cases

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.clxns.app.R
import com.clxns.app.data.model.cases.CasesData
import com.clxns.app.databinding.CasesListItemBinding
import com.clxns.app.ui.main.cases.casedetails.DetailsActivity
import com.clxns.app.utils.*


class CasesAdapter(
    private val context: Context,
    private var casesList: List<CasesData>,
    private val onCaseItemClickListener: OnCaseItemClickListener
) :
    RecyclerView.Adapter<CasesAdapter.CasesVH>() {


    interface OnCaseItemClickListener {
        fun onPlanClick(isPlanned: Boolean, casesData: CasesData)
    }

    class CasesVH(
        itemView: CasesListItemBinding,
        private val onCaseItemClickListener: OnCaseItemClickListener
    ) : RecyclerView.ViewHolder(itemView.root) {
        private val contactBinding = itemView
        fun bind(context: Context, casesData: CasesData) {
            val name = casesData.name.lowercase()
            contactBinding.casesUsernameTv.text = name.makeFirstLetterCapital()
            var eventStatus = "New Lead"
            if (casesData.disposition != null) {
                eventStatus = casesData.disposition.name
                if (casesData.subDisposition != null) {
                    eventStatus += " -> " + casesData.subDisposition.name
                }
            }
            val assignedDate = casesData.fosAssignedDate.convertServerDateToNormal()
            contactBinding.casesStatusTv.text = eventStatus
            val loanIdDatePinCode = casesData.loanAccountNo.toString() + " | " +
                    casesData.applicantPincode.toString() + " | " + assignedDate
            contactBinding.loanIdDatePincodeTv.text = loanIdDatePinCode
            contactBinding.casesAmountTv.text = casesData.totalDueAmount.convertToCurrency()
            if (!casesData.fi?.fiImage.isNullOrEmpty()) {
                val url = Constants.BANK_LOGO_URL + casesData.fi?.fiImage
                contactBinding.casesImage.loadImage(url)
            }
            val isPlanned: Boolean
            if (casesData.plans.isNotEmpty()) {
                isPlanned = true
                contactBinding.casesPlanBtn.backgroundTintList =
                    ContextCompat.getColorStateList(context, R.color.light_red)
                contactBinding.casesPlanBtn.text = context.getString(R.string.un_plan)
            } else {
                isPlanned = false
                contactBinding.casesPlanBtn.backgroundTintList =
                    ContextCompat.getColorStateList(context, R.color.green)
                contactBinding.casesPlanBtn.text = context.getString(R.string.plan)
            }
            contactBinding.casesPlanBtn.setOnClickListener {
                onCaseItemClickListener.onPlanClick(isPlanned, casesData)

            }
            contactBinding.casesCardView.setOnClickListener {

                val intent = Intent(context, DetailsActivity::class.java)
                intent.putExtra("loan_account_number", casesData.loanAccountNo.toString())
                intent.putExtra("status", eventStatus)
                intent.putExtra("name",name)
                intent.putExtra("isPlanned", isPlanned)
                intent.putExtra("isCaseDetail", true)
                context.startActivity(intent)

            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CasesVH {
        return CasesVH(
            CasesListItemBinding.inflate(LayoutInflater.from(parent.context)),
            onCaseItemClickListener
        )
    }

    override fun onBindViewHolder(holder: CasesVH, position: Int) {
        val listItem = casesList[position]
        holder.bind(context, listItem)

    }

    override fun getItemCount(): Int {
        return casesList.size
    }
}