package com.clxns.app.ui.casedetails

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
import androidx.lifecycle.ViewModelProvider
import com.clxns.app.R
import com.clxns.app.data.repository.DetailsRepository
import com.clxns.app.databinding.ActivityDetailsBinding
import com.clxns.app.ui.casedetails.casestatus.checkin.CheckInActivity
import com.clxns.app.ui.casedetails.history.HistoryDetailsActivity

class DetailsActivity : AppCompatActivity() {

    lateinit var detailsBinding: ActivityDetailsBinding
    lateinit var ctx: Context
     val detailsViewModel: DetailsViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.statusBarColor = ContextCompat.getColor(this, R.color.colorPrimary)
        setInit()
        setListeners()

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

        detailsBinding.btnCheckIn.setOnClickListener {
            val checkInIntent = Intent(this, CheckInActivity::class.java)
            checkInIntent.putExtra("status", intent.getStringExtra("status"))
            checkInIntent.putExtra("name", intent.getStringExtra("name"))
            startActivity(checkInIntent)
        }

        detailsBinding.txtHistory.setOnClickListener {
            val checkInIntent = Intent(this, HistoryDetailsActivity::class.java)
            startActivity(checkInIntent)
        }

        detailsBinding.imgBack.setOnClickListener {
            finish()
        }

        detailsBinding.imgCall.setOnClickListener {
            val dialIntent = Intent(Intent.ACTION_DIAL)
            val number = intent.getStringExtra("mobile_number")
            dialIntent.data = Uri.parse("tel:${number ?: "0123456789"}")
            startActivity(dialIntent)
        }
        detailsBinding.showMoreTxt.setOnClickListener {
            if (detailsBinding.userDetailsContainer.isVisible) {
                detailsBinding.userDetailsContainer.visibility = View.GONE
            } else {
                detailsBinding.userDetailsContainer.visibility = View.VISIBLE
            }
        }


    }

    private fun setInit() {
        ctx = this
        detailsBinding = ActivityDetailsBinding.inflate(layoutInflater)
        setContentView(detailsBinding.root)

        detailsBinding.txtHistory.paintFlags =
            detailsBinding.txtHistory.paintFlags or Paint.UNDERLINE_TEXT_FLAG
        detailsBinding.txtAppName.text = intent.getStringExtra("name")
        if (!intent.getStringExtra("address").isNullOrEmpty()) {
            detailsBinding.txtAddressValue.text = intent.getStringExtra("address")
        }
        if (!intent.getStringExtra("mobile_number").isNullOrEmpty()) {
            detailsBinding.txtNewMobileValue.text = intent.getStringExtra("mobile_number")
        }
        if (!intent.getStringExtra("bank_name").isNullOrEmpty()) {
            detailsBinding.txtBankName.text = intent.getStringExtra("bank_name")
        }
        if (!intent.getStringExtra("loan_id").isNullOrEmpty()) {
            detailsBinding.detailsLoanId.text = intent.getStringExtra("loan_id")
        }
        detailsBinding.txtLoanAmountValue.text = intent.getStringExtra("amount")
        detailsBinding.txtStatusValue.text = intent.getStringExtra("status")
        val isPlanned = intent.getBooleanExtra("isPlanned", false)
        if (isPlanned) {
            detailsBinding.detailsPlanBtn.text = "Unplan"
            detailsBinding.detailsPlanBtn.rippleColor =
                ContextCompat.getColorStateList(this, R.color.light_red)
            detailsBinding.detailsPlanBtn.strokeColor =
                ContextCompat.getColorStateList(this, R.color.light_red)
            detailsBinding.detailsPlanBtn.setTextColor(
                ContextCompat.getColor(
                    this,
                    R.color.light_red
                )
            )
        }
    }
}