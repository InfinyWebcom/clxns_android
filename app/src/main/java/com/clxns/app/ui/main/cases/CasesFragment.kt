package com.clxns.app.ui.main.cases

import android.app.ActivityOptions
import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.util.Pair
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.clxns.app.R
import com.clxns.app.data.api.helper.NetworkResult
import com.clxns.app.data.model.cases.CasesData
import com.clxns.app.data.preference.SessionManager
import com.clxns.app.databinding.FragmentCasesBinding
import com.clxns.app.ui.search.SearchActivity
import com.clxns.app.utils.*
import dagger.hilt.android.AndroidEntryPoint
import java.util.*
import javax.inject.Inject

@AndroidEntryPoint
class CasesFragment : Fragment(), CasesAdapter.OnCaseItemClickListener {

    private val viewModel: CasesViewModel by viewModels()
    private var _binding: FragmentCasesBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    @Inject
    lateinit var sessionManager: SessionManager

    private val casesArgs: CasesFragmentArgs by navArgs()

    private var casesDataList: ArrayList<CasesData> = arrayListOf()
    private lateinit var casesAdapter: CasesAdapter
    private lateinit var casesRV: RecyclerView

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

        getCaseList()
    }

    private fun getCaseList() {
        viewModel.getCasesList(
            sessionManager.getString(Constants.TOKEN)!!,
            "", "", "", "", ""
        )
    }

    private fun initView() {
        casesRV = binding.casesRv
        casesRV.layoutManager = LinearLayoutManager(requireContext())
        casesAdapter = CasesAdapter(requireContext(), casesDataList, this)
        casesRV.adapter = casesAdapter
    }

    private fun setListeners() {
        binding.filterBtn.setOnClickListener {
            findNavController().navigate(R.id.action_navigation_cases_to_navigation_cases_filter)
        }

        binding.searchCard.setOnClickListener {
            val i = Intent(requireContext(), SearchActivity::class.java)
            val p = Pair<View, String>(binding.searchCard, "search_bar")
            val options = ActivityOptions.makeSceneTransitionAnimation(requireActivity(), p)
            startActivity(i, options.toBundle())
        }
    }

    private fun subscribeObserver() {
        viewModel.responseCaseList.observe(viewLifecycleOwner) { response ->
            when (response) {
                is NetworkResult.Success -> {
                    // bind data to the view
                    binding.progressBar.hide()
                    casesDataList.clear()
                    casesAdapter.notifyDataSetChanged()
                    if (response.data?.error == false) {
                        val totalCases =
                            getString(R.string.cases_toolbar_txt) + response.data.total.toString()
                        binding.casesAllottedTv.text = totalCases
                        val collectable =
                            getString(R.string.cases_collectable_txt) + (response.data.collectable?.convertToCurrency()
                                ?: "-")
                        binding.amountCollectedTv.text = collectable
                        if (!response.data.casesDataList.isNullOrEmpty()) {
                            binding.txtNoData.visibility = View.GONE
                            val dataList = response.data.casesDataList
                            casesDataList.addAll(dataList)
                            casesAdapter.notifyItemRangeChanged(0, casesDataList.size)
                        } else {
                            binding.txtNoData.visibility = View.VISIBLE
                        }
                    } else {
                        requireContext().toast(response.data?.title!!)
                    }
                }
                is NetworkResult.Error -> {
                    binding.progressBar.hide()
                    requireContext().toast(response.message!!)
                    // show error message
                }
                is NetworkResult.Loading -> {
                    binding.progressBar.show()
                    // show a progress bar
                    binding.casesAllottedTv.text = getString(R.string.cases_toolbar_txt)
                    binding.amountCollectedTv.text = getString(R.string.cases_collectable_txt)
                }
            }
        }

        viewModel.responseAddToPlan.observe(viewLifecycleOwner) { response ->
            when (response) {
                is NetworkResult.Success -> {
                    binding.progressBar.hide()
                    requireContext().toast(response.data?.title!!)
                    casesDataList.clear()
                    casesAdapter.notifyItemRangeChanged(0, casesDataList.size)
                    getCaseList()
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

        viewModel.responseRemovePlan.observe(viewLifecycleOwner) {
            when (it) {
                is NetworkResult.Success -> {
                    binding.progressBar.hide()
                    requireContext().toast(it.data?.title!!)
                    if (!it.data.error) {
                        casesDataList.clear()
                        casesAdapter.notifyItemRangeChanged(0, casesDataList.size)
                        getCaseList()
                    }
                }
                is NetworkResult.Error -> {
                    binding.progressBar.hide()
                    requireContext().toast(it.message!!)
                    // show error message
                }
                is NetworkResult.Loading -> {
                    binding.progressBar.show()
                    // show a progress bar
                }
            }
        }
    }

    private fun showPlanDialog(casesData: CasesData) {
        val cal = Calendar.getInstance()
        val dateSetListener =
            DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
                cal.set(Calendar.YEAR, year)
                cal.set(Calendar.MONTH, monthOfYear)
                cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)

                viewModel.addToPlan(
                    sessionManager.getString(Constants.TOKEN)!!,
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

    private fun showConfirmUnPlanDialog(casesData: CasesData) {
        val logoutDialog = AlertDialog.Builder(requireContext())
        logoutDialog.setTitle("UnPlan -> ${casesData.name}")
        logoutDialog.setMessage("Are you sure want to un-plan this case?")

        logoutDialog.setPositiveButton("Yes") { dialog, _ ->
            viewModel.removePlan(
                sessionManager.getString(Constants.TOKEN)!!,
                casesData.loanAccountNo.toString()
            )
            dialog.dismiss()
        }.setNegativeButton("Cancel") { dialog, _ -> dialog.dismiss() }

        val d = logoutDialog.create()
        d.show()
        d.getButton(AlertDialog.BUTTON_POSITIVE).isAllCaps = false
        d.getButton(AlertDialog.BUTTON_NEGATIVE).isAllCaps = false
    }


}