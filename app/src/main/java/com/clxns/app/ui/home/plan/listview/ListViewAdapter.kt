package com.clxns.app.ui.home.plan.listview

import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.clxns.app.data.model.MyPlanData
import com.clxns.app.databinding.PlanListItemsBinding
import com.clxns.app.ui.home.plan.DetailsActivity
import java.util.*

class ListViewAdapter(var context: Context) :
    PagingDataAdapter<MyPlanData, ListViewAdapter.MyViewHolder>(ListViewDiff) {
    private lateinit var cal: Calendar


    class MyViewHolder(var listItemsBinding: PlanListItemsBinding) :
        RecyclerView.ViewHolder(listItemsBinding.root)

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        holder.listItemsBinding.planCallBtn.setOnClickListener {
            val intent = Intent(Intent.ACTION_DIAL)
            intent.data = Uri.parse("tel:0123456789")
            context.startActivity(intent)
        }

        holder.listItemsBinding.planFollowUpBtn.setOnClickListener {
            cal = Calendar.getInstance()
            datePickerDialog(context)
        }

        holder.listItemsBinding.planCheckInBtn.setOnClickListener {
            val intent = Intent(context, DetailsActivity::class.java)
            context.startActivity(intent)
        }
    }

    private fun datePickerDialog(context: Context) {
        val dateSetListener =
            DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
                cal.set(Calendar.YEAR, year)
                cal.set(Calendar.MONTH, monthOfYear)
                cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            }

        DatePickerDialog(
            context,
            dateSetListener,
            // set DatePickerDialog to point to today's date when it loads up
            cal.get(Calendar.YEAR),
            cal.get(Calendar.MONTH),
            cal.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    override fun getItemCount(): Int {
        return 15
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder(PlanListItemsBinding.inflate(LayoutInflater.from(parent.context)))
    }

    object ListViewDiff : DiffUtil.ItemCallback<MyPlanData>() {
        override fun areItemsTheSame(oldItem: MyPlanData, newItem: MyPlanData): Boolean {
            return oldItem.image == newItem.image
        }

        override fun areContentsTheSame(
            oldItem: MyPlanData,
            newItem: MyPlanData,
        ): Boolean {
            return oldItem.image == newItem.image
        }

    }

}


