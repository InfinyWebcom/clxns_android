package com.clxns.app.ui.main.cases.casedetails.bottomsheets

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.TextView
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.clxns.app.R
import com.clxns.app.data.preference.SessionManager
import com.clxns.app.databinding.BottomSheetCasesFilterBinding
import com.clxns.app.ui.main.cases.CasesViewModel
import com.clxns.app.utils.Constants
import com.clxns.app.utils.toast
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import java.util.*
import javax.inject.Inject

@AndroidEntryPoint
class CasesFilterBS : BottomSheetDialogFragment() {

    private var _binding: BottomSheetCasesFilterBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private lateinit var calendar: Calendar
    private lateinit var datePickerDialog: DatePickerDialog

    private val casesViewModel: CasesViewModel by activityViewModels()
    private var dispositionList: ArrayList<String> = arrayListOf()
    private var subDispositionList = arrayListOf("None")

    @Inject
    lateinit var sessionManager: SessionManager

    private lateinit var dispositionSpinner: Spinner
    private lateinit var subDispositionSpinner: Spinner
    private lateinit var subDispositionAdapter: ArrayAdapter<String>

    private var fromDate: String = ""
    private var toDate: String = ""
    private var dispositionId: String = ""
    private var subDispositionId: String = ""

    private lateinit var token: String

    companion object {
        private var mYear: Int = 0
        private var mMonth = 0
        private var mDay = 0
        private val MONTHS = arrayOf(
            "Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul",
            "Aug", "Sept", "Oct", "Nov", "Dec"
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.CustomBottomSheetDialogTheme)
        casesViewModel.getAllDispositionsFromRoomDB()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = BottomSheetCasesFilterBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initView()
        initSubDispositionAdapter()
        setListeners()
        subscribeObserver()
    }

    private fun subscribeObserver() {
        casesViewModel.dispositionsResponse.observe(viewLifecycleOwner) {
            dispositionList.clear()
            dispositionList.add("Select")
            if (it.isNotEmpty()) {
                dispositionList.addAll(it)
                val dispositionAdapter = ArrayAdapter(
                    requireContext(),
                    android.R.layout.simple_spinner_item,
                    dispositionList
                )
                dispositionAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                dispositionSpinner.adapter = dispositionAdapter
            }
        }

        casesViewModel.subDispositionsResponse.observe(viewLifecycleOwner) {
            if (it.isNotEmpty()) {
                subDispositionList.addAll(it)
            } else {
                subDispositionList.clear()
                subDispositionList.add("None")
            }
            subDispositionSpinner.setSelection(0)
            subDispositionAdapter.notifyDataSetChanged()
        }
        //On success response it will fetch all the related sub dispositions from the local DB
        casesViewModel.dispositionsIdResponse.observe(viewLifecycleOwner) {
            dispositionId = it.toString()
            casesViewModel.getSubDispositionsFromRoomDB(it)
        }

        casesViewModel.subDispositionsIdResponse.observe(viewLifecycleOwner) {
            subDispositionId = it.toString()
        }
    }

    private fun initSubDispositionAdapter() {
        subDispositionAdapter =
            ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, subDispositionList)
        subDispositionAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        subDispositionSpinner.adapter = subDispositionAdapter
    }

    private fun initView() {
        calendar = Calendar.getInstance()
        mYear = calendar.get(Calendar.YEAR)
        mDay = calendar.get(Calendar.DAY_OF_MONTH)
        mMonth = calendar.get(Calendar.MONTH)
        dispositionSpinner = binding.statusSpinner
        subDispositionSpinner = binding.subStatusSpinner
        token = sessionManager.getString(Constants.TOKEN)!!
    }

    private fun setListeners() {
        binding.startDateTv.setOnClickListener {
            showDatePicker(it as TextView, false)
        }
        binding.endDateTv.setOnClickListener {
            if (binding.startDateTv.text.isNullOrEmpty()) {
                requireContext().toast("Please select start date first.")
            } else {
                showDatePicker(it as TextView, true)
            }
        }

        binding.filterApplyBtn.setOnClickListener {
            casesViewModel.getCasesList(
                token,
                "",
                dispositionId,
                subDispositionId,
                fromDate,
                toDate,
                "",
                ""
            )
            val actions = CasesFilterBSDirections.actionNavigationCasesFilterToNavigationCases(
                0,0,0,"","",true
            )
            findNavController().navigate(actions)
        }

        binding.filterResetBtn.setOnClickListener {
            casesViewModel.getCasesList(
                token, "", "", "", "", "",
                "",
                ""
            )
            dismiss()
        }

        dispositionSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                subDispositionList.clear()
                subDispositionList.add("Select")
                if (position != 0) {
                    subDispositionList.clear()
                    subDispositionList.add("Select")
                    casesViewModel.getDispositionIdFromRoomDB(dispositionList[position])
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        subDispositionSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                if (position != 0) {
                    casesViewModel.getSubDispositionIdFromRoomDB(subDispositionList[position])
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
            }

        }

    }

    private fun showDatePicker(textView: TextView, isEndDate: Boolean) {
        datePickerDialog = DatePickerDialog(requireContext(), { _, year, month, day ->
            val monthPlusOne = month + 1
            val date = "$day/${MONTHS[month]}/$year"
            textView.text = date
            mYear = year
            mMonth = month
            mDay = day
            if (!isEndDate) {
                val fDate = "$year-$monthPlusOne-$day"
                fromDate = fDate
                calendar.set(year, month, day + 1)
            } else {
                val tDate = "$year-$monthPlusOne-$day"
                toDate = tDate
            }
        }, mYear, mMonth, mDay)
        if (isEndDate) {
            datePickerDialog.datePicker.minDate = calendar.timeInMillis
        }
        datePickerDialog.show()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}