package com.clxns.app.ui.cases

import android.app.ActivityOptions
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
import androidx.recyclerview.widget.RecyclerView
import com.clxns.app.R
import com.clxns.app.data.api.helper.NetworkResult
import com.clxns.app.data.model.CasesData
import com.clxns.app.data.preference.SessionManager
import com.clxns.app.databinding.FragmentCasesBinding
import com.clxns.app.ui.search.SearchActivity
import com.clxns.app.utils.*
import dagger.hilt.android.AndroidEntryPoint
import java.text.NumberFormat
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

    private var casesDataList: MutableList<CasesData> = mutableListOf()
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
        viewModel.getCasesList(sessionManager.getString(Constants.TOKEN)!!, "")

        casesAdapter = CasesAdapter(requireContext(), casesDataList){
            Toast.makeText(context, it.name, Toast.LENGTH_SHORT).show()
        }
        casesRV = binding.casesRv
        casesRV.layoutManager = LinearLayoutManager(requireContext())
        casesRV.adapter = casesAdapter

        setObserver()

        if (args.dispositionId != 0){
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
        viewModel.response.observe(viewLifecycleOwner) { response ->
            when (response) {
                is NetworkResult.Success -> {
                    binding.progressBar.hide()
                    if (!response.data?.error!! && response.data.casesDataList.isNotEmpty()){
                        casesDataList.addAll(response.data.casesDataList)
                        casesAdapter.notifyItemRangeChanged(0, casesDataList.size)
                        binding.casesAllottedTv.text = "Cases : " + casesDataList.size
                        var amountCollected = 0
                        for (temp in casesDataList){
                            amountCollected += temp.totalDueAmount
                        }
                        val amount = NumberFormat.getCurrencyInstance(Locale("en", "in")).format(amountCollected)
                        binding.amountCollectedTv.text = "Collectable : " + amount.substringBefore('.')
                    }else{
                        binding.root.snackBar("Nothing found")
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}