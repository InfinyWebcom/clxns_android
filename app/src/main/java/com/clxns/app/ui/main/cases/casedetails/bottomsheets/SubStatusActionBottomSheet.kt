package com.clxns.app.ui.main.cases.casedetails.bottomsheets

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.clxns.app.databinding.SubStatusActionBottomSheetBinding
import com.clxns.app.utils.toast
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*


class SubStatusActionBottomSheet(private var callback: OnClick) : BottomSheetDialogFragment() {

    private lateinit var actionBinding: SubStatusActionBottomSheetBinding
    private var isPTP = false
    private var dispositionType = ""
    private var customNotFoundReason = ""
    private var timeFormatted = "00:00:00.000Z"
    private var dateFormatted = ""

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

        fun newInstance(callback: OnClick): SubStatusActionBottomSheet {
            return SubStatusActionBottomSheet(callback)
        }
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
        dispositionType = arguments?.getString("dispositionType") ?: ""
        dispositionType = arguments?.getString("customNotFoundReason") ?: ""
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
            actionBinding.revisitDateTxt.hint = "Set future date"
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
            if (validate()) {
                callback.onClick(
                    dispositionType,
                    "${dateFormatted}T${timeFormatted}",
                    actionBinding.statusActionRemarkET.text.toString(),
                    actionBinding.assignToTracerCB.isChecked,
                    customNotFoundReason
                )
                this.dismiss()
            }
        }

    }

    private fun showTimePicker() {
        val currentTime = Calendar.getInstance()
        val hour = currentTime[Calendar.HOUR_OF_DAY]
        val minute = currentTime[Calendar.MINUTE]
        val formatter = SimpleDateFormat("hh:mm aa", Locale.getDefault())
        val mTimePicker = TimePickerDialog(
            requireContext(),
            { _, selectedHour, selectedMinute ->

                val time = "$selectedHour:$selectedMinute"
                timeFormatted = "$time:00.000Z"
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
                dateFormatted = "${year}-${String.format("%02d", m)}-${String.format("%02d", day)}"
            }, year, month, day)

        datePickerDialog.show()
    }

    private fun validate(): Boolean {
        var success = true
        var message = ""
        if (isPTP && success && actionBinding.statusActionActiveRG.checkedRadioButtonId == -1) {
            success = false
            message = "Please select probability"
        }

        if (success && (actionBinding.revisitDateTxt.text.isBlank()
                    || actionBinding.revisitDateTxt.text.isEmpty())
        ) {
            success = false
            val txt = if (isPTP) "future" else "revisit"
            message = "Please select $txt date"
        }
        if (!isPTP && success && (actionBinding.revisitTimeTxt.text.isBlank()
                    || actionBinding.revisitTimeTxt.text.isEmpty())
        ) {
            success = false
            message = "Please select revisit time"
        }
        if (success && (actionBinding.statusActionRemarkET.text.toString().isBlank()
                    || actionBinding.statusActionRemarkET.text.toString().isEmpty())
        ) {
            success = false
            message = "Please enter remark"
        }

        if (!success) {
            context?.toast(message)
            return false
        }
        return true
    }

    interface OnClick {
        fun onClick(
            dispositionType: String,
            followUpDate: String,
            remark: String,
            assignTracker: Boolean,
            customNotFoundReason: String
        )
    }

}