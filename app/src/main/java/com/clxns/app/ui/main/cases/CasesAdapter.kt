package com.clxns.app.ui.main.cases

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.clxns.app.data.model.cases.CasesData
import com.clxns.app.databinding.CasesListItemBinding
import com.clxns.app.ui.main.cases.casedetails.DetailsActivity
import com.clxns.app.utils.Constants
import com.clxns.app.utils.convertToCurrency
import com.clxns.app.utils.loadImage
import com.clxns.app.utils.makeFirstLetterCapital


class CasesAdapter(
    private val context: Context,
    private var casesList: List<CasesData>,
    private val casesListener: (CasesData) -> Unit
) :
    RecyclerView.Adapter<CasesAdapter.CasesVH>() {

    class CasesVH(itemView: CasesListItemBinding) : RecyclerView.ViewHolder(itemView.root) {
        private val contactBinding = itemView
        fun bind(context: Context, casesData: CasesData, casesListener: (CasesData) -> Unit) {
            contactBinding.casesUsernameTv.text = casesData.name.makeFirstLetterCapital()
            contactBinding.casesStatusTv.text = "Pending"
            val loanIdDatePinCode = casesData.loanAccountNo.toString() + " | " + casesData.applicantPincode.toString() + " | " + casesData.fosAssignedDate.subSequence(0,10)
            contactBinding.loanIdDatePincodeTv.text = loanIdDatePinCode
            contactBinding.casesAmountTv.text = casesData.totalDueAmount.convertToCurrency()
            if (!casesData.fi?.fiImage.isNullOrEmpty()) {
                val url = Constants.BANK_LOGO_URL + casesData.fi?.fiImage
                contactBinding.casesImage.loadImage(url)
            }

            contactBinding.casesPlanBtn.setOnClickListener {
                casesListener.invoke(casesData)
            }
            contactBinding.casesCardView.setOnClickListener {

                val intent = Intent(context, DetailsActivity::class.java)
                intent.putExtra("loan_account_number", casesData.loanAccountNo.toString())
                intent.putExtra("isPlanned", false)
                context.startActivity(intent)

            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CasesVH {
        return CasesVH(CasesListItemBinding.inflate(LayoutInflater.from(parent.context)))
    }

    override fun onBindViewHolder(holder: CasesVH, position: Int) {
        val listItem = casesList[position]
        holder.bind(context, listItem, casesListener)

    }

    override fun getItemCount(): Int {
        return casesList.size
    }
}