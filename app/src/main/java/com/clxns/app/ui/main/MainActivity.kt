package com.clxns.app.ui.main

import android.animation.ValueAnimator
import android.content.Intent
import android.content.IntentSender.SendIntentException
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
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
import com.clxns.app.utils.Constants.APP_UPDATE_RC
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.snackbar.Snackbar
import com.google.android.play.core.appupdate.AppUpdateInfo
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.appupdate.AppUpdateOptions
import com.google.android.play.core.install.InstallStateUpdatedListener
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.InstallStatus
import com.google.android.play.core.install.model.UpdateAvailability
import com.google.android.play.core.tasks.Task
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import javax.inject.Inject


@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding : ActivityMainBinding

    private val mainViewModel : MainViewModel by viewModels()

    @Inject
    lateinit var sessionManager : SessionManager

    private lateinit var appUpdateManager : AppUpdateManager
    private lateinit var listener : InstallStateUpdatedListener

    private lateinit var navController : NavController
    private lateinit var snackbar : Snackbar

    override fun onCreate(savedInstanceState : Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        subscribeObserver()

        val token = sessionManager.getString(Constants.TOKEN).toString()
        val navView : BottomNavigationView = binding.navView

        navController = findNavController(R.id.nav_host_fragment_activity_main)

        snackbar = Snackbar.make(binding.root, "Press back again to exit", Snackbar.LENGTH_LONG);

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

        //Will be removed
        //Timber.i("MAIN_TOKEN -> ${sessionManager.getString(Constants.TOKEN)!!}")

        checkForAppUpdate()
        mainViewModel.getAllDispositions()
        mainViewModel.getBankList(token)
    }

    override fun onNewIntent(intent : Intent?) {
        super.onNewIntent(intent)
        /**
         * This will refresh the my plan fragment only once after successful payment
         * It only gets triggered from the payment activity on by clearing back stack
         * up to this activity as it's been Launched in Single Top mode (Only One Instance)
         */
        if (intent != null) {
            if (intent.getBooleanExtra("hasChangedPlanStatus", false)) {
                setIntent(intent)
            }
        }
    }

    private fun subscribeObserver() {
        mainViewModel.responseBankList.observe(this) {
            when (it) {
                is NetworkResult.Success -> {
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

    private fun checkForAppUpdate() {
        appUpdateManager = AppUpdateManagerFactory.create(this)

        listener = InstallStateUpdatedListener {
            if (it.installStatus() == InstallStatus.DOWNLOADING) {
                binding.appUpdateProgressBar.show()
                binding.appUpdateProgressBar.progress = it.bytesDownloaded().toInt()
            }
            if (it.installStatus() == InstallStatus.DOWNLOADED) {
                binding.appUpdateProgressBar.hide()
                popupSnackBarForCompleteUpdate()
            }
        }
        appUpdateManager.registerListener(listener)
        // Returns an intent object that you use to check for an update.
        val appUpdateInfoTask : Task<AppUpdateInfo> = appUpdateManager.appUpdateInfo

        // Checks that the platform will allow the specified type of update.
        appUpdateInfoTask.addOnSuccessListener { appUpdateInfo ->
            if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
                && appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.FLEXIBLE)
            ) {
                try {
                    appUpdateManager.startUpdateFlowForResult(
                        appUpdateInfo,
                        this,
                        AppUpdateOptions.newBuilder(AppUpdateType.FLEXIBLE)
                            .setAllowAssetPackDeletion(true)
                            .build(),
                        APP_UPDATE_RC
                    )
                } catch (e : SendIntentException) {
                    e.printStackTrace()
                }
            }
        }
    }

    private fun popupSnackBarForCompleteUpdate() {
        val snackBar = Snackbar.make(
            binding.root,
            "An update has just been downloaded.",
            Snackbar.LENGTH_INDEFINITE
        )
        snackBar.setAction("RESTART") { appUpdateManager.completeUpdate() }
        snackBar.setActionTextColor(
            ContextCompat.getColor(this, android.R.color.holo_green_light)
        )
        snackBar.show()
    }

    override fun onResume() {
        super.onResume()
        if (::appUpdateManager.isInitialized) {
            appUpdateManager
                .appUpdateInfo
                .addOnSuccessListener { appUpdateInfo : AppUpdateInfo ->
                    // If the update is downloaded but not installed,
                    // notify the user to complete the update.
                    if (appUpdateInfo.installStatus() == InstallStatus.DOWNLOADED) {
                        popupSnackBarForCompleteUpdate()
                    }
                }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (::appUpdateManager.isInitialized && ::listener.isInitialized) {
            appUpdateManager.unregisterListener(listener)
        }
    }

    override fun onBackPressed() {
        if (navController.currentDestination!!.id == navController.graph.startDestination){
            if (snackbar.isShown){
                super.onBackPressed()
            }else{
                snackbar.show()
            }
        }else{
            super.onBackPressed()
        }
    }
}