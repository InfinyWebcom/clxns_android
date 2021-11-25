package com.clxns.app.ui.search

import android.app.Activity
import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.widget.RelativeLayout
import android.widget.SearchView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.clxns.app.R
import com.clxns.app.data.api.helper.NetworkResult
import com.clxns.app.data.model.cases.CasesData
import com.clxns.app.data.preference.SessionManager
import com.clxns.app.databinding.ActivitySearchBinding
import com.clxns.app.ui.main.cases.CasesAdapter
import com.clxns.app.ui.main.cases.CasesViewModel
import com.clxns.app.ui.main.cases.caseDetails.DetailsActivity
import com.clxns.app.utils.*
import dagger.hilt.android.AndroidEntryPoint
import java.util.*
import javax.inject.Inject

@AndroidEntryPoint
class SearchActivity : AppCompatActivity(), CasesAdapter.OnCaseItemClickListener {

    private lateinit var binding: ActivitySearchBinding
    private lateinit var searchView: SearchView

    private val casesViewModel: CasesViewModel by viewModels()
    private var casesDataList: MutableList<CasesData> = mutableListOf()

    private lateinit var casesAdapter: CasesAdapter

    @Inject
    lateinit var sessionManager: SessionManager //Property will be initialize by Preference Module using Hilt - Dependency Injection

    private lateinit var token: String

    private var searchTxt: String = ""

    private lateinit var searchRV: RecyclerView
    private lateinit var noDataLayout: RelativeLayout // This layout will be shown if any error has occurred or no data has been found

    private lateinit var planStatusIntent: Intent //This is used for notifying the cases fragment if it needs to refresh the cases list


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initViews()

        setListeners()

