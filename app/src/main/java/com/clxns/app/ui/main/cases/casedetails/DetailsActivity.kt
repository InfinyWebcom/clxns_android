package com.clxns.app.ui.main.cases.casedetails

import android.content.Context
import android.content.Intent
import android.graphics.Paint
import android.net.Uri
import android.os.Bundle
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
import com.clxns.app.ui.main.cases.casedetails.casestatus.checkin.CheckInActivity
import com.clxns.app.ui.main.cases.casedetails.history.HistoryDetailsActivity
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
        viewModel.loanAccountNumber = intent.getStringExtra("loan_account_number")
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
        } else {
            binding.btnCheckIn.visibility = View.INVISIBLE
        }

        binding.btnCheckIn.setOnClickListener {
            val intent = Intent(this, CheckInActivity::class.java)
            intent.putExtra("status", intent.getStringExtra("status"))
            intent.putExtra("name", intent.getStringExtra("name"))
            startActivity(intent)
        }

        binding.txtHistory.setOnClickListener {
            val intent = Intent(this, HistoryDetailsActivity::class.java)
            intent.putExtra("loan_account_number", viewModel.loanAccountNumber)
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

        binding.txtAppName.text = nullSafeString(data.name.toString())
        binding.txtBankName.text = nullSafeString(data.chequeBank.toString())
        binding.detailsLoanId.text = nullSafeString(data.loanAccountNo.toString())
        binding.txtLoanAmountValue.text = nullSafeString(data.totalLoanAmount.toString())
        binding.txtStatusValue.text = nullSafeString(data.paymentStatus.toString())
        binding.txtPendingValue.text = nullSafeString(data.totalDueAmount.toString())
        binding.txtDatePassedDueValue.text = nullSafeString(data.emiAmount.toString())
        binding.txtSettlementAmount.text =
            nullSafeString(data.principalOutstandingAmount.toString())
        binding.txtInterestDueAmountValue.text = nullSafeString(data.interestDueAmount.toString())
        binding.txtLateChargesValue.text = nullSafeString(data.penaltyAmount.toString())
        binding.txtDisbursementDateValue.text = nullSafeString(data.daysDue.toString())
        binding.txtTotalAmountValue.text = nullSafeString(data.allocationDpd.toString())
        binding.txtProductValue.text = nullSafeString(data.productTypeId.toString())
        binding.txtPinCodeValue.text = nullSafeString(data.applicantPincode.toString())
        binding.txtEmail.text = nullSafeString(data.email.toString())
        binding.txtContactNo.text = nullSafeString(data.applicantAlternateMobile1.toString())
        binding.txtPanCard.text = nullSafeString(data.applicantPanNumber.toString())
        binding.txtDob.text = nullSafeString(data.applicantDob.toString())
        binding.txtCibilScore.text = nullSafeString(data.applicantCibilScore.toString())
        binding.txtBusinessName.text = nullSafeString(data.businessName.toString())
        binding.txtCity.text = nullSafeString(data.applicantCity.toString())
        binding.txtState.text = nullSafeString(data.applicantState.toString())
        binding.txtNewAddress.text = nullSafeString(data.applicantAltAddress.toString())
        binding.txtNewMobileValue.text = nullSafeString(data.applicantAlternateMobile1.toString())

    }

    private fun nullSafeString(value: String): String {
        if (value.isNullOrEmpty() || value.isNullOrBlank() || value == "null") {
            return "-"
        }
        return value
    }
}