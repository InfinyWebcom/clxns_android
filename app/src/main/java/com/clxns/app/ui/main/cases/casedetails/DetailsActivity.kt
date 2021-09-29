package com.clxns.app.ui.main.cases.casedetails

import android.app.Activity
import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.graphics.Paint
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import com.clxns.app.R
import com.clxns.app.data.api.helper.NetworkResult
import com.clxns.app.data.model.Lead
import com.clxns.app.data.preference.SessionManager
import com.clxns.app.databinding.ActivityDetailsBinding
import com.clxns.app.ui.main.cases.CasesViewModel
import com.clxns.app.ui.main.cases.casedetails.casestatus.checkin.CheckInActivity
import com.clxns.app.ui.main.cases.casedetails.history.HistoryDetailsActivity
import com.clxns.app.utils.*
import dagger.hilt.android.AndroidEntryPoint
import java.util.*
import javax.inject.Inject

@AndroidEntryPoint
class DetailsActivity : AppCompatActivity() {

    lateinit var binding: ActivityDetailsBinding
    lateinit var ctx: Context
    private val detailsViewModel: DetailsViewModel by viewModels()

    private val casesViewModel: CasesViewModel by viewModels()

    @Inject
    lateinit var sessionManager: SessionManager

    private lateinit var token: String
    private lateinit var loanAccountNo: String
    private lateinit var name: String
    private lateinit var status: String
    private var isPlanned = false
    private var isCaseDetail = false

    private var mobileNo: String? = null

    private lateinit var planStatusIntent: Intent

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.txtHistory.paintFlags =
            binding.txtHistory.paintFlags or Paint.UNDERLINE_TEXT_FLAG

        isPlanned = intent.getBooleanExtra("isPlanned", false)
        isCaseDetail = intent.getBooleanExtra("isCaseDetail", false)
        loanAccountNo = intent.getStringExtra("loan_account_number").toString()
        status = intent.getStringExtra("status").toString()
        name = intent.getStringExtra("name").toString()
        token = sessionManager.getString(Constants.TOKEN)!!

        binding.txtToolbarTitle.text = name
        binding.txtStatusValue.text = status
        if (isCaseDetail) {
            binding.btnCheckIn.visibility = View.GONE
        }

        setListeners()

        subscribeObserver()

        getCaseDetail()

