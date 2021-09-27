package com.clxns.app.ui.main

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.clxns.app.R
import com.clxns.app.data.api.helper.NetworkResult
import com.clxns.app.data.database.BankDetailsEntity
import com.clxns.app.data.database.DispositionEntity
import com.clxns.app.data.database.SubDispositionEntity
import com.clxns.app.data.preference.SessionManager
import com.clxns.app.databinding.ActivityMainBinding
import com.clxns.app.utils.Constants
import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private val mainViewModel: MainViewModel by viewModels()

    @Inject
    lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        subscribeObserver()

        val token = sessionManager.getString(Constants.TOKEN).toString()
        val navView: BottomNavigationView = binding.navView

        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
//        val appBarConfiguration = AppBarConfiguration(
//            setOf(
//                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications
//            )
//        )
        navView.setOnItemReselectedListener { }
        //setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)


        mainViewModel.getAllDispositions()
        mainViewModel.getBankList(token)
    }

    private fun subscribeObserver() {
        mainViewModel.responseBankList.observe(this) {
            when (it) {
                is NetworkResult.Success -> {
                    Timber.i("Bank List")
                    Timber.i(it.data.toString())
                    if (it.data?.error == false) {
                        val bankList = arrayListOf<BankDetailsEntity>()
                        for (data in it.data.bankData) {
                            bankList.add(
                                BankDetailsEntity(
                                    data.id,
                                    data.name,
                                    data.location,
                                    data.category,
                                    data.description,
                                    Constants.BANK_LOGO_URL + data.fiImage
                                )
                            )
                        }
                        mainViewModel.saveAllBankDetails(bankList)
                    }
                }
                is NetworkResult.Loading -> {
                }
                is NetworkResult.Error -> Timber.i(it.message)
            }
        }
        mainViewModel.responseDisposition.observe(this) {
            when (it) {
                is NetworkResult.Success -> {
                    //Timber.i(it.data.toString())
                    if (it.data?.error == false) {
                        if (it.data.dispositionData.isNotEmpty()) {
                            val dispositionList = arrayListOf<DispositionEntity>()
                            val subDispositionList = arrayListOf<SubDispositionEntity>()
                            for (dis in it.data.dispositionData) {
                                dispositionList.add(DispositionEntity(dis.id, dis.name))
                                if (dis.subDispositionDataList.isNotEmpty()) {
                                    for (subDis in dis.subDispositionDataList) {
                                        subDispositionList.add(
                                            SubDispositionEntity(
                                                subDis.id,
                                                subDis.name,
                                                subDis.dispositionId
                                            )
                                        )
                                    }
                                }
                            }
                            mainViewModel.saveAllDispositions(dispositionList)
                            mainViewModel.saveAllSubDispositions(subDispositionList)
                        }
                    }
                }
                is NetworkResult.Loading -> {
                    Timber.i("Loading...")
                }
                is NetworkResult.Error -> Timber.i("Error")
            }
        }
    }
}