package com.clxns.app.ui.search

import android.app.DatePickerDialog
import android.os.Bundle
import android.widget.SearchView
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.clxns.app.data.api.helper.NetworkResult
import com.clxns.app.data.model.cases.CasesData
import com.clxns.app.data.preference.SessionManager
import com.clxns.app.databinding.ActivitySearchBinding
import com.clxns.app.ui.main.cases.CasesAdapter
import com.clxns.app.ui.main.cases.CasesViewModel
import com.clxns.app.utils.*
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
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
    lateinit var sessionManager: SessionManager

    private lateinit var token: String

    private var searchTxt: String = ""


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
                    if (it.data?.error == false && it.data.casesDataList.isNotEmpty()) {
                        casesDataList.addAll(it.data.casesDataList)
                        casesAdapter.notifyItemRangeChanged(0, casesDataList.size)
                    } else {
                        binding.root.snackBar("Nothing found")
                    }
                }
                is NetworkResult.Error -> {
                    toast(it.message!!)
                }
                is NetworkResult.Loading -> {
                    Timber.i("Loading...")
                }
            }
        }

        casesViewModel.responseAddToPlan.observe(this) {
            when (it) {
                is NetworkResult.Success -> {
                    Timber.i(it.toString())
                    casesDataList.clear()
                    casesAdapter.notifyItemRangeChanged(0, casesDataList.size)
                    casesViewModel.getCasesList(
                        token,
                        searchTxt,
                        "",
                        "",
                        "",
                        ""
                    )
                }

                is NetworkResult.Error -> toast(it.message!!)

                is NetworkResult.Loading -> toast("Adding to my plan...")
            }
        }

        casesViewModel.responseRemovePlan.observe(this) {
            when (it) {
                is NetworkResult.Success -> {
                    binding.root.snackBar(it.data?.title!!)
                    if (!it.data.error) {
                        casesDataList.clear()
                        casesAdapter.notifyItemRangeChanged(0, casesDataList.size)
                        casesViewModel.getCasesList(
                            token,
                            searchTxt,
                            "",
                            "",
                            "",
                            ""
                        )
                    }
                }
                is NetworkResult.Error -> {
                    toast(it.message!!)
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

        token = sessionManager.getString(Constants.TOKEN).toString()

        searchView = binding.searchView

        casesAdapter = CasesAdapter(this, casesDataList, this)

        binding.recyclerToolbarSearch.layoutManager = LinearLayoutManager(this)
    }

    private fun setListeners() {
        binding.searchBackBtn.setOnClickListener { onBackPressed() }

        binding.recyclerToolbarSearch.adapter = casesAdapter


        binding.recyclerToolbarSearch.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
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
                toast("Searching..")
                casesDataList.clear()
                if (query != null) {
                    casesViewModel.getCasesList(
                        token, query, "", "",
                        "", ""
                    )
                }
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if (newText.isNullOrEmpty()) {
                    // Search
                    casesDataList.clear()
                    casesAdapter.notifyItemRangeChanged(0, casesDataList.size)
                } else {
                    searchTxt = newText
                }
                return false
            }

        })
    }

    override fun onStart() {
        super.onStart()
        binding.searchView.requestFocus()
    }

    override fun onPlanClick(isPlanned: Boolean, casesData: CasesData) {
        if (isPlanned) {
            showConfirmUnPlanDialog(casesData)
        } else {
            showPlanDialog(casesData)
        }
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
        logoutDialog.setTitle("UnPlan -> ${casesData.name}")
        logoutDialog.setMessage("Are you sure want to un-plan this case?")

        logoutDialog.setPositiveButton("Yes") { dialog, _ ->
            casesViewModel.removePlan(token, casesData.loanAccountNo.toString())
            dialog.dismiss()
        }.setNegativeButton("Cancel") { dialog, _ -> dialog.dismiss() }

        val d = logoutDialog.create()
        d.show()
        d.getButton(AlertDialog.BUTTON_POSITIVE).isAllCaps = false
        d.getButton(AlertDialog.BUTTON_NEGATIVE).isAllCaps = false
    }
}