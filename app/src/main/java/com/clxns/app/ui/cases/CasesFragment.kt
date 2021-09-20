package com.clxns.app.ui.cases

import android.app.ActivityOptions
import android.app.DatePickerDialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.util.Pair
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.clxns.app.R
import com.clxns.app.data.api.helper.NetworkResult
import com.clxns.app.data.model.CasesData
import com.clxns.app.data.preference.SessionManager
import com.clxns.app.databinding.FragmentCasesBinding
import com.clxns.app.ui.search.SearchActivity
import com.clxns.app.utils.*
import dagger.hilt.android.AndroidEntryPoint
import java.util.*
import javax.inject.Inject

@AndroidEntryPoint
class CasesFragment : Fragment() {

    private val viewModel: CasesViewModel by viewModels()
    private var _binding: FragmentCasesBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    @Inject
    lateinit var sessionManager: SessionManager

    private val args: CasesFragmentArgs by navArgs()

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
        viewModel.getCasesList(sessionManager.getString(Constants.TOKEN)!!)

        setObserver()

        if (args.dispositionId != 0) {
            binding.root.snackBar(args.dispositionId.toString())
        }

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

    private fun setObserver() {
        viewModel.responseCaseList.observe(viewLifecycleOwner) { response ->
            when (response) {
                is NetworkResult.Success -> {
                    // bind data to the view
                    binding.progressBar.hide()

                    if (!response.data?.error!!) {
                        if (response.data.total > 0) {
                            binding.txtNoData.visibility = View.GONE

                            binding.casesAllottedTv.text =
                                "Cases: " + response.data?.total.toString()
                            binding.amountCollectedTv.text =
                                "Collectable : ₹${response.data?.amountCollected.toString()}"
                            binding.casesRv.apply {
                                layoutManager = LinearLayoutManager(context)
                                adapter = CasesAdapter(requireContext(), response.data?.data!!) {
                                    showPlanDialog(it)
                                }
                            }

                        } else {
                            binding.txtNoData.visibility = View.VISIBLE
                        }
                    } else {
                        requireContext().toast(response.data.title)
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
                }
            }
        }

        viewModel.responseAddToPlan.observe(viewLifecycleOwner) { response ->
            when (response) {
                is NetworkResult.Success -> {
                    binding.progressBar.hide()
                    requireContext().toast(response.data?.title!!)
//                    if (!response.data?.error!!) {
//
//                    } else {
//                        requireContext().toast(response.data.title!!)
//                    }
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

    private fun showPlanDialog(casesData: CasesData) {
        val cal = Calendar.getInstance()
        val dateSetListener =
            DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
                cal.set(Calendar.YEAR, year)
                cal.set(Calendar.MONTH, monthOfYear)
                cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)

                binding.progressBar.show()

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

//        datePicker.setButton(
//            DialogInterface.BUTTON_POSITIVE,
//            "Plan"
//        ) { _, _ ->
//            Toast.makeText(context, "Click.", Toast.LENGTH_LONG).show()
//
////            viewModel.addToPlan(sessionManager.getString(Constants.TOKEN)!!,casesData.loanAccountNo,date)
//        }
        datePicker.show()

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}