package com.clxns.app.ui.main.cases

import android.annotation.SuppressLint
import android.app.Activity
import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityOptionsCompat
import androidx.core.util.Pair
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.clxns.app.R
import com.clxns.app.data.api.helper.NetworkResult
import com.clxns.app.data.model.cases.CasesData
import com.clxns.app.data.preference.SessionManager
import com.clxns.app.databinding.FragmentCasesBinding
import com.clxns.app.ui.main.cases.casedetails.DetailsActivity
import com.clxns.app.ui.search.SearchActivity
import com.clxns.app.utils.*
import com.google.android.material.button.MaterialButton
import dagger.hilt.android.AndroidEntryPoint
import java.util.*
import javax.inject.Inject

@AndroidEntryPoint
class CasesFragment : Fragment(), CasesAdapter.OnCaseItemClickListener {

    private val viewModel: CasesViewModel by activityViewModels()
    private var _binding: FragmentCasesBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    @Inject
    lateinit var sessionManager: SessionManager

    private lateinit var noDataLayout: RelativeLayout
    private lateinit var filterBtn: MaterialButton

    private val casesArgs: CasesFragmentArgs by navArgs()

    private var casesDataList: ArrayList<CasesData> = arrayListOf()
    private lateinit var casesAdapter: CasesAdapter
    private lateinit var casesRV: RecyclerView
    private lateinit var token: String

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCasesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initView()

        subscribeObserver()

        setListeners()

        binding.casesProgressBar.show()

