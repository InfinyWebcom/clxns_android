package com.clxns.app.ui.main.cases.caseDetails

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.clxns.app.data.model.cases.UpdatedContactData
import com.clxns.app.databinding.ContactListItemBinding
import com.clxns.app.utils.makeFirstLetterCapital

class UpdatedContactAdapter(private val dataList:List<UpdatedContactData>) : RecyclerView.Adapter<UpdatedContactAdapter.UpdatedContactVH>() {
class UpdatedContactVH(itemView: ContactListItemBinding) : RecyclerView.ViewHolder(itemView.root){
    val binding = itemView
}

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UpdatedContactVH {
        return UpdatedContactVH(ContactListItemBinding.inflate(LayoutInflater.from(parent.context)))
    }

    override fun onBindViewHolder(holder: UpdatedContactVH, position: Int) {
        val data = dataList[position]
        holder.binding.contactLabel.text = data.type.makeFirstLetterCapital()
        holder.binding.contactValue.text = data.content
    }

    override fun getItemCount(): Int = dataList.size

    override fun getItemViewType(position: Int): Int {
        return position
    }
}