package com.clxns.app.ui.cases

import android.app.ActivityOptions
import android.content.Intent
import android.os.Bundle
import android.util.Pair
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.clxns.app.R
import com.clxns.app.databinding.FragmentCasesBinding
import com.clxns.app.ui.search.SearchActivity
import com.clxns.app.utils.Constants
import com.clxns.app.utils.Status
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

@AndroidEntryPoint
class CasesFragment : Fragment() {

    private val casesViewModel: CasesViewModel by viewModels()
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
        casesViewModel.getCasesList(Constants.TOKEN_TEMP)
        casesViewModel.casesResponse.observe(viewLifecycleOwner,{
          when(it.status){
              Status.SUCCESS -> binding.casesRv.apply {
                  layoutManager = LinearLayoutManager(context)
                  adapter = CasesAdapter(it.data?.data!!)
              }
              Status.ERROR -> Timber.i("Error loading")
              else -> Timber.i("Nothing")
          }
        })

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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}