        //If user is navigating from home case summary bottom sheet
        if (casesArgs.dispositionId != 0) {
            filterBtn.text = getString(R.string.reset)
            viewModel.getCasesList(
                token, "", casesArgs.dispositionId.toString(),
                "", "", "",
                "",
                ""
            )
        } else if (casesArgs.visitPending != 0) {
            filterBtn.text = getString(R.string.reset)
            viewModel.getCasesList(
                token, "", "",
                "", "", "",
                "1",
                ""
            )
        } else if (casesArgs.followUps!= 0) {
            filterBtn.text = getString(R.string.reset)
            viewModel.getCasesList(
                token, "", "",
                "", "", "",
                "",
                "1"
            )
        } else {
            getCaseList()
        }
    }

    private fun getCaseList() {
        viewModel.getCasesList(
            token,
            "", "", "", "", "",
            "",
            ""
        )
    }

    private fun initView() {
        casesRV = binding.casesRv
        casesRV.layoutManager = LinearLayoutManager(requireContext())
        casesAdapter = CasesAdapter(requireContext(), casesDataList, this)
        casesRV.adapter = casesAdapter

        token = sessionManager.getString(Constants.TOKEN)!!

        noDataLayout = binding.casesNoData.root
        filterBtn = binding.filterBtn
    }

    private fun setListeners() {
        filterBtn.setOnClickListener {
            if (filterBtn.text.equals("Filter")) {
                findNavController().navigate(R.id.action_navigation_cases_to_navigation_cases_filter)
            } else {
                filterBtn.text = getString(R.string.filter)
                getCaseList()
            }
        }

        binding.searchCard.setOnClickListener {
            val i = Intent(requireContext(), SearchActivity::class.java)
            val p = Pair<View, String>(binding.searchCard, "search_bar")
            val options = ActivityOptionsCompat.makeSceneTransitionAnimation(requireActivity(), p)
            startDetailActivityForResult.launch(i, options)
        }

        binding.casesNoData.retryBtn.setOnClickListener {
            getCaseList()
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun subscribeObserver() {
        viewModel.responseCaseList.observe(viewLifecycleOwner) { response ->
            binding.casesNoData.noDataTv.text = getString(R.string.something_went_wrong)
            if (lifecycle.currentState.isAtLeast(Lifecycle.State.RESUMED)) {
                when (response) {
                    is NetworkResult.Success -> {
                        // bind data to the view
                        binding.casesProgressBar.hide()
                        noDataLayout.hide()
                        casesRV.show()
                        if (response.data?.error == false) {
                            val totalCases =
                                getString(R.string.cases_toolbar_txt) + response.data.total.toString()
                            binding.casesAllottedTv.text = totalCases
                            val collectable =
                                getString(R.string.cases_collectable_txt) + (response.data.collectable?.convertToCurrency()
                                    ?: "-")
                            binding.amountCollectedTv.text = collectable
                            if (!response.data.casesDataList.isNullOrEmpty()) {
                                clearAndNotifyAdapter()
                                val dataList = response.data.casesDataList
                                casesDataList.addAll(dataList)
                                casesAdapter.notifyDataSetChanged()
                            } else {
                                binding.casesNoData.noDataTv.text = getString(R.string.no_data)
                                binding.casesNoData.retryBtn.hide()
                                noDataLayout.show()
                                val size = casesDataList.size
                                casesDataList.clear()
                                casesAdapter.notifyItemRangeRemoved(0, size)
                            }
                        } else {
                            binding.casesNoData.noDataTv.text = response.data?.title
                            noDataLayout.show()
                            casesRV.hide()
                        }
                    }
                    is NetworkResult.Error -> {
                        binding.casesProgressBar.hide()
                        noDataLayout.show()
                        casesRV.hide()
                        binding.root.snackBar(response.message!!)
                        // show error message
                    }
                    is NetworkResult.Loading -> {
                        noDataLayout.hide()
                        binding.casesAllottedTv.text = getString(R.string.cases_toolbar_txt)
                        binding.amountCollectedTv.text = getString(R.string.cases_collectable_txt)
                    }
                }
            }
        }

        viewModel.responseAddToPlan.observe(viewLifecycleOwner) { response ->
            if (lifecycle.currentState.isAtLeast(Lifecycle.State.RESUMED)) {
                when (response) {
                    is NetworkResult.Success -> {
                        binding.root.snackBar(response.data?.title!!)
                        getCaseList()
                    }
                    is NetworkResult.Error -> {
                        binding.root.snackBar(response.message!!)
                        // show error message
                    }
                    is NetworkResult.Loading -> {
                        binding.root.snackBar("Adding to my plan...")
                        // show a progress bar
                    }
                }
            }
        }

        viewModel.responseRemovePlan.observe(viewLifecycleOwner) {
            if (lifecycle.currentState.isAtLeast(Lifecycle.State.RESUMED)) {
                when (it) {
                    is NetworkResult.Success -> {
                        binding.root.snackBar(it.data?.title!!)
                        if (!it.data.error) {
                            getCaseList()
                        }
                    }
                    is NetworkResult.Error -> {
                        binding.root.snackBar(it.message!!)
                        // show error message
                    }
                    is NetworkResult.Loading -> {
                        binding.root.snackBar("Removing from my plan...")
                        // show a progress bar
                    }
                }
            }
        }
    }

    private fun clearAndNotifyAdapter() {
        val size = casesDataList.size
        casesDataList.clear()
        casesAdapter.notifyItemRangeChanged(0, size)
    }

    private fun showPlanDialog(casesData: CasesData) {
        val cal = Calendar.getInstance()
        val dateSetListener =
            DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
                cal.set(Calendar.YEAR, year)
                cal.set(Calendar.MONTH, monthOfYear)
                cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)

                viewModel.addToPlan(
                    token,
                    casesData.loanAccountNo.toString(),
                    "${year}-${monthOfYear + 1}-${dayOfMonth}"
                )
            }
        val datePicker = DatePickerDialog(
            requireActivity(),
            dateSetListener,
            // set DatePickerDialog to point to today's date when it loads up
            cal.get(Calendar.YEAR),
            cal.get(Calendar.MONTH),
            cal.get(Calendar.DAY_OF_MONTH)
        )
        datePicker.datePicker.minDate = cal.timeInMillis
        datePicker.show()

    }

    private fun showConfirmUnPlanDialog(casesData: CasesData) {
        val logoutDialog = AlertDialog.Builder(requireContext())
        logoutDialog.setTitle("UnPlan -> ${casesData.name}")
        logoutDialog.setMessage("Are you sure you want to un-plan this case?")

        logoutDialog.setPositiveButton("Yes") { dialog, _ ->
            viewModel.removePlan(
                token,
                casesData.loanAccountNo.toString()
            )
            dialog.dismiss()
        }.setNegativeButton("Cancel") { dialog, _ -> dialog.dismiss() }

        val d = logoutDialog.create()
        d.show()
        d.getButton(AlertDialog.BUTTON_POSITIVE).isAllCaps = false
        d.getButton(AlertDialog.BUTTON_NEGATIVE).isAllCaps = false
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onPlanClick(isPlanned: Boolean, casesData: CasesData) {
        if (isPlanned) {
            showConfirmUnPlanDialog(casesData)
        } else {
            showPlanDialog(casesData)
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
        intent.putExtra("status", dispositions)
        intent.putExtra("name", name)
        intent.putExtra("isPlanned", isPlanned)
        intent.putExtra("isCaseDetail", true)
        startDetailActivityForResult.launch(intent)
    }

    private val startDetailActivityForResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == Activity.RESULT_OK) {
                val data: Intent? = it.data
                val status = data?.getBooleanExtra("hasChangedPlanStatus", false)
                if (status == true) {
                    getCaseList()
                }
            }
        }


}