package com.clxns.app.ui.main.plan.plannedLeads

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.RecyclerView
import com.clxns.app.R
import com.clxns.app.data.api.helper.NetworkResult
import com.clxns.app.data.preference.SessionManager
import com.clxns.app.databinding.FragmentMyPlanBinding
import com.clxns.app.ui.main.cases.caseDetails.DetailsActivity
import com.clxns.app.utils.*
import dagger.hilt.android.AndroidEntryPoint
import java.util.*
import javax.inject.Inject


@AndroidEntryPoint
class MyPlanFragment : Fragment(), MyPlanAdapter.OnPlanItemClickListener {

    private val planViewModel : MyPlanViewModel by activityViewModels()
    private var _binding : FragmentMyPlanBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    @Inject
    lateinit var sessionManager : SessionManager

    private lateinit var token : String
    private lateinit var noDataLayout : RelativeLayout
    private lateinit var planRV : RecyclerView

    override fun onCreateView(
        inflater : LayoutInflater,
        container : ViewGroup?,
        savedInstanceState : Bundle?,
    ) : View {
        _binding = FragmentMyPlanBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view : View, savedInstanceState : Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()

        setListeners()

        subscribeObserver()

        getPlanList()
    }

    override fun onResume() {
        super.onResume()
        //This code will run only once to refresh the plan list on receiving the intent data from main the activity,
        // only if the main activity has received intent data from the payment activity on successful payment
        val intentReceivedFromMainActivity = requireActivity().intent
        if (intentReceivedFromMainActivity != null && intentReceivedFromMainActivity.getBooleanExtra(
                "hasChangedPlanStatus",
                false
            )
        ) {
            val filterAPIDate = "${planViewModel.mYear}-${
                String.format(
                    "%02d",
                    planViewModel.mMonth + 1
                )
            }-${String.format("%02d", planViewModel.mDay)}"
            getPlanList(filterAPIDate)
            //Setting intent data to null otherwise it's gonna keep refreshing when being resumed from the paused state
            requireActivity().intent = null
        }
    }

    private fun initView() {
        //Initializing Calendar to get the current date
        planViewModel.calendar = Calendar.getInstance()
        planViewModel.getCurrentDate()
        noDataLayout = binding.myPlanNoDataLayout.root
        planRV = binding.myPlanRecyclerView
        token = sessionManager.getString(Constants.TOKEN)!!

    }

    private fun setListeners() {

        binding.myPlanNoDataLayout.retryBtn.setOnClickListener {
            getPlanList()
        }
    }

    private fun getPlanList(date : String = planViewModel.currentDate) {
        planViewModel.getMyPlanList(token, date)
    }

    private fun subscribeObserver() {
        planViewModel.response.observe(viewLifecycleOwner) { response ->
            when (response) {
                is NetworkResult.Success -> {
                    binding.myPlanProgressBar.hide()
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
                            binding.myPlanNoDataLayout.noDataTxt.text = getString(R.string.no_data)
                            noDataLayout.show()
                            binding.myPlanNoDataLayout.retryBtn.hide()
                            planRV.hide()
                        }
                    } else {
                        noDataLayout.show()
                        binding.myPlanNoDataLayout.noDataTxt.text = response.data.title
                        planRV.hide()
                    }
                    // bind data to the view
                }
                is NetworkResult.Error -> {
                    binding.myPlanProgressBar.hide()
                    planRV.hide()
                    noDataLayout.show()
                    binding.root.snackBar(response.message!!)
                    // show error message
                }
                is NetworkResult.Loading -> {
                    binding.myPlanProgressBar.show()
                    noDataLayout.hide()
                    // show a progress bar
                }
            }
        }
    }

    private val startDetailActivityForResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == Activity.RESULT_OK) {
                val data : Intent? = it.data
                val status = data?.getBooleanExtra("hasChangedPlanStatus", false)
                if (status == true) {
                    val filterAPIDate = "${planViewModel.mYear}-${
                        String.format(
                            "%02d",
                            planViewModel.mMonth + 1
                        )
                    }-${String.format("%02d", planViewModel.mDay)}"
                    getPlanList(filterAPIDate)
                }
            }
        }

    override fun openDetailActivity(
        loadId : String,
        name : String,
        dispositions : String,
        isPlanned : Boolean
    ) {
        val intent = Intent(context, DetailsActivity::class.java)
        intent.putExtra("loan_account_number", loadId)
        intent.putExtra("isPlanned", true)
        intent.putExtra("status", dispositions)
        intent.putExtra("name", name)
        startDetailActivityForResult.launch(intent)
    }

}