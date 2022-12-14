package com.clxns.app.ui.main.cases.caseDetails

import android.app.Activity
import android.app.DatePickerDialog
import android.content.Intent
import android.graphics.Paint
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.annotation.ColorRes
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import com.clxns.app.R
import com.clxns.app.data.api.helper.NetworkResult
import com.clxns.app.data.model.CaseDetailsResponse
import com.clxns.app.data.model.Lead
import com.clxns.app.data.preference.SessionManager
import com.clxns.app.databinding.ActivityDetailsBinding
import com.clxns.app.ui.main.cases.CasesViewModel
import com.clxns.app.ui.main.cases.caseDetails.caseStatus.checkIn.CheckInActivity
import com.clxns.app.ui.main.cases.caseDetails.history.HistoryDetailsActivity
import com.clxns.app.utils.*
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

@AndroidEntryPoint
class DetailsActivity : AppCompatActivity() {

    lateinit var binding : ActivityDetailsBinding

    private val detailsViewModel : DetailsViewModel by viewModels()

    private val casesViewModel : CasesViewModel by viewModels()

    @Inject
    lateinit var sessionManager : SessionManager

    private lateinit var token : String
    private lateinit var loanAccountNo : String
    private lateinit var name : String
    private var status = ""
    private var isPlanned = false
    private var hasComeFromCasesScreen = false

    private var mobileNo : String? = null

    private lateinit var planStatusIntent : Intent

    private var totalDueAmount = 0
    private var collectedAmount = 0
    private var checkInLauncher : ActivityResultLauncher<Intent>? = null

    private lateinit var fosAssignedDate : String

    private lateinit var caseDetailsResponse : CaseDetailsResponse

    override fun onCreate(savedInstanceState : Bundle?) {
        super.onCreate(savedInstanceState)

        initView()

        initCheckInLauncher()

        setListeners()

        subscribeObserver()

        getCaseDetail()
    }


    private fun initView() {
        binding = ActivityDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.txtHistory.paintFlags =
            binding.txtHistory.paintFlags or Paint.UNDERLINE_TEXT_FLAG

        isPlanned = intent.getBooleanExtra("isPlanned", false)
        hasComeFromCasesScreen = intent.getBooleanExtra("isCaseDetail", false)
        loanAccountNo = intent.getStringExtra("loan_account_number").toString()
        status = intent.getStringExtra("status").toString()
        name = intent.getStringExtra("name").toString()
        token = sessionManager.getString(Constants.TOKEN)!!

        binding.txtToolbarTitle.text = name
        binding.txtStatusValue.text = status

        //If the user has come from Cases or Search Cases Screen then hide the Check In Button
        if (hasComeFromCasesScreen) {
            binding.btnCheckIn.hide()
        }

        //Changing UI of the plan btn according to the lead plan status
        if (isPlanned) {
            updatePlanButtonUI(R.color.light_red)
        } else {
            updatePlanButtonUI(R.color.green)
        }
    }


    private fun getCaseDetail() {
        detailsViewModel.getCaseDetails(
            token,
            loanAccountNo
        )
    }

