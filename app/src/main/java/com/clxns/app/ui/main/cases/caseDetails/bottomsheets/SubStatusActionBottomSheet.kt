package com.clxns.app.ui.main.cases.caseDetails.bottomsheets

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import com.clxns.app.R
import com.clxns.app.data.model.AdditionalFieldModel
import com.clxns.app.data.model.CaseDetailsResponse
import com.clxns.app.databinding.SubStatusActionBottomSheetBinding
import com.clxns.app.utils.getDateInLongFormat
import com.clxns.app.utils.hide
import com.clxns.app.utils.show
import com.clxns.app.utils.toast
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*


class SubStatusActionBottomSheet(
    private var caseDetails : CaseDetailsResponse?, private var callback : OnClick
) : BottomSheetDialogFragment() {

    private lateinit var actionBinding : SubStatusActionBottomSheetBinding
    private var isPTP = false
    private var dispositionType = ""
    private var subDispositionType = ""
    private var timeFormatted = "00:00:00.000Z"
    private var dateFormatted = ""
    private var dateFormattedRecovery = ""
    private val additionalFields = AdditionalFieldModel()
    private var fosAssignedDate : String? = null

    companion object {
        const val TAG = "SubStatusActionBottomSheet"
        private var year : Int = 0
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
            caseDetails : CaseDetailsResponse?,
            callback : OnClick
        ) : SubStatusActionBottomSheet {
            return SubStatusActionBottomSheet(caseDetails, callback)
        }
    }


    override fun onCreate(savedInstanceState : Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.CustomBottomSheetDialogTheme)
    }

    override fun onCreateView(
        inflater : LayoutInflater,
        container : ViewGroup?,
        savedInstanceState : Bundle?
    ) : View {
        isPTP = arguments?.getBoolean("isPTPAction") ?: false
        dispositionType = arguments?.getString("dispositionType") ?: ""
        subDispositionType = arguments?.getString("customNotFoundReason") ?: ""
        fosAssignedDate = arguments?.getString("fosAssignedDate")
        actionBinding = SubStatusActionBottomSheetBinding.inflate(layoutInflater)
        return actionBinding.root
    }

    override fun onViewCreated(view : View, savedInstanceState : Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initView()

        setListeners()

        initSpinners()


    }


    private fun initView() {
        actionBinding.llPayment.hide()
        if (isPTP) {
            actionBinding.revisitDateLabel.text = getString(R.string.future)
            actionBinding.assignToTracerCB.hide()
            actionBinding.revisitTimeLabel.hide()
            actionBinding.revisitTimeTxt.hide()
            actionBinding.revisitDateTxt.hint = "Set future date"
        } else {
            actionBinding.statusActionActiveLabel.hide()
            actionBinding.statusActionActiveRG.hide()
        }

        if (dispositionType == "Settlement/Foreclosure") {
            actionBinding.statusActionActiveRG.hide()
            actionBinding.txtStatus.hide()
            actionBinding.statusSpinnerLayout.hide()
            actionBinding.llPaymentDetails.hide()
            actionBinding.revisitTimeLabel.hide()
            actionBinding.revisitTimeTxt.hide()
            actionBinding.llPayment.show()
            actionBinding.statusActionActiveLabel.show()
            actionBinding.statusActionActiveLabel.text = getString(R.string.settlement_foreclosure)
            actionBinding.revisitDateLabel.text = getString(R.string.future)
            actionBinding.revisitDateTxt.hint = "Set future date"
        }
    }

    private fun setListeners() {
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
                        || dispositionType == "Settlement/Foreclosure"
                    ) additionalFields else null
                )
                this.dismiss()
            }
        }
    }

    private fun initSpinners() {
        if (dispositionType == "PTP"
            || dispositionType == "Dispute"
            || dispositionType == "Customer Not Found"
            || dispositionType == "Settlement/Foreclosure"
        ) {
            if (dispositionType != "Settlement/Foreclosure") {
                actionBinding.spStatus.show()
                actionBinding.txtStatus.show()
            }

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
                        parent : AdapterView<*>?,
                        view : View?,
                        position : Int,
                        id : Long
                    ) {
                        if (position == 0) {
                            subDispositionType = ""
                            actionBinding.llPayment.hide()
                            actionBinding.llPaymentDetails.hide()
                            resetFields()
                        } else {
                            subDispositionType = actionBinding.spStatus.selectedItem.toString()
                        }
                        if (dispositionType == "PTP") {
                            actionBinding.llPaymentDetails.hide()
                            if (position == 1) {
                                actionBinding.llPayment.show()
                                resetFields()
                            }
                            if (position == 2) {
                                actionBinding.llPayment.hide()
                                resetFields()
                            }
                        }
                        if (dispositionType == "Dispute") {
                            actionBinding.llPaymentDetails.show()
                            if (position == 1) {
                                resetFields()
                                actionBinding.llPayment.show()
                            } else {
                                resetFields()
                                actionBinding.llPayment.hide()
                            }
                        }
                    }

                    override fun onNothingSelected(parent : AdapterView<*>?) {

                    }
                }

            //
            val amountTypeAdapter = ArrayAdapter.createFromResource(
                requireContext(),
                when (dispositionType) {
                    "Settlement/Foreclosure" -> {
                        R.array.settlement_amount_type
                    }
                    "PTP" -> {
                        R.array.ptp_amount_type
                    }
                    else -> {
                        R.array.dispute_amount_type
                    }
                },
                android.R.layout.simple_spinner_item
            )
            amountTypeAdapter.setDropDownViewResource(android.R.layout.select_dialog_singlechoice)
            actionBinding.spAmount.adapter = amountTypeAdapter

            actionBinding.spAmount.onItemSelectedListener =
                object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(
                        parent : AdapterView<*>?,
                        view : View?,
                        position : Int,
                        id : Long
                    ) {
                        if (dispositionType == "Settlement/Foreclosure") {
                            setPaymentETActive(true)
                            actionBinding.paymentAmountEt.setText("")
                        } else {
                            if (position == 1 || position == 2) {
                                setPaymentETActive(false)
                                if (position == 1) {
                                    val totalDueAmount =
                                        caseDetails?.data?.totalDueAmount?.minus(
                                            caseDetails?.data?.amountCollected!!
                                        )
                                    actionBinding.paymentAmountEt.setText(totalDueAmount.toString())
                                } else {
                                    val posAmount =
                                        caseDetails?.data?.principalOutstandingAmount?.minus(
                                            caseDetails?.data?.amountCollected!!
                                        )
                                    actionBinding.paymentAmountEt.setText(posAmount.toString())
                                }
                            } else if (position == 3) {
                                setPaymentETActive(true)
                                actionBinding.paymentAmountEt.setText("")
                            } else {
                                setPaymentETActive(false)
                                actionBinding.paymentAmountEt.setText("")
                            }
                        }
                    }

                    override fun onNothingSelected(parent : AdapterView<*>?) {

                    }
                }

        } else {
            actionBinding.spStatus.hide()
            actionBinding.txtStatus.hide()
        }
    }

    private fun setPaymentETActive(value : Boolean) {
        actionBinding.paymentAmountEt.isClickable = value
        actionBinding.paymentAmountEt.isFocusableInTouchMode = value
        actionBinding.paymentAmountEt.isFocusable = value
    }

    private fun showTimePicker() {
        val currentTime = Calendar.getInstance()
        val hour = currentTime[Calendar.HOUR_OF_DAY]
        val minute = currentTime[Calendar.MINUTE]
        val formatter = SimpleDateFormat("hh:mm aa", Locale.getDefault())
        val mTimePicker = TimePickerDialog(
            requireContext(),
            { _, selectedHour, selectedMinute ->

                val time = "${String.format("%02d", selectedHour)}:${
                    String.format(
                        "%02d",
                        selectedMinute
                    )
                }"
                timeFormatted = "$time:00.000Z"
                val fmt = SimpleDateFormat("HH:mm", Locale.getDefault())
                var date : Date? = null
                try {
                    date = fmt.parse(time)
                } catch (e : ParseException) {
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

    private fun showDatePicker(isRecovery : Boolean) {
        val calendar = Calendar.getInstance()
        year = calendar.get(Calendar.YEAR)
        day = calendar.get(Calendar.DAY_OF_MONTH)
        month = calendar.get(Calendar.MONTH)

        val datePickerDialog =
            DatePickerDialog(requireContext(), { _, year, month, day ->
                val m = month + 1
                val date = "$day, ${MONTHS[month]} $year"
                if (isRecovery) {

                    actionBinding.txtPaymentOrRecoveryDateValue.text = date
                    dateFormattedRecovery =
                        "${year}-${String.format("%02d", m)}-${String.format("%02d", day)}"

                } else {
                    if (actionBinding.revisitTimeTxt.text.toString().isEmpty()
                        || actionBinding.revisitTimeTxt.text.toString().isBlank()
                    ) {
                        //set current time as default time
                        val millis = System.currentTimeMillis()
                        val c = Calendar.getInstance()
                        c.timeInMillis = millis
                        val hours = c.get(Calendar.HOUR)
                        val minutes = c.get(Calendar.MINUTE)

                        timeFormatted = "${String.format("%02d", hours + 12)}:${
                            String.format(
                                "%02d",
                                minutes
                            )
                        }:00.000Z"
                    }
                    actionBinding.revisitDateTxt.text = date
                    dateFormatted =
                        "${year}-${String.format("%02d", m)}-${String.format("%02d", day)}"

                }
            }, year, month, day)
        if (isRecovery) {
            datePickerDialog.datePicker.minDate =
                fosAssignedDate?.getDateInLongFormat() ?: System.currentTimeMillis()
            datePickerDialog.datePicker.maxDate = System.currentTimeMillis()
        } else {
            datePickerDialog.datePicker.minDate = System.currentTimeMillis()
        }
        datePickerDialog.show()
    }

    private fun validate() : Boolean {
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

        val amount = actionBinding.paymentAmountEt.text.toString()
        val actualAmount = caseDetails?.data?.totalDueAmount?.minus(
            caseDetails?.data?.amountCollected!!
        )
        if (amount.isNotEmpty() || amount.isNotBlank()) {
            if (isPTP){
                if (amount.toInt() < actualAmount!!) {
                    additionalFields.ptpAmount = amount
                } else {
                    requireContext().toast("Amount must be less than total due amount - ₹$actualAmount")
                    return false
                }
            }else{
                if (amount.toInt() <= actualAmount!!) {
                    additionalFields.ptpAmount = amount
                } else {
                    requireContext().toast("Amount cannot be greater than total due amount - ₹$actualAmount")
                    return false
                }
            }

        }

        additionalFields.recoveredAmount = additionalFields.ptpAmount

        additionalFields.recoveryDate = dateFormattedRecovery

        additionalFields.refChequeNo = actionBinding.edtReferenceType.text.toString()

        additionalFields.ptpDate = dateFormatted

        additionalFields.assignTracer = actionBinding.assignToTracerCB.isChecked

        additionalFields.ptpProbability =
            if (actionBinding.statusActionActiveRG.checkedRadioButtonId == actionBinding.rb80.id) "80% >" else
                (when (actionBinding.statusActionActiveRG.checkedRadioButtonId) {
                    actionBinding.rb50.id -> "50% - 80%"
                    actionBinding.rb30.id -> "50% <"
                    else -> ""
                })

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
            dispositionType : String,
            subDispositionType : String,
            followUpDate : String,
            remark : String,
            additionalFields : AdditionalFieldModel?
        )
    }

}