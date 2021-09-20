package com.clxns.app.ui.main.cases.casedetails.bottomsheets

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.clxns.app.databinding.SubStatusActionBottomSheetBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*


class SubStatusActionBottomSheet : BottomSheetDialogFragment() {

    private lateinit var actionBinding: SubStatusActionBottomSheetBinding
    private var isPTP = false

    companion object {
        const val TAG = "SubStatusActionBottomSheet"
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
    }

    fun newInstance(): SubStatusActionBottomSheet {
        return SubStatusActionBottomSheet()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        isPTP = arguments?.getBoolean("isPTPAction") ?: false
        actionBinding = SubStatusActionBottomSheetBinding.inflate(layoutInflater)
        return actionBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (isPTP) {
            actionBinding.revisitDateLabel.text = "Future"
            actionBinding.assignToTracerCB.visibility = View.GONE
            actionBinding.revisitTimeLable.visibility = View.GONE
            actionBinding.revisitTimeTxt.visibility = View.GONE
        } else {
            actionBinding.statusActionActiveLabel.visibility = View.GONE
            actionBinding.statusActionActiveRG.visibility = View.GONE
        }

        actionBinding.revisitDateTxt.setOnClickListener {
            showDatePicker()
        }

        actionBinding.revisitTimeTxt.setOnClickListener {
            showTimePicker()
        }

        actionBinding.doneBtn.setOnClickListener {
            this.dismiss()
        }
    }

    private fun showTimePicker() {
        val currentTime = Calendar.getInstance()
        val hour = currentTime[Calendar.HOUR_OF_DAY]
        val minute = currentTime[Calendar.MINUTE]
        val formatter = SimpleDateFormat("hh:mm Aa", Locale.getDefault())
        val mTimePicker = TimePickerDialog(
            requireContext(),
            { _, selectedHour, selectedMinute ->

                val time = "$selectedHour:$selectedMinute"
                val fmt = SimpleDateFormat("HH:mm", Locale.getDefault())
                var date: Date? = null
                try {
                    date = fmt.parse(time)
                } catch (e: ParseException) {
                    e.printStackTrace()
                }
                actionBinding.revisitTimeTxt.text = formatter.format(date)
            },
            hour,
            minute,
            false
        ) //Yes 24 hour time

        mTimePicker.setTitle("Select Time")
        mTimePicker.show()
    }

    private fun showDatePicker() {
        val calendar = Calendar.getInstance()
        year = calendar.get(Calendar.YEAR)
        day = calendar.get(Calendar.DAY_OF_MONTH)
        month = calendar.get(Calendar.MONTH)

        val datePickerDialog =
            DatePickerDialog(requireContext(), { _, year, month, day ->
                val m = month + 1
                Log.i(javaClass.name, "month--->" + MONTHS[month])
                actionBinding.revisitDateTxt.text = "$day/$m/$year"
            }, year, month, day)

        datePickerDialog.show()
    }
}