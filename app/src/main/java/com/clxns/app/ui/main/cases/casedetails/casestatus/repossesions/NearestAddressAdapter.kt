package com.clxns.app.ui.main.cases.casedetails.casestatus.repossesions

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.clxns.app.R


class NearestAddressAdapter() : RecyclerView.Adapter<NearestAddressAdapter.NearestAddressVH>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NearestAddressVH {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.nearest_address_list_item, parent, false)
        return NearestAddressVH(v)
    }

    override fun onBindViewHolder(holder: NearestAddressVH, position: Int) {

    }

    override fun getItemCount(): Int {
        return 3
    }


    class NearestAddressVH(itemView: View) :
        RecyclerView.ViewHolder(itemView) {
        //var nearestAddressListCardItemBinding: NearestAddressListItemBinding = itemView
    }
}