        subscribeObserver()
    }

    private fun subscribeObserver() {
        casesViewModel.responseCaseList.observe(this) {
            when (it) {
                is NetworkResult.Success -> {
                    searchRV.show()
                    noDataLayout.hide()
                    if (it.data?.error == false && it.data.casesDataList.isNotEmpty()) {
                        clearAndNotifyAdapter()
                        val dataList = it.data.casesDataList
                        casesDataList.addAll(dataList)
                        casesAdapter.notifyItemRangeChanged(0, dataList.size)
                    } else {
                        binding.searchNoData.noDataTxt.text = getString(R.string.no_data)
                        binding.searchNoData.retryBtn.hide()
                        noDataLayout.show()
                        val size = casesDataList.size
                        casesDataList.clear()
                        casesAdapter.notifyItemRangeRemoved(0, size)
                    }
                }
                is NetworkResult.Error -> {
                    noDataLayout.show()
                    searchRV.hide()
                    binding.searchNoData.noDataTxt.text = getString(R.string.something_went_wrong)
                    binding.searchNoData.retryBtn.show()
                    binding.root.snackBar(it.message!!)
                }
                is NetworkResult.Loading -> {
                    binding.root.snackBar("Searching...")
                }
            }
        }

        casesViewModel.responseAddToPlan.observe(this) {
            when (it) {
                is NetworkResult.Success -> {
                    binding.root.snackBar(it.data?.title!!)
                    setPlanStatus()
                    casesViewModel.getCasesList(
                        token,
                        searchTxt,
                        "",
                        "",
                        "",
                        "",
                        "",
                        ""
                    )
                }

                is NetworkResult.Error -> binding.root.snackBar(it.message!!)

                is NetworkResult.Loading -> binding.root.snackBar("Adding to my plan...")
            }
        }

        casesViewModel.responseRemovePlan.observe(this) {
            when (it) {
                is NetworkResult.Success -> {
                    binding.root.snackBar(it.data?.title!!)
                    if (!it.data.error) {
                        setPlanStatus()
                        casesViewModel.getCasesList(
                            token,
                            searchTxt,
                            "",
                            "",
                            "",
                            "",
                            "",
                            ""
                        )
                    }
                }
                is NetworkResult.Error -> {
                    binding.root.snackBar(it.message!!)
                    // show error message
                }
                is NetworkResult.Loading -> {
                    binding.root.snackBar("Removing plan...")
                    // show a progress bar
                }
            }
        }
    }

    private fun initViews() {
        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        token = sessionManager.getString(Constants.TOKEN).toString() //Token required for the authentication for the network calls

        searchView = binding.searchView
        searchRV = binding.searchRecyclerView
        noDataLayout = binding.searchNoData.root

        casesAdapter = CasesAdapter(this, casesDataList, this) //Passing the context, list data, and interface reference that is implemented by this activity
        searchRV.layoutManager = LinearLayoutManager(this)
        searchRV.adapter = casesAdapter
    }

    private fun setListeners() {
        binding.searchBackBtn.setOnClickListener { onBackPressed() }

        searchRV.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                //This helps in hiding the keyboard if it's opened when the user starts scrolling the list
                if (searchView.hasFocus() && dy > 0) {
                    searchView.clearFocus()
                    this@SearchActivity.hideKeyboard(binding.root)
                }
            }
        })
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                this@SearchActivity.hideKeyboard(binding.root)
                if (searchView.hasFocus()) {
                    searchView.clearFocus()
                }
                if (query != null) {
                    casesViewModel.getCasesList(
                        token, query, "", "",
                        "", "",
                        "",
                        ""
                    )
                }
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if (newText.isNullOrEmpty()) {
                    // Search
                    val size = casesDataList.size
                    casesDataList.clear()
                    casesAdapter.notifyItemRangeRemoved(0, size)
                } else {
                    /* searchTxt is use for refreshing the list with last query searched,
                    if the user has planned or unplanned the lead either from this activity or details activity */
                    searchTxt = newText
                }
                return false
            }

        })
    }

    /* This function is used for clearing all the data list and notifying the cases adapter to update the UI accordingly.
    * Advantage of using this new notifying functions that you can see the smooth UI transition if implemented properly.
    * If it's not implemented correctly you can get "Inconsistency Detected Crash Problem" while updating the list
    * */
    private fun clearAndNotifyAdapter() {
        val size = casesDataList.size
        casesDataList.clear()
        casesAdapter.notifyItemRangeChanged(0, size)
    }


    private fun showPlanDialog(casesData: CasesData) {
        val cal = Calendar.getInstance()
        val dateSetListener =
            DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
                cal.set(Calendar.YEAR, year)
                cal.set(Calendar.MONTH, monthOfYear)
                cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                casesViewModel.addToPlan(
                    token,
                    casesData.loanAccountNo.toString(),
                    "${year}-${monthOfYear + 1}-${dayOfMonth}"
                )
            }
        val datePicker = DatePickerDialog(
            this,
            dateSetListener,
            // set DatePickerDialog to point to today's date when it loads up
            cal.get(Calendar.YEAR),
            cal.get(Calendar.MONTH),
            cal.get(Calendar.DAY_OF_MONTH)
        )
        datePicker.datePicker.minDate = cal.timeInMillis
        datePicker.show()

    }

    private fun showConfirmUnPlanDialog(casesData: CasesData) {
        val logoutDialog = AlertDialog.Builder(this)
        val title = "UnPlan" + getString(R.string.arrow_forward) + casesData.name
        logoutDialog.setTitle(title)
        logoutDialog.setMessage("Are you sure you want to un-plan this case?")

        logoutDialog.setPositiveButton("Yes") { dialog, _ ->
            casesViewModel.removePlan(token, casesData.loanAccountNo.toString())
            dialog.dismiss()
        }.setNegativeButton("Cancel") { dialog, _ -> dialog.dismiss() }

        /** Could have directly used the create() & show() method above itself but we wanted to customize the button appearance
         * And the customization must be written after the show() function or else we'll get NPE
         */
        val d = logoutDialog.create()
        d.show()
        d.getButton(AlertDialog.BUTTON_POSITIVE).isAllCaps = false
        d.getButton(AlertDialog.BUTTON_NEGATIVE).isAllCaps = false
    }

    private fun setPlanStatus() {
        planStatusIntent = Intent()
        planStatusIntent.putExtra("hasChangedPlanStatus", true)
        setResult(Activity.RESULT_OK, planStatusIntent)
    }


    override fun onStart() {
        super.onStart()
        binding.searchView.requestFocus()
    }

    //Abstract method define in the Cases Adapter OnCaseItemClickListener
    override fun onPlanClick(isPlanned: Boolean, casesData: CasesData) {
        if (isPlanned) {
            showConfirmUnPlanDialog(casesData)
        } else {
            showPlanDialog(casesData)
        }
    }

    override fun openDetailActivity(
        loadId: String,
        name: String,
        dispositions: String,
        isPlanned: Boolean
    ) {
        val intent = Intent(this, DetailsActivity::class.java)
        intent.putExtra("loan_account_number", loadId) //Loan Id to fetch the lead's details in the detail activity
        intent.putExtra("status", dispositions) //Disposition Status such as Collected, PTP, etc.
        intent.putExtra("name", name)
        intent.putExtra("isPlanned", isPlanned) //Lead planned status
        intent.putExtra("isCaseDetail", true) //Only set true if the user has opened the detail activity from cases fragment or search activity(this), used for hiding the check-in Button
        startDetailActivityForResult.launch(intent) // Launching an intent using activity result api to get any data back to this activity
    }

    //Used "Activity Result API" which is a replacement to an old method called "OnActivityResult" for getting any data back to calling activities
    private val startDetailActivityForResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == Activity.RESULT_OK) {
                val data: Intent? = it.data
                //It's only true when the user has perform plan or unplanned action on the lead from the details activity
                val status = data?.getBooleanExtra("hasChangedPlanStatus", false)
                if (status == true) {
                    casesViewModel.getCasesList(
                        token, searchTxt, "",
                        "", "", "",
                        "",
                        ""
                    )
                }
            }
        }

}