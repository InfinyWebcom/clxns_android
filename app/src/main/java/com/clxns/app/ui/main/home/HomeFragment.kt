package com.clxns.app.ui.main.home

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioGroup
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
import com.clxns.app.utils.Constants
import com.clxns.app.utils.convertToCurrency
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
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

        homeViewModel.getHomeStatsData(sessionManager.getString(Constants.TOKEN)!!)

        initView()

        setListeners()

        subscribeObserver()

    }

    private fun subscribeObserver() {
        homeViewModel.responseHomeStats.observe(viewLifecycleOwner) {
            when (it) {
                is NetworkResult.Success -> {
                    if (it.data?.error == false) {
                        todayData = it.data.todayData!!
                        weekData = it.data.weekData!!
                        monthData = it.data.monthData!!

                        //Updating UI for Today
                        summaryData = todayData.summaryData!!
                        updateHomeStatsUI(todayData.actionsData!!, todayData.stats!!)
                    }
                }
                is NetworkResult.Loading -> {
                    Timber.i("Loading...")
                }
                is NetworkResult.Error -> Timber.i("Error")
            }
        }
    }

    private fun initView() {
        binding.homeDaysContainer.setOnCheckedChangeListener(this)
        binding.usernameTv.text = sessionManager.getString(Constants.USER_NAME)
    }

    private fun setListeners() {
        binding.notificationBtn.setOnClickListener {
            startActivity(Intent(context, NotificationActivity::class.java))
        }

        binding.summaryCardView.setOnClickListener {
            val actions =
                HomeFragmentDirections.actionNavigationHomeToNavigationHomeSummary(summaryData)
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