    private fun setListeners() {

        binding.txtTotalDueAmount.setOnClickListener {
            it.showDialog(totalDueAmount, collectedAmount)
        }

        binding.btnCheckIn.setOnClickListener {
            val intent = Intent(this, CheckInActivity::class.java)
//            intent.putExtra("status", intent.getStringExtra("status"))
//            intent.putExtra("loan_account_number", loanAccountNo)
//            intent.putExtra("name", intent.getStringExtra("name"))
//            intent.putExtra("fosAssignedDate", fosAssignedDate)
            intent.putExtra("caseDetail", caseDetailsResponse)
            checkInLauncher!!.launch(intent)
        }

        binding.txtHistory.setOnClickListener {
            val intent = Intent(this, HistoryDetailsActivity::class.java)
            intent.putExtra("loan_account_number", loanAccountNo)
            intent.putExtra("total_due_amount", totalDueAmount)
            intent.putExtra("collected_amount", collectedAmount)
            intent.putExtra("name", name)
            startActivity(intent)
        }

        binding.detailsPlanBtn.setOnClickListener {
            it.preventDoubleClick(800)
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
            it.preventDoubleClick()
            val dialIntent = Intent(Intent.ACTION_DIAL)
            if (mobileNo != null) {
                dialIntent.data = Uri.parse("tel:${mobileNo}")
            }
            startActivity(dialIntent)
        }
        binding.showMoreTxt.setOnClickListener {
            if (binding.userDetailsContainer.isVisible) {
                binding.showMoreTxt.text = getString(R.string.show_more)
                binding.userDetailsContainer.hide()
                binding.showMoreTxt.setCompoundDrawablesRelativeWithIntrinsicBounds(
                    null, null,
                    ContextCompat.getDrawable(this, R.drawable.ic_round_arrow_drop_down), null
                )
            } else {
                binding.showMoreTxt.text = getString(R.string.show_less)
                binding.userDetailsContainer.show()
                binding.showMoreTxt.setCompoundDrawablesRelativeWithIntrinsicBounds(
                    null, null,
                    ContextCompat.getDrawable(this, R.drawable.ic_round_arrow_drop_up_24), null
                )
                lifecycleScope.launch {
                    delay(100L)
                    binding.detailsScrollView.fullScroll(View.FOCUS_DOWN)
                }
            }
        }

        binding.additionalDetailTV.setOnClickListener {
            if (binding.additionalInfoRV.isVisible) {
                binding.additionalInfoRV.hide()
                binding.additionalDetailTV.setCompoundDrawablesRelativeWithIntrinsicBounds(
                    null, null,
                    ContextCompat.getDrawable(this, R.drawable.ic_round_arrow_drop_down), null
                )
            } else {
                binding.additionalDetailTV.setCompoundDrawablesRelativeWithIntrinsicBounds(
                    null, null,
                    ContextCompat.getDrawable(this, R.drawable.ic_round_arrow_drop_up_24), null
                )
                binding.additionalInfoRV.show()
                lifecycleScope.launch {
                    delay(100L)
                    binding.detailsScrollView.fullScroll(View.FOCUS_DOWN)
                }
            }
        }

    }

