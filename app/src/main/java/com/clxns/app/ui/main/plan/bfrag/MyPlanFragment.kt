package com.clxns.app.ui.main.plan.bfrag

import android.app.Activity
import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.RecyclerView
import com.clxns.app.R
import com.clxns.app.data.api.helper.NetworkResult
import com.clxns.app.data.preference.SessionManager
import com.clxns.app.databinding.FragmentMyPlanBinding
import com.clxns.app.ui.main.cases.casedetails.DetailsActivity
import com.clxns.app.utils.Constants
import com.clxns.app.utils.hide
import com.clxns.app.utils.show
import com.clxns.app.utils.snackBar
import dagger.hilt.android.AndroidEntryPoint
import java.util.*
import javax.inject.Inject

@AndroidEntryPoint
class MyPlanFragment : Fragment(), MyPlanAdapter.OnPlanItemClickListener {

    private val planViewModel: MyPlanViewModel by viewModels()
    private var _binding: FragmentMyPlanBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private lateinit var datePickerDialog: DatePickerDialog

    @Inject
    lateinit var sessionManager: SessionManager

    private lateinit var token: String
    private lateinit var noDataLayout: RelativeLayout
    private lateinit var planRV: RecyclerView

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
        initView()
        setListeners()
        subscribeObserver()
        getPlanList()
    }

    private fun initView() {
        //Initializing Calendar
        planViewModel.calendar = Calendar.getInstance()
        planViewModel.getCurrentDate()
        noDataLayout = binding.planNoData.root
        planRV = binding.recyclerContacts
        token = sessionManager.getString(Constants.TOKEN)!!
    }


    private fun setListeners() {

        binding.planNoData.retryBtn.setOnClickListener {
            getPlanList()
        }

        binding.txtDateSort.setOnClickListener {
            datePickerDialog.show()
        }
        //Listener for Calender pop up dialog
        datePickerDialog = DatePickerDialog(requireContext(), { _, year, monthOfYear, dayOfMonth ->

            val filterDate =
                "" + dayOfMonth + " " + planViewModel.monthArray[monthOfYear] + ", " + year
            //binding.root.snackBar("Fetching data for $filterDate")
            planViewModel.mYear = year
            planViewModel.mMonth = monthOfYear
            planViewModel.mDay = dayOfMonth
            val filterAPIDate = "${planViewModel.mYear}-${
                String.format(
                    "%02d",
                    planViewModel.mMonth + 1
                )
            }-${String.format("%02d", planViewModel.mDay)}"

            if (planViewModel.currentDate == filterAPIDate) {
                binding.txtDateSort.text = resources.getString(R.string.today)
            } else {
                binding.txtDateSort.text = filterDate
            }
            planViewModel.getMyPlanList(token, filterAPIDate)
        }, planViewModel.mYear, planViewModel.mMonth, planViewModel.mDay)

        //Renamed "OK" button to "Filter"
        datePickerDialog.setButton(DatePickerDialog.BUTTON_POSITIVE, "Filter", datePickerDialog)
        //Neutral button which appears on the bottom left corner to reset the date filter to Today
        datePickerDialog.setButton(DatePickerDialog.BUTTON_NEUTRAL, "Reset") { _, _ ->
            planViewModel.getCurrentDate()
            datePickerDialog.updateDate(
                planViewModel.mYear,
                planViewModel.mMonth,
                planViewModel.mDay
            )
            binding.txtDateSort.text = resources.getString(R.string.today)
            getPlanList()
        }

        //Setting max date to current date for the calender
        //datePickerDialog.datePicker.maxDate = System.currentTimeMillis()
    }

    private fun getPlanList() {
        planViewModel.getMyPlanList(token, planViewModel.currentDate)
    }

    private fun subscribeObserver() {
        planViewModel.response.observe(viewLifecycleOwner) { response ->
            when (response) {
                is NetworkResult.Success -> {
                    binding.planProgressBar.hide()
                    planRV.show()
                    if (!response.data?.error!!) {
                        if (response.data.total!! > 0) {
                            planRV.apply {
                                adapter = MyPlanAdapter(
                                    requireContext(),
                                    response.data.data,
                                    this@MyPlanFragment
                                )
                            }
                        } else {
                            binding.planNoData.noDataTv.text = getString(R.string.no_data)
                            noDataLayout.show()
                            binding.planNoData.retryBtn.hide()
                            planRV.hide()
                        }
                    } else {
                        noDataLayout.show()
                        binding.planNoData.noDataTv.text = response.data.title
                        planRV.hide()
                    }
                    // bind data to the view
                }
                is NetworkResult.Error -> {
                    binding.planProgressBar.hide()
                    planRV.hide()
                    noDataLayout.show()
                    binding.root.snackBar(response.message!!)
                    // show error message
                }
                is NetworkResult.Loading -> {
                    binding.planProgressBar.show()
                    noDataLayout.hide()
                    // show a progress bar
                }
            }
        }
    }

    private val startDetailActivityForResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == Activity.RESULT_OK) {
                val data: Intent? = it.data
                val status = data?.getBooleanExtra("hasChangedPlanStatus", false)
                if (status == true) {
                    getPlanList()
                }
            }
        }

    override fun openDetailActivity(
        loadId: String,
        name: String,
        dispositions: String,
        isPlanned: Boolean
    ) {
        val intent = Intent(context, DetailsActivity::class.java)
        intent.putExtra("loan_account_number", loadId)
        intent.putExtra("isPlanned", true)
        intent.putExtra("status", dispositions)
        intent.putExtra("name", name)
        startDetailActivityForResult.launch(intent)
    }


}