package com.clxns.app.ui.cases

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.clxns.app.data.model.CasesData
import com.clxns.app.databinding.CasesListItemBinding
import java.text.NumberFormat
import java.util.*


class CasesAdapter(private val context: Context, private var casesList: List<CasesData>) :
    RecyclerView.Adapter<CasesAdapter.CasesVH>() {

    class CasesVH(itemView: CasesListItemBinding) : RecyclerView.ViewHolder(itemView.root) {
        val contactBinding = itemView
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CasesVH {
        return CasesVH(CasesListItemBinding.inflate(LayoutInflater.from(parent.context)))
    }

    override fun onBindViewHolder(holder: CasesVH, position: Int) {
        val listItem = casesList[position]
        holder.contactBinding.casesUsernameTv.text = listItem.name
        holder.contactBinding.casesStatusTv.text = listItem.paymentStatus
        holder.contactBinding.loadAccountNoTv.text = listItem.loanAccountNo.toString()
        val amount = NumberFormat.getCurrencyInstance(Locale("en", "in")).format(listItem.totalDueAmount)
        holder.contactBinding.casesAmountTv.text = amount.substringBefore('.')

    }

    override fun getItemCount(): Int {
       return casesList.size
    }
}