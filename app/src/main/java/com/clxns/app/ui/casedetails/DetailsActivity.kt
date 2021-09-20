package com.clxns.app.ui.casedetails

import android.content.Context
import android.content.Intent
import android.graphics.Paint
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import com.clxns.app.R
import com.clxns.app.data.api.helper.NetworkResult
import com.clxns.app.data.model.Lead
import com.clxns.app.data.preference.SessionManager
import com.clxns.app.databinding.ActivityDetailsBinding
import com.clxns.app.ui.casedetails.casestatus.checkin.CheckInActivity
import com.clxns.app.ui.casedetails.history.HistoryDetailsActivity
import com.clxns.app.ui.home.plan.listview.TempAdapter2
import com.clxns.app.utils.Constants
import com.clxns.app.utils.hide
import com.clxns.app.utils.show
import com.clxns.app.utils.toast
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class DetailsActivity : AppCompatActivity() {

    lateinit var binding: ActivityDetailsBinding
    lateinit var ctx: Context
    val viewModel: DetailsViewModel by viewModels()

    @Inject
    lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.statusBarColor = ContextCompat.getColor(this, R.color.colorPrimary)
        binding = ActivityDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        viewModel.loanAccountNumber=intent.getStringExtra("loan_account_number")
        setListeners()
        setObserver()
        if (viewModel.loanAccountNumber != null) {
            viewModel.getCaseDetails(
                sessionManager.getString(Constants.TOKEN)!!,
                viewModel.loanAccountNumber!!
            )
        } else {
            toast("Error while fetching plan details")
            finish()
        }

    }

    private fun setListeners() {

        /*detailsBinding.txtUpdate.setOnClickListener {
            val popUpView =
                LayoutInflater.from(this).inflate(R.layout.update_status_pop_up_layout, null)
            val updateStatusPopUp = AlertDialog.Builder(this)
            updateStatusPopUp.setCancelable(true)


            val statusSpinner = popUpView.findViewById<Spinner>(R.id.popup_update_status_spinner)
            val cancelBtn = popUpView.findViewById<MaterialButton>(R.id.cancel_status_btn)
            val updateBtn = popUpView.findViewById<MaterialButton>(R.id.update_status_btn)
            val statusAdapter = ArrayAdapter.createFromResource(
                ctx,
                R.array.status_list,
                android.R.layout.simple_spinner_item
            )
            statusAdapter.setDropDownViewResource(android.R.layout.select_dialog_singlechoice)
            statusSpinner.adapter = statusAdapter
            updateStatusPopUp.setView(popUpView)

            val alertDialog = updateStatusPopUp.create()
            alertDialog.show()
            cancelBtn.setOnClickListener {
                alertDialog.dismiss()
            }
            updateBtn.setOnClickListener {

                detailsBinding.txtStatusValue.text = statusSpinner.selectedItem.toString()
                Toast.makeText(this@DetailsActivity, "Status updated", Toast.LENGTH_LONG).show()
                alertDialog.dismiss()
            }
        }*/

        binding.btnCheckIn.setOnClickListener {
            val intent = Intent(this, CheckInActivity::class.java)
            intent.putExtra("status", intent.getStringExtra("status"))
            intent.putExtra("name", intent.getStringExtra("name"))
            startActivity(intent)
        }

        binding.txtHistory.setOnClickListener {
            val intent = Intent(this, HistoryDetailsActivity::class.java)
            intent.putExtra("loan_account_number",viewModel.loanAccountNumber)
            startActivity(intent)
        }

        binding.imgBack.setOnClickListener {
            finish()
        }

        binding.imgCall.setOnClickListener {
            val dialIntent = Intent(Intent.ACTION_DIAL)
            val number = intent.getStringExtra("mobile_number")
            dialIntent.data = Uri.parse("tel:${number ?: "0123456789"}")
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

    private fun setObserver() {
        viewModel.response.observe(this) { response ->
            when (response) {
                is NetworkResult.Success -> {
                    binding.progressBar.hide()
                    if (!response.data?.error!!) {
                        setData(response.data.data!!)

                    } else {
                        toast(response.data.title!!)
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
    }

    private fun setData(data: Lead) {

        binding.txtHistory.paintFlags =
            binding.txtHistory.paintFlags or Paint.UNDERLINE_TEXT_FLAG

        binding.txtAppName.text = data.name
        binding.txtBankName.text=data.band
        binding.detailsLoanId.text=data.loanAccountNo.toString()
        binding.txtLoanAmountValue.text = data.totalLoanAmount.toString()
        binding.txtStatusValue.text = data.paymentStatus
        binding.txtPendingValue.text=data.totalDueAmount.toString()
        binding.txtDatePassedDueValue.text=data.emiAmount.toString()
        binding.txtSettlementAmount.text=data.principalOutstandingAmount.toString()
        binding.txtInterestDueAmountValue.text=data.interestDueAmount.toString()
        binding.txtLateChargesValue.text=data.penaltyAmount.toString()
        binding.txtDisbursementDateValue.text=data.daysDue.toString()
        binding.txtTotalAmountValue.text=data.allocationDpd.toString()
        binding.txtProductValue.text=data.productTypeId.toString()
        binding.txtPinCodeValue.text=data.applicantPincode.toString()
        binding.txtEmail.text=data.email.toString()
        binding.txtContactNo.text=data.applicantAlternateMobile1.toString()
        binding.txtPanCard.text=data.applicantPanNumber.toString()
        binding.txtDob.text=data.applicantDob.toString()
        binding.txtCibilScore.text=data.applicantCibilScore.toString()
        binding.txtBusinessName.text=data.businessName.toString()
        binding.txtCity.text=data.applicantCity.toString()
        binding.txtState.text=data.applicantState.toString()
        binding.txtNewAddress.text=data.applicantAltAddress.toString()
        binding.txtNewMobileValue.text=data.applicantAlternateMobile1
//        if (!intent.getStringExtra("address").isNullOrEmpty()) {
//            binding.txtAddressValue.text = intent.getStringExtra("address").toString()
//        }
//        if (!intent.getStringExtra("loan_account_number").isNullOrEmpty()) {
//            viewModel.loanAccountNumber = intent.getStringExtra("loan_account_number").toString()
//        }
//        if (!intent.getStringExtra("mobile_number").isNullOrEmpty()) {
//            binding.txtNewMobileValue.text = intent.getStringExtra("mobile_number").toString()
//        }
//        if (!intent.getStringExtra("bank_name").isNullOrEmpty()) {
//            binding.txtBankName.text = intent.getStringExtra("bank_name").toString()
//        }
//        if (!intent.getStringExtra("loan_id").isNullOrEmpty()) {
//            binding.detailsLoanId.text = intent.getStringExtra("loan_id")
//        }
//        binding.txtLoanAmountValue.text = intent.getStringExtra("amount").toString()
//        binding.txtStatusValue.text = intent.getStringExtra("status")

        val isPlanned = intent.getBooleanExtra("isPlanned", false)
        if (isPlanned) {
            binding.detailsPlanBtn.text = "Unplan"
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
        }
    }
}