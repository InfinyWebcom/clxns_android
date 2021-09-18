package com.clxns.app.ui.home.plan

import android.app.DatePickerDialog
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.clxns.app.R
import com.clxns.app.databinding.FragmentPlanBinding
import com.clxns.app.databinding.ListViewFragmentBinding
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint
import java.util.*


@AndroidEntryPoint
class PlanFragment : Fragment() {
    private val viewModel: PlanViewModel by viewModels()
    private var _binding: FragmentPlanBinding? = null
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    lateinit var ctx: Context
    lateinit var viewPagerAdapter: PlansViewPagerAdapter
    lateinit var calendar: Calendar
    var tabName = arrayOf("List View", "Map View")
    private val monthArray =
        arrayOf("Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec")
    private var mYear: Int = 0
    private var mMonth: Int = 0
    private var mDay: Int = 0
    private lateinit var datePickerDialog: DatePickerDialog

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentPlanBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setInit()
        setListeners()
    }

    private fun setListeners() {
        binding.tabLayout.addOnTabSelectedListener(object :
            TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                binding.viewPager.currentItem = tab?.position!!

                Log.i(javaClass.name, "position--->" + tab.position)
                if (tab.position == 1) {
                    binding.relParent.setBackgroundColor((Color.parseColor("#e8eaed")))

//                    myPlanBinding.btnCreateBestRoute.visibility = View.VISIBLE
                } else {
                    binding.relParent.setBackgroundColor(Color.WHITE);

                }

            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {

            }

        })

        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            tab.text = tabName[position]
        }.attach()

        binding.txtDateSort.setOnClickListener {
            datePickerDialog.show()
        }


        //Listener for Calender pop up dialog
        datePickerDialog = DatePickerDialog(ctx, { _, year, monthOfYear, dayOfMonth ->

            val filterDate = "" + dayOfMonth + " " + monthArray[monthOfYear] + ", " + year
            binding.txtDateSort.text = filterDate
            Toast.makeText(context, "Fetching data for $filterDate", Toast.LENGTH_LONG).show()
            mYear = year
            mMonth = monthOfYear
            mDay = dayOfMonth

        }, mYear, mMonth, mDay)

        //Renamed "OK" button to "Filter"
        datePickerDialog.setButton(DatePickerDialog.BUTTON_POSITIVE, "Filter", datePickerDialog)
        //Neutral button which appears on the bottom left corner to reset the date filter to Today
        datePickerDialog.setButton(DatePickerDialog.BUTTON_NEUTRAL, "Reset") { _, _ ->
            getCurrentDate()
            datePickerDialog.updateDate(mYear, mMonth, mDay)
            binding.txtDateSort.text = resources.getString(R.string.today)
        }

        //Setting max date to current date for the calender
        datePickerDialog.datePicker.maxDate = System.currentTimeMillis()
    }

    private fun getCurrentDate() {

        mYear = calendar.get(Calendar.YEAR)
        mMonth = calendar.get(Calendar.MONTH)
        mDay = calendar.get(Calendar.DAY_OF_MONTH)
    }

    private fun setInit() {
        ctx = activity as Context
        viewPagerAdapter = PlansViewPagerAdapter(requireActivity())
        binding.viewPager.adapter = viewPagerAdapter
        binding.viewPager.isUserInputEnabled = false
        binding.tabLayout.setTabTextColors(
            Color.parseColor("#CCFFFFFF"),
            Color.parseColor("#FFFFFF")
        )
        //Initializing Calendar
        calendar = Calendar.getInstance()
        getCurrentDate()
    }

}