package com.clxns.app.ui.main.cases.casedetails.bottomsheets

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import com.clxns.app.R
import com.clxns.app.data.model.AdditionalFieldModel
import com.clxns.app.data.model.CaseDetailsResponse
import com.clxns.app.databinding.SubStatusActionBottomSheetBinding
import com.clxns.app.utils.toast
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.gson.Gson
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*


class SubStatusActionBottomSheet(
    private var caseDetails: CaseDetailsResponse?, private var callback: OnClick
) : BottomSheetDialogFragment() {

    private lateinit var actionBinding: SubStatusActionBottomSheetBinding
    private var isPTP = false
    private var dispositionType = ""
    private var subDispositionType = ""
    private var timeFormatted = "00:00:00.000Z"
    private var dateFormatted = ""
    private var dateFormattedRecovery = ""
    private val additionalFields = AdditionalFieldModel()

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

        fun newInstance(
            caseDetails: CaseDetailsResponse?,
            callback: OnClick
        ): SubStatusActionBottomSheet {
            return SubStatusActionBottomSheet(caseDetails, callback)
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
        subDispositionType = arguments?.getString("customNotFoundReason") ?: ""
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
            showDatePicker(false)
        }

        actionBinding.txtPaymentOrRecoveryDateValue.setOnClickListener {
            showDatePicker(true)
        }

        actionBinding.revisitTimeTxt.setOnClickListener {
            showTimePicker()
        }

        actionBinding.doneBtn.setOnClickListener {
            if (validate()) {
                callback.onClick(
                    dispositionType,
                    subDispositionType,
                    if (dateFormatted == "") "" else "${dateFormatted}T${timeFormatted}",
                actionBinding.statusActionRemarkET.text.toString(),
                if (subDispositionType == "Active PTP"
                    || subDispositionType == "Future PTP"
                    || subDispositionType == "Already Paid"
                ) Gson().toJson(additionalFields) else ""
                )
                this.dismiss()
            }
        }



        actionBinding.llPayment.visibility = View.GONE
        if (dispositionType == "PTP"
            || dispositionType == "Dispute"
            || dispositionType == "Customer Not Found"
        ) {
            actionBinding.spStatus.visibility = View.VISIBLE
            actionBinding.txtStatus.visibility = View.VISIBLE

            val dispositionAdapter = ArrayAdapter.createFromResource(
                requireContext(),
                if (dispositionType == "PTP") R.array.ptp_sub_dispositions else
                    (if (dispositionType == "Dispute") R.array.dispute_sub_dispositions
                    else R.array.customer_not_found_sub_dispositions),
                android.R.layout.simple_spinner_item
            )
            dispositionAdapter.setDropDownViewResource(android.R.layout.select_dialog_singlechoice)
            actionBinding.spStatus.adapter = dispositionAdapter

            actionBinding.spStatus.onItemSelectedListener =
                object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(
                        parent: AdapterView<*>?,
                        view: View?,
                        position: Int,
                        id: Long
                    ) {
                        if (position == 0) {
                            subDispositionType = ""
                            actionBinding.llPayment.visibility = View.GONE
                            actionBinding.llPaymentDetails.visibility = View.GONE
                            resetFields()
                        } else {
                            subDispositionType = actionBinding.spStatus.selectedItem.toString()
                        }
                        if (dispositionType == "PTP") {
                            actionBinding.llPaymentDetails.visibility = View.GONE
                            if (position == 1) {
                                actionBinding.llPayment.visibility = View.VISIBLE
                                resetFields()
                            }
                            if (position == 2) {
                                actionBinding.llPayment.visibility = View.GONE
                                resetFields()
                            }
                        }
                        if (dispositionType == "Dispute") {
                            actionBinding.llPaymentDetails.visibility = View.VISIBLE
                            if (position == 1) {
                                resetFields()
                                actionBinding.llPayment.visibility = View.VISIBLE
                            } else {
                                resetFields()
                                actionBinding.llPayment.visibility = View.GONE
                            }
                        }
                    }

                    override fun onNothingSelected(parent: AdapterView<*>?) {

                    }
                }

            //
            val amountTypeAdapter = ArrayAdapter.createFromResource(
                requireContext(),
                R.array.amount_type,
                android.R.layout.simple_spinner_item
            )
            amountTypeAdapter.setDropDownViewResource(android.R.layout.select_dialog_singlechoice)
            actionBinding.spAmount.adapter = amountTypeAdapter

            actionBinding.spAmount.onItemSelectedListener =
                object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(
                        parent: AdapterView<*>?,
                        view: View?,
                        position: Int,
                        id: Long
                    ) {
                        if (position == 1 || position == 2) {
                            actionBinding.paymentAmountEt.isEnabled = false
                            if (position == 1) {
                                actionBinding.paymentAmountEt.setText(caseDetails?.data?.totalDueAmount.toString())
                            } else {
                                actionBinding.paymentAmountEt.setText(caseDetails?.data?.principalOutstandingAmount.toString())
                            }
                        } else {
                            actionBinding.paymentAmountEt.isEnabled = true
                            actionBinding.paymentAmountEt.setText("")
                        }
                    }

                    override fun onNothingSelected(parent: AdapterView<*>?) {

                    }
                }

        } else {
            actionBinding.spStatus.visibility = View.GONE
            actionBinding.txtStatus.visibility = View.GONE
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

    private fun showDatePicker(isRecovery: Boolean) {
        val calendar = Calendar.getInstance()
        year = calendar.get(Calendar.YEAR)
        day = calendar.get(Calendar.DAY_OF_MONTH)
        month = calendar.get(Calendar.MONTH)

        val datePickerDialog =
            DatePickerDialog(requireContext(), { _, year, month, day ->
                val m = month + 1
                Log.i(javaClass.name, "month--->" + MONTHS[month])
                if (isRecovery) {
                    actionBinding.txtPaymentOrRecoveryDateValue.text = "$day/$m/$year"
                    dateFormattedRecovery =
                        "${year}-${String.format("%02d", m)}-${String.format("%02d", day)}"

                } else {
                    actionBinding.revisitDateTxt.text = "$day/$m/$year"
                    dateFormatted =
                        "${year}-${String.format("%02d", m)}-${String.format("%02d", day)}"
                }
            }, year, month, day)

        datePickerDialog.datePicker.minDate=System.currentTimeMillis()
        datePickerDialog.show()
    }

    private fun validate(): Boolean {
        var success = true
        var message = ""
        if (isPTP && success && actionBinding.statusActionActiveRG.checkedRadioButtonId == -1) {
            success = false
            message = "Please select probability"
        }

        if (success && (dispositionType == "PTP"
                    || dispositionType == "Dispute"
                    || dispositionType == "Customer Not Found")
            && (subDispositionType.isEmpty() || subDispositionType.isBlank())
        ) {
            success = false
            message = "Please select status"
        }

//        if (success && (actionBinding.revisitDateTxt.text.isBlank()
//                    || actionBinding.revisitDateTxt.text.isEmpty())
//        ) {
//            success = false
//            val txt = if (isPTP) "future" else "revisit"
//            message = "Please select $txt date"
//        }
//        if (!isPTP && success && (actionBinding.revisitTimeTxt.text.isBlank()
//                    || actionBinding.revisitTimeTxt.text.isEmpty())
//        ) {
//            success = false
//            message = "Please select revisit time"
//        }
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
        additionalFields.paymentMode =
            if (actionBinding.rgPayment.checkedRadioButtonId == actionBinding.rbOnline.id) "Online" else
                (if (actionBinding.rgPayment.checkedRadioButtonId == actionBinding.rbCheque.id) "Cheque" else
                    (if (actionBinding.rgPayment.checkedRadioButtonId == actionBinding.rbCash.id) "Cash" else ""))

        additionalFields.recoveryType = additionalFields.paymentMode

        additionalFields.ptpAmountType =
            if (actionBinding.spAmount.selectedItem == null
                || actionBinding.spAmount.selectedItem.toString() == "Select an Option"
            ) ""
            else actionBinding.spAmount.selectedItem.toString()

        additionalFields.ptpAmount = actionBinding.paymentAmountEt.text.toString()
        additionalFields.recoveredAmount = additionalFields.ptpAmount

        additionalFields.recoveryDate = dateFormattedRecovery

        additionalFields.refChequeNo = actionBinding.edtReferenceType.text.toString()

        additionalFields.ptpDate = dateFormatted

        additionalFields.assignTracer = actionBinding.assignToTracerCB.isChecked

        additionalFields.ptpProbability =
            if (actionBinding.statusActionActiveRG.checkedRadioButtonId == actionBinding.rb80.id) "80% >" else
                (if (actionBinding.statusActionActiveRG.checkedRadioButtonId == actionBinding.rb50.id) "50% - 80%" else
                    if (actionBinding.statusActionActiveRG.checkedRadioButtonId == actionBinding.rb30.id) "50% <" else "")

        return true
    }

    private fun resetFields() {
        actionBinding.paymentAmountEt.text = null
        actionBinding.rgPayment.clearCheck()
        actionBinding.txtPaymentOrRecoveryDateValue.text = ""
        actionBinding.edtReferenceType.text = null
        dateFormattedRecovery = ""
    }

    interface OnClick {
        fun onClick(
            dispositionType: String,
            subDispositionType: String,
            followUpDate: String,
            remark: String,
            additionalFields: String
        )
    }

}