        updatePlanButtonUI()
    }


    private fun getCaseDetail() {
        detailsViewModel.getCaseDetails(
            token,
            loanAccountNo
        )
    }

    private fun setListeners() {

        binding.btnCheckIn.setOnClickListener {
            val intent = Intent(this, CheckInActivity::class.java)
            intent.putExtra("status", intent.getStringExtra("status"))
            intent.putExtra("loan_account_number", loanAccountNo)
            intent.putExtra("name", intent.getStringExtra("name"))
            startActivity(intent)
        }

        binding.txtHistory.setOnClickListener {
            val intent = Intent(this, HistoryDetailsActivity::class.java)
            intent.putExtra("loan_account_number", loanAccountNo)
            intent.putExtra("name", name)
            startActivity(intent)
        }

        binding.detailsPlanBtn.setOnClickListener {
            if (isPlanned) {
                showConfirmUnPlanDialog(loanAccountNo, name)
            } else {
                showPlanDialog(loanAccountNo)
            }
        }

        binding.caseDetailBackBtn.setOnClickListener {
            onBackPressed()
        }

        binding.caseDetailCallBtn.setOnClickListener {
            val dialIntent = Intent(Intent.ACTION_DIAL)
            if (mobileNo != null) {
                dialIntent.data = Uri.parse("tel:${mobileNo}")
            }
            startActivity(dialIntent)
        }
        binding.showMoreTxt.setOnClickListener {
            if (binding.userDetailsContainer.isVisible) {
                binding.userDetailsContainer.visibility = View.GONE
            } else {
                binding.userDetailsContainer.visibility = View.VISIBLE
            }
        }


    }

    private fun subscribeObserver() {
        detailsViewModel.responseCaseDetail.observe(this) { response ->
            when (response) {
                is NetworkResult.Success -> {
                    binding.progressBar.hide()
                    if (response.data?.error == false && response.data.data != null) {
                        mobileNo = response.data.data.phone
                        updateUI(response.data.data)
                    } else {
                        toast("Failed to fetch details")
                        finish()
                    }
                    // bind data to the view
                }
                is NetworkResult.Error -> {
                    binding.progressBar.hide()
                    toast(response.message!!)
                    finish()
                    // show error message
                }
                is NetworkResult.Loading -> {
                    binding.progressBar.show()
                    // show a progress bar
                }
            }
        }

        casesViewModel.responseAddToPlan.observe(this) {
            when (it) {
                is NetworkResult.Success -> {
                    binding.root.snackBar(it.data?.title!!)
                    if (it.data.error == false) {
                        isPlanned = true
                        setPlanStatus()
                        updatePlanButtonUI()
                    }
                }

                is NetworkResult.Error -> toast(it.message!!)

                is NetworkResult.Loading -> binding.root.snackBar("Adding to my plan...")
            }
        }

        casesViewModel.responseRemovePlan.observe(this) {
            when (it) {
                is NetworkResult.Success -> {
                    binding.root.snackBar(it.data?.title!!)
                    if (!it.data.error) {
                        isPlanned = false
                        setPlanStatus()
                        updatePlanButtonUI()
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

    private fun setPlanStatus() {
        planStatusIntent = Intent()
        planStatusIntent.putExtra("hasChangedPlanStatus", true)
        setResult(Activity.RESULT_OK, planStatusIntent)
    }

    private fun updateUI(data: Lead) {

        val bankImageUrl = Constants.BANK_LOGO_URL + data.fiData?.fiImage
        binding.imgBank.loadImage(bankImageUrl)
        binding.txtBankName.text = data.fiData?.name?.let { nullSafeString(it) }
        binding.detailsLoanId.text = nullSafeString(data.loanAccountNo.toString())
        binding.paymentStatus.text =
            nullSafeString(data.paymentStatus.toString()).makeFirstLetterCapital()

        binding.txtLoanAmountValue.text = checkIfAmountValueIsZero(data.totalLoanAmount.toString())
        binding.totalDueAmountValue.text = checkIfAmountValueIsZero(data.totalDueAmount.toString())
        binding.emiAmountValue.text = checkIfAmountValueIsZero(data.emiAmount.toString())
        binding.principalOutstandingAmountValue.text =
            checkIfAmountValueIsZero(data.principalOutstandingAmount.toString())
        binding.txtInterestDueAmountValue.text =
            checkIfAmountValueIsZero(data.interestDueAmount.toString())
        binding.txtLateChargesValue.text = checkIfAmountValueIsZero(data.penaltyAmount.toString())

        binding.DPDValue.text = nullSafeString(data.allocationDpd.toString())

        binding.txtDisbursementDateValue.text = data.disbursementDate?.convertServerDateToNormal("dd MMM, yyyy")

        binding.txtProductValue.text = nullSafeString(data.productTypeId.toString())
        binding.txtPinCodeValue.text = nullSafeString(data.applicantPincode.toString())

        //These details get visible on click of Show More
        binding.txtEmail.text = nullSafeString(data.email.toString())
        binding.txtContactNo.text = nullSafeString(data.phone.toString())
        binding.txtPanCard.text = nullSafeString(data.applicantPanNumber.toString())
        binding.txtDob.text = nullSafeString(data.applicantDob.toString())
        binding.txtCibilScore.text = nullSafeString(data.applicantCibilScore.toString())
        binding.txtBusinessName.text = nullSafeString(data.businessName.toString())
        binding.txtCity.text = nullSafeString(data.applicantCity.toString())
        binding.txtState.text = nullSafeString(data.applicantState.toString())
        binding.txtAddressValue.text = nullSafeString(data.address.toString())
        binding.txtNewAddressValue.text = nullSafeString(data.applicantAddress.toString())
        binding.txtNewMobileValue.text = nullSafeString(data.applicantAlternateMobile1.toString())

    }

    private fun showPlanDialog(loanId: String) {
        val cal = Calendar.getInstance()
        val dateSetListener =
            DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
                cal.set(Calendar.YEAR, year)
                cal.set(Calendar.MONTH, monthOfYear)
                cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)

                casesViewModel.addToPlan(
                    token,
                    loanId,
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

    private fun showConfirmUnPlanDialog(loanId: String, name: String) {
        val logoutDialog = AlertDialog.Builder(this)
        logoutDialog.setTitle("UnPlan -> $name")
        logoutDialog.setMessage("Are you sure want to un-plan this case?")

        logoutDialog.setPositiveButton("Yes") { dialog, _ ->
            casesViewModel.removePlan(
                token,
                loanId
            )
            dialog.dismiss()
        }.setNegativeButton("Cancel") { dialog, _ -> dialog.dismiss() }

        val d = logoutDialog.create()
        d.show()
        d.getButton(AlertDialog.BUTTON_POSITIVE).isAllCaps = false
        d.getButton(AlertDialog.BUTTON_NEGATIVE).isAllCaps = false
    }

    private fun updatePlanButtonUI() {
        if (isPlanned) {
            binding.detailsPlanBtn.text = getString(R.string.un_plan)
            binding.detailsPlanBtn.rippleColor =
                ContextCompat.getColorStateList(this, R.color.light_red)
            binding.detailsPlanBtn.strokeColor =
                ContextCompat.getColorStateList(this, R.color.light_red)
            binding.detailsPlanBtn.setTextColor(
                ContextCompat.getColor(
                    this,
                    R.color.light_red
                )
            )
        } else {
            binding.detailsPlanBtn.text = getString(R.string.plan)
            binding.detailsPlanBtn.rippleColor =
                ContextCompat.getColorStateList(this, R.color.green)
            binding.detailsPlanBtn.strokeColor =
                ContextCompat.getColorStateList(this, R.color.green)
            binding.detailsPlanBtn.setTextColor(
                ContextCompat.getColor(
                    this,
                    R.color.green
                )
            )
        }
    }


    private fun checkIfAmountValueIsZero(value: String): String {
        if (value.isNotEmpty() && value == "0") {
            return "-"
        }
        return value.toInt().convertToCurrency()
    }

    private fun nullSafeString(value: String): String {
        if (value.isEmpty() || value.isBlank() || value == "null" || value == "0") {
            return "-"
        }
        return value
    }
}