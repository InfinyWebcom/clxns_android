package com.clxns.app.ui.cases

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.clxns.app.data.model.CasesData
import com.clxns.app.databinding.CasesListItemBinding
import com.clxns.app.utils.makeFirstLetterCapital
import timber.log.Timber
import java.text.NumberFormat
import java.util.*


class CasesAdapter(
    private var casesList: List<CasesData>,
    private val casesListener: (CasesData) -> Unit
) :
    RecyclerView.Adapter<CasesAdapter.CasesVH>() {

    class CasesVH(itemView: CasesListItemBinding) : RecyclerView.ViewHolder(itemView.root) {
        private val contactBinding = itemView
        fun bind(casesData: CasesData, casesListener: (CasesData) -> Unit) {
            contactBinding.casesUsernameTv.text = casesData.name.makeFirstLetterCapital()
            contactBinding.casesStatusTv.text = casesData.paymentStatus.makeFirstLetterCapital()
            contactBinding.loanIdDatePincodeTv.text = casesData.loanAccountNo.toString()
            val amount = NumberFormat.getCurrencyInstance(Locale("en", "in"))
                .format(casesData.totalDueAmount)
            contactBinding.casesAmountTv.text = amount.substringBefore('.')

            contactBinding.casesPlanBtn.setOnClickListener {
                casesListener.invoke(casesData)
            }
            contactBinding.casesCardView.setOnClickListener {
                Timber.i("Clicked")
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CasesVH {
        return CasesVH(CasesListItemBinding.inflate(LayoutInflater.from(parent.context)))
    }

    override fun onBindViewHolder(holder: CasesVH, position: Int) {
        val listItem = casesList[position]
        holder.bind(listItem, casesListener)

    }

    override fun getItemCount(): Int {
        return casesList.size
    }
}