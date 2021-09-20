package com.clxns.app.ui.main.home

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.clxns.app.R
import com.clxns.app.data.api.helper.NetworkResult
import com.clxns.app.data.preference.SessionManager
import com.clxns.app.databinding.FragmentHomeBinding
import com.clxns.app.ui.notification.NotificationActivity
import com.clxns.app.utils.Constants
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class HomeFragment : Fragment() {

    private val homeViewModel: HomeViewModel by viewModels()
    private var _binding: FragmentHomeBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    @Inject
    lateinit var sessionManager: SessionManager

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        homeViewModel.getAllDispositions()

        initView()

        setListeners()

        subscribeObserver()

    }

    private fun subscribeObserver() {
        homeViewModel.responseDisposition.observe(viewLifecycleOwner) {
            when (it) {
                is NetworkResult.Success -> {
                    Timber.i(it.data.toString())
                }
                is NetworkResult.Loading -> {
                    Timber.i("Loading...")
                }
                is NetworkResult.Error -> Timber.i("Error")
            }
        }
    }

    private fun initView() {
        binding.usernameTv.text = sessionManager.getString(Constants.USER_NAME)
    }

    private fun setListeners() {
        binding.notificationBtn.setOnClickListener {
            startActivity(Intent(context, NotificationActivity::class.java))
        }

        binding.summaryCardView.setOnClickListener {
            findNavController().navigate(R.id.action_navigation_home_to_navigation_home_summary)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}