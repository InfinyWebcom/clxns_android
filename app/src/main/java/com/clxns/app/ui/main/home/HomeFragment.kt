package com.clxns.app.ui.main.home

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioGroup
import android.widget.RelativeLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.clxns.app.R
import com.clxns.app.data.api.helper.NetworkResult
import com.clxns.app.data.model.home.ActionsData
import com.clxns.app.data.model.home.HomeStatsData
import com.clxns.app.data.model.home.StatsData
import com.clxns.app.data.model.home.SummaryData
import com.clxns.app.data.preference.SessionManager
import com.clxns.app.databinding.FragmentHomeBinding
import com.clxns.app.ui.notification.NotificationActivity
import com.clxns.app.utils.*
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class HomeFragment : Fragment(), RadioGroup.OnCheckedChangeListener {

    private val homeViewModel: HomeViewModel by viewModels()
    private var _binding: FragmentHomeBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    @Inject
    lateinit var sessionManager: SessionManager

    private lateinit var actionsData: ActionsData
    private lateinit var statsData: StatsData
    private lateinit var summaryData: SummaryData

    private lateinit var todayData: HomeStatsData
    private lateinit var weekData: HomeStatsData
    private lateinit var monthData: HomeStatsData

    private lateinit var noDataLayout: RelativeLayout

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

        getHomeStatistics()

        initView()

        setListeners()

        subscribeObserver()

    }

    private fun getHomeStatistics() {
        homeViewModel.getHomeStatsData(sessionManager.getString(Constants.TOKEN)!!)
    }

    private fun subscribeObserver() {
        homeViewModel.responseHomeStats.observe(viewLifecycleOwner) {
            when (it) {
                is NetworkResult.Success -> {
                    binding.homeProgressBar.hide()
                    noDataLayout.hide()
                    binding.homeScrollView.show()
                    if (it.data?.error == false) {
                        todayData = it.data.todayData!!
                        weekData = it.data.weekData!!
                        monthData = it.data.monthData!!

                        //Updating UI for Today
                        summaryData = todayData.summaryData!!
                        updateHomeStatsUI(todayData.actionsData!!, todayData.stats!!)
                    } else {
                        noDataLayout.show()
                        binding.homeScrollView.hide()
                    }
                }
                is NetworkResult.Loading -> {
                    noDataLayout.hide()
                    binding.homeScrollView.hide()
                    binding.homeProgressBar.show()
                }
                is NetworkResult.Error -> {
                    binding.homeProgressBar.hide()
                    binding.homeScrollView.hide()
                    noDataLayout.show()
                    binding.root.snackBar(it.message!!)
                }
            }
        }
    }

    private fun initView() {
        binding.homeDaysContainer.setOnCheckedChangeListener(this)
        binding.usernameTv.text = sessionManager.getString(Constants.USER_NAME)
        noDataLayout = binding.homeNoData.root
    }

    private fun setListeners() {

        binding.homeSwipeRefresh.setOnRefreshListener {
            binding.homeSwipeRefresh.isRefreshing = false
            getHomeStatistics()
        }
        binding.notificationBtn.setOnClickListener {
            startActivity(Intent(context, NotificationActivity::class.java))
        }

        binding.summaryCardView.setOnClickListener {
            val actions =
                HomeFragmentDirections.actionNavigationHomeToNavigationHomeSummary(summaryData)
            findNavController().navigate(actions)
        }

        binding.homeNoData.retryBtn.setOnClickListener {
            getHomeStatistics()
        }

        binding.visitPendingCard.setOnClickListener {
            val actions = HomeFragmentDirections.actionNavigationHomeToNavigationCases(
                0,
                1, 0
            )
            findNavController().navigate(actions)
        }
        binding.followUpCard.setOnClickListener {
            val actions = HomeFragmentDirections.actionNavigationHomeToNavigationCases(
                0,
                0, 1
            )
            findNavController().navigate(actions)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onCheckedChanged(group: RadioGroup?, checkedId: Int) {
        when (checkedId) {
            R.id.rbToday -> {
                actionsData = todayData.actionsData!!
                statsData = todayData.stats!!
                summaryData = todayData.summaryData!!
                updateHomeStatsUI(actionsData, statsData)
            }
            R.id.rbWeek -> {
                actionsData = weekData.actionsData!!
                statsData = weekData.stats!!
                summaryData = weekData.summaryData!!
                updateHomeStatsUI(actionsData, statsData)
            }
            R.id.rbMonth -> {
                actionsData = monthData.actionsData!!
                statsData = monthData.stats!!
                summaryData = monthData.summaryData!!
                updateHomeStatsUI(actionsData, statsData)
            }
        }
    }

    private fun updateHomeStatsUI(actionsData: ActionsData, statsData: StatsData) {
        binding.casesAllocatedCountTv.text = statsData.cases.toString()
        binding.totalPosAmountTv.text = statsData.pos.convertToCurrency()
        binding.totalDueAmountTv.text = statsData.totalAmountDue.convertToCurrency()
        binding.amountCollectedTv.text = statsData.totalCollectedAmt.convertToCurrency()

        binding.visitPendingTv.text = actionsData.pendingVisit.toString()
        binding.followUpTv.text = actionsData.pendingFollowUp.toString()
    }
}