    private fun subscribeObserver() {
        detailsViewModel.responseCaseDetail.observe(this) { response ->
            when (response) {
                is NetworkResult.Success -> {
                    binding.progressBar.hide()
                    binding.detailsPlanBtn.show()
                    if (response.data?.error == false && response.data.data != null) {
                        binding.btnCheckIn.show()

                        caseDetailsResponse = response.data

                        mobileNo = response.data.data.phone
                        totalDueAmount = response.data.data.totalDueAmount!!
                        collectedAmount = response.data.data.amountCollected!!

                        fosAssignedDate = response.data.data.fosAssignedDate.toString()

                        if (((response.data.data.totalDueAmount.minus(response.data.data.amountCollected)) <= 0) || hasComeFromCasesScreen) {
                            binding.btnCheckIn.hide()
                        } else {
                            binding.btnCheckIn.show()
                        }

                        //Fetching Dispositions from Local DB
                        if (response.data.data.dispositionId != null) {
                            detailsViewModel.getDispositionName(response.data.data.dispositionId)
                            if (response.data.data.subDispositionId != null) {
                                lifecycleScope.launch {
                                    delay(100)
                                    //3C3F41FF
                                    detailsViewModel.getSubDispositionName(response.data.data.subDispositionId)
                                }
                            }
                        }
                        //Addition Details if available only then show it
                        if (response.data.contactDataList.isNotEmpty()) {
                            binding.additionalInfoRV.apply {
                                adapter = UpdatedContactAdapter(response.data.contactDataList)
                            }
                            binding.additionalLL.show()
                        } else {
                            binding.additionalLL.hide()
                        }
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
                    binding.btnCheckIn.hide()
                    binding.detailsPlanBtn.hide()
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
                        if (!hasComeFromCasesScreen) {
                            binding.btnCheckIn.show()
                        }
                        setPlanStatus()
                        updatePlanButtonUI(R.color.light_red)
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
                        binding.btnCheckIn.hide()
                        setPlanStatus()
                        updatePlanButtonUI(R.color.green)
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

        detailsViewModel.responseDispositionName.observe(this) {
            if (!it.isNullOrEmpty()) {
                status = it
                binding.txtStatusValue.text = status
            }
        }

        detailsViewModel.responseSubDispositionName.observe(this) {
            if (!it.isNullOrEmpty()) {
                status += getString(R.string.arrow_forward) + it
                binding.txtStatusValue.text = status
            }
        }


    }

    private fun initCheckInLauncher() {
        checkInLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == RESULT_OK) {
//                val data : Intent? = result.data
//                if (data != null) {
//                    val returnValue : Boolean = data.getBooleanExtra("close_app", false)
//                    if (returnValue) {
//                        setPlanStatus()
//                        val uiHandler = Handler(Looper.getMainLooper())
//                        uiHandler.postDelayed({ super.onBackPressed() }, 50)
//                    }
//                }
                //Refresh detail screen if user has checked in
                getCaseDetail()
                //To refresh my plan fragment
                setPlanStatus()
            }
        }
    }

    private fun setPlanStatus() {
        planStatusIntent = Intent()
        planStatusIntent.putExtra("hasChangedPlanStatus", true)
        setResult(Activity.RESULT_OK, planStatusIntent)
    }

    private fun updateUI(data : Lead) {

        val bankImageUrl = Constants.BANK_LOGO_URL + data.fiData?.fiImage
        binding.imgBank.loadImage(bankImageUrl)
        binding.txtBankName.text = data.fiData?.name?.let { nullSafeString(it) }
        binding.detailsLoanId.text = nullSafeString(data.loanAccountNo.toString())
        binding.paymentStatus.text =
            nullSafeString(data.paymentStatus.toString()).makeFirstLetterCapital()

        binding.txtLoanAmountValue.text = checkIfAmountValueIsZero(data.totalLoanAmount.toString())

        val updatedDueAmount = totalDueAmount - collectedAmount
        binding.totalDueAmountValue.text = checkIfAmountValueIsZero(updatedDueAmount.toString())
        if (updatedDueAmount == 0) {
            updatePlanButtonUI(R.color.quantum_grey400)
            binding.detailsPlanBtn.isEnabled = false
        }

        binding.emiAmountValue.text = checkIfAmountValueIsZero(data.emiAmount.toString())
        binding.principalOutstandingAmountValue.text =
            checkIfAmountValueIsZero(data.principalOutstandingAmount.toString())
        binding.txtInterestDueAmountValue.text =
            checkIfAmountValueIsZero(data.interestDueAmount.toString())
        binding.txtLateChargesValue.text = checkIfAmountValueIsZero(data.penaltyAmount.toString())

        binding.DPDValue.text = nullSafeString(data.allocationDpd.toString())

        binding.txtDueDateValue.text =
            data.dateOfDefault?.convertServerDateToNormal("dd, MMM yyyy")

        binding.txtLoanTypeValue.text = nullSafeString(data.loanType.toString())
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

    private fun showPlanDialog(loanId : String) {
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

    private fun showConfirmUnPlanDialog(loanId : String, name : String) {
        val logoutDialog = AlertDialog.Builder(this)
        val title = "UnPlan" + getString(R.string.arrow_forward) + name
        logoutDialog.setTitle(title)
        logoutDialog.setMessage("Are you sure you want to un-plan this case?")

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

    private fun updatePlanButtonUI(@ColorRes color : Int) {
        binding.detailsPlanBtn.rippleColor =
            ContextCompat.getColorStateList(this, color)
        binding.detailsPlanBtn.strokeColor =
            ContextCompat.getColorStateList(this, color)
        binding.detailsPlanBtn.setTextColor(
            ContextCompat.getColor(
                this,
                color
            )
        )
        if (isPlanned) {
            binding.detailsPlanBtn.text = getString(R.string.un_plan)
        } else {
            binding.detailsPlanBtn.text = getString(R.string.plan)
        }
    }


    private fun checkIfAmountValueIsZero(value : String) : String {
        if (value.isNotEmpty() && value == "0") {
            return "-"
        }
        return value.toInt().convertToCurrency()
    }

    private fun nullSafeString(value : String) : String {
        if (value.isEmpty() || value.isBlank() || value == "null" || value == "0") {
            return "-"
        }
        return value
    }
}