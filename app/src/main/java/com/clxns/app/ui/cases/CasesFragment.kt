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
import androidx.recyclerview.widget.LinearLayoutManager
import com.clxns.app.R
import com.clxns.app.data.api.helper.NetworkResult
import com.clxns.app.databinding.FragmentCasesBinding
import com.clxns.app.ui.search.SearchActivity
import com.clxns.app.utils.*
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

@AndroidEntryPoint
class CasesFragment : Fragment() {

    private val viewModel: CasesViewModel by viewModels()
    private var _binding: FragmentCasesBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

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
//        viewModel.getCasesList(Constants.TOKEN_TEMP)
//        viewModel.casesResponse.observe(viewLifecycleOwner, { it ->
//            when (it.status) {
//                Status.SUCCESS -> binding.casesRv.apply {
//                    layoutManager = LinearLayoutManager(context)
//                    adapter = CasesAdapter(it.data?.data!!) {
//                        Toast.makeText(context, it.name, Toast.LENGTH_SHORT).show()
//                    }
//                }
//                Status.ERROR -> Timber.i("Error loading")
//                else -> Timber.i("Nothing")
//            }
//        })
        setObserver()

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
        viewModel.getCasesList(Constants.TOKEN_TEMP)
        viewModel.response.observe(viewLifecycleOwner) { response ->
            when (response) {
                is NetworkResult.Success -> {
                    binding.progressBar.hide()

                    binding.casesRv.apply {
                        layoutManager = LinearLayoutManager(context)
                        adapter = CasesAdapter(response.data?.data!!) {
                            Toast.makeText(context, it.name, Toast.LENGTH_SHORT).show()
                        }
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