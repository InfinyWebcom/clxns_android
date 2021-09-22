package com.clxns.app.ui.main

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.clxns.app.R
import com.clxns.app.data.api.helper.NetworkResult
import com.clxns.app.data.database.DispositionEntity
import com.clxns.app.data.database.LocalDataSource
import com.clxns.app.data.database.SubDispositionEntity
import com.clxns.app.data.preference.SessionManager
import com.clxns.app.databinding.ActivityMainBinding
import com.clxns.app.ui.main.home.HomeViewModel
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

    @Inject
    lateinit var localDataSource: LocalDataSource

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

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
        mainViewModel.getHomeStatsData(token)
        mainViewModel.getAll()
        subscribeObserver()
    }

    private fun subscribeObserver() {

        mainViewModel.response.observe(this) {
            Timber.i("Printing DB")
            Timber.i(it.toString())
        }
        mainViewModel.responseDisposition.observe(this) {
            when (it) {
                is NetworkResult.Success -> {
                    //Timber.i(it.data.toString())
                    if (it.data?.error == false) {
                        if (it.data.data.isNotEmpty()) {
                            val dispositionList = arrayListOf<DispositionEntity>()
                            val subDispositionList = arrayListOf<SubDispositionEntity>()
                            for (dis in it.data.data) {
                                dispositionList.add(DispositionEntity(dis.id, dis.name))
                                if (dis.subDispositionList.isNotEmpty()) {
                                    for (subDis in dis.subDispositionList) {
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

        mainViewModel.responseHomeStats.observe(this) {
            when (it) {
                is NetworkResult.Success -> {
                    //Timber.i(it.data.toString())
                }
                is NetworkResult.Loading -> {
                    Timber.i("Loading...")
                }
                is NetworkResult.Error -> Timber.i("Error")
            }
        }
    }
}