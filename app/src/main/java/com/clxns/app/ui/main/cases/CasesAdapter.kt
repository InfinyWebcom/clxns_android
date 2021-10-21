package com.clxns.app.ui.main.cases

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.clxns.app.R
import com.clxns.app.data.model.cases.CasesData
import com.clxns.app.databinding.CasesListItemBinding
import com.clxns.app.utils.*


class CasesAdapter(
    private val context: Context,
    private var casesList: List<CasesData>,
    private val onCaseItemClickListener: OnCaseItemClickListener
) :
    RecyclerView.Adapter<CasesAdapter.CasesVH>() {


    interface OnCaseItemClickListener {
        fun onPlanClick(isPlanned: Boolean, casesData: CasesData)
        fun openDetailActivity(
            loadId: String,
            name: String,
            dispositions: String,
            isPlanned: Boolean
        )
    }

    class CasesVH(
        itemView: CasesListItemBinding,
        private val onCaseItemClickListener: OnCaseItemClickListener
    ) : RecyclerView.ViewHolder(itemView.root) {
        private val contactBinding = itemView
        fun bind(context: Context, casesData: CasesData) {
            val name = casesData.name.lowercase()
            contactBinding.casesUsernameTv.text = name.makeFirstLetterCapital()
            var dispositions = "New Lead"
            if (casesData.disposition != null) {
                dispositions = casesData.disposition.name
                if (casesData.subDisposition != null) {
                    dispositions += context.getString(R.string.arrow_forward) +
                            casesData.subDisposition.name
                }
            }
            val assignedDate = casesData.fosAssignedDate?.convertServerDateToNormal("dd, MMM yyyy")
            contactBinding.casesStatusTv.text = dispositions
            val loanIdDatePinCode = casesData.loanAccountNo.toString() + " | " +
                    casesData.applicantPincode.toString() + " | " + assignedDate
            contactBinding.loanIdDatePincodeTv.text = loanIdDatePinCode

            val amount = casesData.totalDueAmount - casesData.amountCollected
            contactBinding.casesAmountTv.text = amount.convertToCurrency()
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
                onCaseItemClickListener.openDetailActivity(
                    casesData.loanAccountNo.toString(),
                    name,
                    dispositions,
                    isPlanned
                )
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