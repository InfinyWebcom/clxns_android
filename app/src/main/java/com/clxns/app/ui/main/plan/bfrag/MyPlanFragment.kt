package com.clxns.app.ui.main.plan.bfrag

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.clxns.app.R
import com.clxns.app.data.api.helper.NetworkResult
import com.clxns.app.data.preference.SessionManager
import com.clxns.app.databinding.FragmentMyPlanBinding
import com.clxns.app.utils.Constants
import com.clxns.app.utils.hide
import com.clxns.app.utils.show
import com.clxns.app.utils.toast
import dagger.hilt.android.AndroidEntryPoint
import java.util.*
import javax.inject.Inject

@AndroidEntryPoint
class MyPlanFragment : Fragment() {

    private val viewModel: MyPlanViewModel by viewModels()
    private var _binding: FragmentMyPlanBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private lateinit var datePickerDialog: DatePickerDialog

    @Inject
    lateinit var sessionManager: SessionManager

    companion object {
        fun newInstance() = MyPlanFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentMyPlanBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setInit()
        setListeners()
        setObserver()
    }

    private fun setInit() {
        //Initializing Calendar
        viewModel.calendar = Calendar.getInstance()
        viewModel.getCurrentDate()
    }


    private fun setListeners() {

        binding.txtDateSort.setOnClickListener {
            datePickerDialog.show()
        }
        //Listener for Calender pop up dialog
        datePickerDialog = DatePickerDialog(requireContext(), { _, year, monthOfYear, dayOfMonth ->

            val filterDate = "" + dayOfMonth + " " + viewModel.monthArray[monthOfYear] + ", " + year
            Toast.makeText(context, "Fetching data for $filterDate", Toast.LENGTH_LONG).show()
            viewModel.mYear = year
            viewModel.mMonth = monthOfYear
            viewModel.mDay = dayOfMonth
            val filterAPIDate = "${viewModel.mYear}-${
                String.format(
                    "%02d",
                    viewModel.mMonth + 1
                )
            }-${String.format("%02d", viewModel.mDay)}"

            if (viewModel.currentDate == filterAPIDate) {
                binding.txtDateSort.text = resources.getString(R.string.today)
            } else {
                binding.txtDateSort.text = filterDate
            }

            binding.progressBar.show()
            viewModel.getMyPlanList(sessionManager.getString(Constants.TOKEN)!!, filterAPIDate)
        }, viewModel.mYear, viewModel.mMonth, viewModel.mDay)

        //Renamed "OK" button to "Filter"
        datePickerDialog.setButton(DatePickerDialog.BUTTON_POSITIVE, "Filter", datePickerDialog)
        //Neutral button which appears on the bottom left corner to reset the date filter to Today
        datePickerDialog.setButton(DatePickerDialog.BUTTON_NEUTRAL, "Reset") { _, _ ->
            viewModel.getCurrentDate()
            datePickerDialog.updateDate(viewModel.mYear, viewModel.mMonth, viewModel.mDay)
            binding.txtDateSort.text = resources.getString(R.string.today)

            binding.progressBar.show()
            viewModel.getMyPlanList(
                sessionManager.getString(Constants.TOKEN)!!,
                viewModel.currentDate
            )
        }

        //Setting max date to current date for the calender
        datePickerDialog.datePicker.maxDate = System.currentTimeMillis()
    }


    private fun setObserver() {
        viewModel.getMyPlanList(sessionManager.getString(Constants.TOKEN)!!, viewModel.currentDate)
        viewModel.response.observe(viewLifecycleOwner) { response ->
            when (response) {
                is NetworkResult.Success -> {
                    binding.progressBar.hide()
                    if (!response.data?.error!!) {
                        if (response.data.total!! > 0) {
                            binding.txtNoData.visibility = View.GONE

                            binding.recyclerContacts.apply {
                                adapter = MyPlanAdapter(requireContext(), response.data.data)
                            }
                        } else {
                            binding.txtNoData.visibility = View.VISIBLE
                        }
                    } else {
                        requireContext().toast(response.data.title!!)
                    }
                    // bind data to the view
                }
                is NetworkResult.Error -> {
                    binding.progressBar.hide()
                    requireContext().toast(response.message!!)
                    // show error message
                }
                is NetworkResult.Loading -> {
                    binding.progressBar.show()
                    // show a progress bar
                }
            }
        }
    }


}