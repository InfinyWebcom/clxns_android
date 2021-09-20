package com.clxns.app.ui.casedetails.bottomsheets

import android.app.DatePickerDialog
import android.content.Context
import android.content.res.Resources
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import com.clxns.app.R
import com.clxns.app.databinding.FragmentFilterDialogBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import java.util.*


class FilterDialogFragment : BottomSheetDialogFragment() {

    private var year: Int = 0
    var month = 0
    var day = 0
    private val MONTHS = arrayOf(
        "Jan",
        "Feb",
        "Mar",
        "Apr",
        "May",
        "Jun",
        "Jul",
        "Aug",
        "Sept",
        "Oct",
        "Nov",
        "Dec"
    )
    lateinit var ctx: Context
    lateinit var filterDialogBinding: FragmentFilterDialogBinding
    lateinit var view1: View

    fun newInstance(): FilterDialogFragment {
        return FilterDialogFragment()
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?,
    ): View? {

        setInit()
        setListeners()

        return view1
    }

    private fun setListeners() {
        filterDialogBinding.txtStartMonth.setOnClickListener {
            showDatePicker()
        }

        filterDialogBinding.txtEndMonth.setOnClickListener {
            showSecondDatePicker()
        }

        filterDialogBinding.txtApplyFilter.setOnClickListener {
            dismiss()
        }

        filterDialogBinding.txtCancelFilter.setOnClickListener {
            dismiss()
        }

    }

    private fun showDatePicker() {

        val calendar = Calendar.getInstance()
        year = calendar.get(Calendar.YEAR)
        day = calendar.get(Calendar.DAY_OF_MONTH)
        month = calendar.get(Calendar.MONTH)

        val datePickerDialog =
            DatePickerDialog(ctx, { _, year, month, day ->
                val m = month + 1
                Log.i(javaClass.name, "month--->" + MONTHS[month])
                filterDialogBinding.txtStartMonth.text =
                    "$day/$m/$year"
            }, year, month, day)

        datePickerDialog.show()
    }
    private fun showSecondDatePicker() {

        val calendar = Calendar.getInstance()
        year = calendar.get(Calendar.YEAR)
        day = calendar.get(Calendar.DAY_OF_MONTH)
        month = calendar.get(Calendar.MONTH)

        val datePickerDialog =
            DatePickerDialog(ctx, { _, year, month, day ->
                val m = month + 1
                Log.i(javaClass.name, "month--->" + MONTHS[month])
                filterDialogBinding.txtEndMonth.text =
                    "$day/$m/$year"
            }, year, month, day)

        datePickerDialog.show()
    }

    private fun setInit() {
        ctx = activity as Context
        filterDialogBinding = FragmentFilterDialogBinding.inflate(layoutInflater)
        view1 = filterDialogBinding.root


        val statusArrayAdapter = ArrayAdapter.createFromResource(
            ctx,
            R.array.status_list,
            android.R.layout.simple_spinner_item
        )
        statusArrayAdapter.setDropDownViewResource(android.R.layout.select_dialog_singlechoice)
        filterDialogBinding.spStatus.adapter = statusArrayAdapter

        val subStatusArrayAdapter = ArrayAdapter.createFromResource(
            ctx,
            R.array.sub_status_list,
            android.R.layout.simple_spinner_item
        )
        subStatusArrayAdapter.setDropDownViewResource(android.R.layout.select_dialog_singlechoice)
        filterDialogBinding.spSubStatus.adapter = subStatusArrayAdapter


    }

}