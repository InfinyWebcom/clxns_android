package com.clxns.app.ui.main.plan

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.clxns.app.R
import com.clxns.app.data.preference.SessionManager
import com.clxns.app.databinding.FragmentPlanBinding
import com.clxns.app.ui.main.plan.plannedLeads.MyPlanViewModel
import com.clxns.app.utils.Constants
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint
import java.util.*
import javax.inject.Inject


@AndroidEntryPoint
class PlanFragment : Fragment() {
    private val planViewModel : MyPlanViewModel by activityViewModels()
    private var _binding : FragmentPlanBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private lateinit var viewPagerAdapter : PlansViewPagerAdapter
    private var tabName = arrayOf("Leads", "Map View")

    private lateinit var datePickerDialog : DatePickerDialog

    @Inject
    lateinit var sessionManager : SessionManager

    private lateinit var token : String

    override fun onCreateView(
        inflater : LayoutInflater,
        container : ViewGroup?,
        savedInstanceState : Bundle?,
    ) : View {
        _binding = FragmentPlanBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view : View, savedInstanceState : Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setInit()
        initDatePicker()
        setListeners()
    }

    private fun setListeners() {
        binding.tabLayout.addOnTabSelectedListener(object :
            TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab : TabLayout.Tab?) {
                binding.viewPager.currentItem = tab?.position!!
            }

            override fun onTabUnselected(tab : TabLayout.Tab?) {
            }

            override fun onTabReselected(tab : TabLayout.Tab?) {

            }

        })

        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            tab.text = tabName[position]
        }.attach()

        binding.txtDateSort.setOnClickListener {
            datePickerDialog.show()
        }


    }


    private fun setInit() {
        viewPagerAdapter = PlansViewPagerAdapter(requireActivity())
        binding.viewPager.adapter = viewPagerAdapter
        binding.viewPager.isUserInputEnabled = false
        planViewModel.calendar = Calendar.getInstance()
        planViewModel.getCurrentDate()
        token = sessionManager.getString(Constants.TOKEN)!!
    }

    private fun initDatePicker() {
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
            planViewModel.getMyPlanList(token, planViewModel.currentDate)
        }
    }

}