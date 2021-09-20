package com.clxns.app.ui.bottomSheetDialog

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.activityViewModels
import com.clxns.app.R
import com.clxns.app.data.preference.SessionManager
import com.clxns.app.databinding.BottomSheetCasesFilterBinding
import com.clxns.app.ui.cases.CasesViewModel
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

    @Inject
    lateinit var sessionManager: SessionManager

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
        setListeners()
    }

    private fun initView() {
        calendar = Calendar.getInstance()
        mYear = calendar.get(Calendar.YEAR)
        mDay = calendar.get(Calendar.DAY_OF_MONTH)
        mMonth = calendar.get(Calendar.MONTH)
    }

    private fun setListeners() {
        binding.startDateTv.setOnClickListener {
            showDatePicker(it as TextView, false)
        }
        binding.endDateTv.setOnClickListener {
            if (binding.startDateTv.text.isNullOrEmpty()) {
                requireContext().toast("Please select start date first")
            } else {
                showDatePicker(it as TextView, true)
            }
        }

        binding.filterApplyBtn.setOnClickListener {
            //casesViewModel.getCasesList(sessionManager.getString(Constants.TOKEN)!!)
            this.dismiss()
        }

        binding.filterResetBtn.setOnClickListener {
            initView()
            binding.startDateTv.text = ""
            binding.endDateTv.text = ""
        }
    }

    private fun showDatePicker(textView: TextView, isEndDate: Boolean) {
        datePickerDialog = DatePickerDialog(requireContext(), { _, year, month, day ->
            val date = "$day/${MONTHS[month]}/$year"
            textView.text = date
            mYear = year
            mMonth = month
            mDay = day
            if (!isEndDate) {
                calendar.set(year, month, day + 1)
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