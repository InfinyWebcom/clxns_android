package com.clxns.app.ui.main.cases.caseDetails.caseStatus.paymentCollection

import android.Manifest
import android.app.DatePickerDialog
import android.app.ProgressDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.LinearLayout
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import com.clxns.app.R
import com.clxns.app.data.api.helper.NetworkResult
import com.clxns.app.data.model.AdditionalFieldModel
import com.clxns.app.data.model.CaseDetailsResponse
import com.clxns.app.data.model.PaymentModel
import com.clxns.app.data.model.cases.CaseCheckInBody
import com.clxns.app.data.model.home.DemoCap
import com.clxns.app.data.preference.SessionManager
import com.clxns.app.databinding.ActivityPaymentCollectionBinding
import com.clxns.app.ui.main.MainActivity
import com.clxns.app.ui.main.cases.caseDetails.caseStatus.AddImageAdapter
import com.clxns.app.utils.*
import com.clxns.app.utils.support.CropImageActivity
import com.google.gson.Gson
import com.nabinbhandari.android.permissions.PermissionHandler
import com.nabinbhandari.android.permissions.Permissions
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import java.io.File
import java.io.IOException
import java.util.*
import javax.inject.Inject
import kotlin.collections.ArrayList

@AndroidEntryPoint
class PaymentCollectionActivity : AppCompatActivity(), AddImageAdapter.RemovePhotoListener {

    lateinit var binding : ActivityPaymentCollectionBinding
    val viewModel : PaymentCollectionViewModel by viewModels()

    @Inject
    lateinit var sessionManager : SessionManager

    private var imageUri : Uri? = null
    private var addedPhotosList : ArrayList<Uri> = ArrayList()
    private var photoB64List : ArrayList<String> = ArrayList()
    private var cropImageLauncher : ActivityResultLauncher<Intent>? = null
    private lateinit var addImageAdapter : AddImageAdapter
    private var recoveryDate = ""
    private var paymentType = "ONLINE"
    private lateinit var caseDetails : CaseDetailsResponse

    private var year = 0
    private var month = 0
    private var day = 0
    private var imageCameraPickerLauncher : ActivityResultLauncher<Intent>? = null
    var imageGalleryPickerLauncher : ActivityResultLauncher<Intent>? = null

    private var isPartialCollect = false

    companion object {
        private val MONTHS = arrayOf(
            "Jan",
            "Feb",
            "Mar",
            "Apr",
            "May",
            "Jun",
            "Jul",
            "Aug",
            "Sept",
            "Oct",
            "Nov",
            "Dec"
        )
        private var IS_PAYMENT_DONE : Boolean = false
    }

    private lateinit var datePickerDialog : DatePickerDialog

    private lateinit var progressDialog : ProgressDialog


    override fun onCreate(savedInstanceState : Bundle?) {
        super.onCreate(savedInstanceState)
        setInit()
        setListeners()
        getResultFromActivity()
        setObserver()

//        if (viewModel.loanAccountNumber != null) {
//            viewModel.getCaseDetails(
//                sessionManager.getString(Constants.TOKEN)!!,
//                viewModel.loanAccountNumber!!
//            )
//        } else {
//            toast("Error while fetching plan details")
//            finish()
//        }
    }

    private fun setListeners() {

        binding.rgPayment.setOnCheckedChangeListener { _, id ->
            when (id) {
                R.id.rbCheque -> setChequeVisibility()
                R.id.rbOnline -> setOnlineVisibility()
                R.id.rbCash -> setCashVisibility()
            }
        }

        binding.txtPaymentOrRecoveryDateValue.setOnClickListener {
            showDatePickerDialog()
        }

        binding.imgBack.setOnClickListener { onBackPressed() }

        binding.txtUploadScreenShot.setOnClickListener {
            openFileUploadDialog()
        }

        binding.generateReceiptBtn.setOnClickListener {
            if (binding.generateReceiptBtn.text.equals("back to my plan")) {
                onBackPressed()
            } else {
                if (validate()) {
                    //additional fields
                    val additionalField = AdditionalFieldModel()
                    additionalField.recoveryDate = recoveryDate
                    additionalField.paymentMode = paymentType
                    additionalField.recoveryType = getSelectedRecoveryType()
                    additionalField.refChequeNo =
                        if (paymentType == "CHEQUE") binding.edtChequeNumber.text.toString() else (if (paymentType == "ONLINE") binding.edtReferenceNo.text.toString() else "")
                    additionalField.recoveredAmount =
                        binding.paymentAmountEt.text.toString()

                    //payment details
                    val payment = PaymentModel()
                    payment.leadId = viewModel.loanAccountNumber
                    payment.loanNo = viewModel.loanAccountNumber
                    payment.amtType = getSelectedRecoveryType()
                    payment.paymentMode = paymentType
                    payment.recoveryDate = recoveryDate
                    payment.refNo =
                        if (paymentType == "ONLINE") binding.edtReferenceNo.text.toString() else ""
                    payment.chequeNo =
                        if (paymentType == "CHEQUE") binding.edtChequeNumber.text.toString() else ""
                    payment.remark = binding.remarksET.text.toString()
                    payment.supporting = photoB64List
                    payment.collectedAmt = binding.paymentAmountEt.text.toString().toLong()

                    //body
                    val body = CaseCheckInBody()
                    body.loanAccountNo = viewModel.loanAccountNumber!!
                    body.dispositionId = viewModel.dispositionId!!
                    body.subDispositionId = null
                    body.comments = binding.remarksET.text.toString()
                    //body.followUp = recoveryDate
                    //body.nextAction = recoveryDate
                    body.additionalField = additionalField
                    body.location = viewModel.location!!
                    body.supporting = viewModel.mainSupporting
                    body.payment = payment

                    Timber.i(body.toString())
                    viewModel.saveCheckInData(
                        sessionManager.getString(Constants.TOKEN)!!,
                        body
                    )
                }
            }

        }

        binding.collectableAmountEt.setOnClickListener {
            it.showDialog(caseDetails.data?.totalDueAmount!!, caseDetails.data?.amountCollected!!)
        }

    }

    private fun getSelectedRecoveryType() : String {
        return if (binding.spAmount.selectedItemPosition != 0) {
            binding.spAmount.selectedItem.toString()
        } else {
            " "
        }
    }


    private fun setObserver() {
        /*viewModel.responseCaseDetails.observe(this) { response ->
            when (response) {
                is NetworkResult.Success -> {
                    binding.paymentProgressBar.hide()
                    if (!response.data?.error!!) {
                        caseDetails = response.data
                        binding.txtName.text = nullSafeString(response.data.data?.name)
                        binding.txtStatus.text = nullSafeString(response.data.data?.paymentStatus)
                        binding.txtProductValue.text = nullSafeString(response.data.data?.loanType)
                        if (response.data.data?.allocationDate != null) {
                            binding.txtDate.text =
                                response.data.data.allocationDate.convertServerDateToNormal("dd, MMM yyyy")

                        }
                        binding.txtBankName.text = nullSafeString(response.data.data?.fiData?.name)
                        val amount =
                            (response.data.data?.totalDueAmount?.minus(response.data.data.amountCollected!!))
                        if (amount != null) {
                            binding.collectableAmountEt.text = amount.convertToCurrency()
                        }
                    } else {
                        toast(response.data.title!!)
                        finish()
                    }
                    // bind data to the view
                }
                is NetworkResult.Error -> {
                    toast(response.message!!)
                    // show error message
                    binding.paymentProgressBar.hide()
                }
                is NetworkResult.Loading -> {
                    // show a progress bar
                    binding.paymentProgressBar.show()
                }
            }
        }*/

        viewModel.responseSaveCheckIn.observe(this) { response ->
            when (response) {
                is NetworkResult.Success -> {
                    binding.paymentProgressBar.hide()
                    progressDialog.dismiss()
                    if (!response.data?.error!!) {
                        toast("Payment has been done successfully")
                        binding.paymentSuccessLayout.show()
                        binding.paymentAnimationView.playAnimation()
                        val l : LinearLayout = findViewById(R.id.mainLinearLayout)
                        l.forEachChildView {
                            it.isEnabled = false
                        }
                        IS_PAYMENT_DONE = true
                        binding.generateReceiptBtn.text = getString(R.string.back_to_my_plan)
                    } else {
                        toast(response.data.title!!)
                    }
                }
                is NetworkResult.Error -> {
                    binding.paymentProgressBar.hide()
                    toast(response.message!!)
                    progressDialog.dismiss()
                    // show error message
                }
                is NetworkResult.Loading -> {
                    binding.paymentProgressBar.show()
                    progressDialog.show()
                    // show a progress bar
                }
            }
        }
    }

    private fun View.forEachChildView(closure : (View) -> Unit) {
        closure(this)
        val groupView = this as? ViewGroup ?: return
        val size = groupView.childCount - 1
        for (i in 0..size) {
            groupView.getChildAt(i).forEachChildView(closure)
        }
    }

    private fun showDatePickerDialog() {
        datePickerDialog =
            DatePickerDialog(this, { _, year, month, day ->
                Timber.i("month--->" + MONTHS[month])
                val paymentOrRecoveryDate = "$day, " + MONTHS[month] + " $year"
                binding.txtPaymentOrRecoveryDateValue.text = paymentOrRecoveryDate

                recoveryDate =
                    "${year}-${String.format("%02d", month + 1)}-${
                        String.format(
                            "%02d",
                            day
                        )
                    }"

            }, year, month, day)

        val minDate = caseDetails.data?.fosAssignedDate?.getDateInLongFormat()
        if (minDate != null) {
            datePickerDialog.datePicker.minDate = minDate
        } else {
            datePickerDialog.datePicker.minDate = System.currentTimeMillis()
        }
        datePickerDialog.datePicker.maxDate = System.currentTimeMillis()
        datePickerDialog.show()
    }

    private fun setCashVisibility() {
        paymentType = "CASH"
        binding.txtReferenceNo.hide()
        binding.edtReferenceNo.hide()
        binding.edtChequeNumber.hide()
    }

    private fun setOnlineVisibility() {
        paymentType = "ONLINE"
        binding.txtReferenceNo.show()
        binding.edtReferenceNo.show()
        binding.edtChequeNumber.hide()
        binding.txtReferenceNo.text = getString(R.string.reference_no)
        binding.edtReferenceNo.setText("")
        binding.edtChequeNumber.setText("")
    }

    private fun setChequeVisibility() {
        paymentType = "CHEQUE"
        binding.txtReferenceNo.text = getString(R.string.cheque_number)
        binding.edtReferenceNo.hide()
        binding.txtReferenceNo.show()
        binding.edtChequeNumber.show()
        binding.edtReferenceNo.setText("")
        binding.edtChequeNumber.setText("")

    }

    private fun setInit() {
        binding = ActivityPaymentCollectionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        progressDialog = getProgressDialog(
            this,
            "Processing payment...",
            "Please wait while we're processing your payment"
        )

        addImageAdapter = AddImageAdapter(this, this)
        binding.recyclerAddImage.adapter = addImageAdapter
        binding.recyclerAddImage.isNestedScrollingEnabled = false

        val calendar = Calendar.getInstance()
        year = calendar.get(Calendar.YEAR)
        day = calendar.get(Calendar.DAY_OF_MONTH)
        month = calendar.get(Calendar.MONTH)

        caseDetails = intent.getSerializableExtra("caseDetail") as CaseDetailsResponse

        viewModel.loanAccountNumber = caseDetails.data?.loanAccountNo.toString()
        viewModel.dispositionId = intent.getStringExtra("disposition_id")
        viewModel.location = intent.getStringExtra("location")
        if (sessionManager.getString("main_supporting") != null) {
            //val myType = object : TypeToken<ArrayList<String>>() {}.type
            val mainSupporting =
                Gson().fromJson(sessionManager.getString("main_supporting"), DemoCap::class.java)

            viewModel.mainSupporting = mainSupporting.mainSupporting!!
        }
        isPartialCollect = intent.getBooleanExtra("isPartialCollect", false)

        if (isPartialCollect) {
            binding.spAmountLayout.hide()
            binding.txtAmountType.hide()
            binding.amountLabel.text = getString(R.string.partial_amount)
        }

        binding.txtName.text = nullSafeString(caseDetails.data?.name)
        binding.txtStatus.text = nullSafeString(caseDetails.data?.paymentStatus)
        binding.txtProductValue.text = nullSafeString(caseDetails.data?.loanType)
        if (caseDetails.data?.allocationDate != null) {
            binding.txtDate.text =
                caseDetails.data!!.allocationDate?.convertServerDateToNormal("dd, MMM yyyy") ?: ""

        }
        val loanId = "Loan ID : ${caseDetails.data?.loanAccountNo.toString()}"
        binding.txtPaymentLoanId.text = loanId
        binding.txtBankName.text = nullSafeString(caseDetails.data?.fiData?.name)
        val amount =
            (caseDetails.data?.totalDueAmount?.minus(caseDetails.data!!.amountCollected!!))
        if (amount != null) {
            binding.collectableAmountEt.text = amount.convertToCurrency()
        }

        val amountTypeAdapter = ArrayAdapter.createFromResource(
            this,
            R.array.amount_type,
            android.R.layout.simple_spinner_item
        )
        amountTypeAdapter.setDropDownViewResource(android.R.layout.select_dialog_singlechoice)
        binding.spAmount.adapter = amountTypeAdapter

        binding.spAmount.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent : AdapterView<*>?,
                    view : View?,
                    position : Int,
                    id : Long
                ) {
                    if (position == 1 || position == 2) {
                        binding.paymentAmountEt.isEnabled = false
                        if (position == 1) {
                            val totalDueAmount = caseDetails.data?.totalDueAmount?.minus(
                                caseDetails.data?.amountCollected!!
                            )
                            binding.paymentAmountEt.setText(totalDueAmount.toString())
                        } else {
                            val posAmount = caseDetails.data?.principalOutstandingAmount?.minus(
                                caseDetails.data?.amountCollected!!
                            )
                            binding.paymentAmountEt.setText(posAmount.toString())
                        }
                    } else {
                        binding.paymentAmountEt.isEnabled = true
                        binding.paymentAmountEt.setText("")
                    }
                }

                override fun onNothingSelected(parent : AdapterView<*>?) {

                }
            }

    }


    private fun openFileUploadDialog() {
        val items = arrayOf<CharSequence>("Camera", "Choose from gallery")
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Add File ")
        builder.setItems(items) { _ : DialogInterface?, item : Int ->
            when (items[item].toString()) {
                "Camera" -> {
                    val permissions = arrayOf(
                        Manifest.permission.CAMERA,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                    )
                    val rationale = "Please provide camera permission"
                    val options : Permissions.Options = Permissions.Options()
                        .setRationaleDialogTitle("Info")
                        .setSettingsDialogTitle("Warning")
                    Permissions.check(this, permissions, rationale, options,
                        object : PermissionHandler() {
                            override fun onGranted() {
                                openCamera()
                            }

                            override fun onDenied(
                                context : Context?,
                                deniedPermissions : ArrayList<String?>?
                            ) {
                                // permission denied, block the feature.
                            }
                        })
                }

                "Choose from gallery" -> {

                    val permissions = arrayOf(
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                    )
                    val rationale = "Please provide storage permission"
                    val options : Permissions.Options = Permissions.Options()
                        .setRationaleDialogTitle("Info")
                        .setSettingsDialogTitle("Warning")
                    Permissions.check(this, permissions, rationale, options,
                        object : PermissionHandler() {
                            override fun onGranted() {
                                val intent = Intent(
                                    Intent.ACTION_PICK,
                                    MediaStore.Images.Media.INTERNAL_CONTENT_URI
                                )
                                // intent.setType("image/*");
                                //  Allows any image file type. Change * to specific extension to limit it
                                //**These following line is the important one!
                                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, false)
                                imageGalleryPickerLauncher!!.launch(intent)
                                //SELECT_PICTURES is simply a global int used to check the calling intent in onActivityResult
                            }

                            override fun onDenied(
                                context : Context?,
                                deniedPermissions : ArrayList<String?>?
                            ) {
                                // permission denied, block the feature.
                            }
                        })
                }
            }
        }
        builder.show()
    }

    private fun getResultFromActivity() {
        imageCameraPickerLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            Timber.i("getResultCode---->" + result.resultCode)
            if (result.resultCode == RESULT_OK) {
                // There are no request codes
                Timber.i("data---->$imageUri")

                imageUri?.let {
                    Timber.i(" ------------- > $imageUri")
                    startCropActivity(imageUri!!)
                }

            }
        }

        imageGalleryPickerLauncher =
            registerForActivityResult(
                ActivityResultContracts.StartActivityForResult()
            ) { result ->
                if (result?.resultCode == RESULT_OK) {
                    val data = result.data
                    Timber.i("data-->$data")

                    data?.data?.let { startCropActivity(data.data!!) }

                }
            }

        cropImageLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == RESULT_OK) {
                    // There are no request codes
                    val data = result.data
                    if (data != null) {
                        val imageUri = data.getParcelableExtra<Uri>("sourceUri")
                        if (imageUri != null) {
                            try {
                                addedPhotosList.add(imageUri)
                                addImageAdapter.submitList(addedPhotosList)
                                //Convert Base64
                                var filepathString : String? = null
                                try {
                                    filepathString = imageUri.getFileNameFromUri(this)
                                } catch (e : Exception) {
                                    e.printStackTrace()
                                    toast("Error loading file")
                                }
                                if (filepathString != null) {
                                    try {
                                        photoB64List.add(
                                            "data:image/jpeg;base64,${
                                                File(
                                                    filepathString
                                                ).getBase64StringFile()
                                            }"
                                        )
                                    } catch (e : IOException) {
                                        e.printStackTrace()
                                    }
                                }
                            } catch (e : IOException) {
                                e.printStackTrace()
                            }
                        }
                    }
                }
            }
    }

    private fun startCropActivity(uri : Uri) {
        val intent = Intent(this, CropImageActivity::class.java)
        // Log.i(TAG,"startCropActivity  -- > "+uri.toString());
        intent.putExtra("sourceUri", uri.toString())
        intent.putExtra("cropping", "disable")
        cropImageLauncher!!.launch(intent)
        //overridePendingTransition(R.anim.right_in,R.anim.left_out);
    }

    private fun openCamera() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            imageUri = FileProvider.getUriForFile(
                this, this.applicationContext.packageName
                        + ".provider",
                createImageFile()!!
            )
            intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri)
            imageCameraPickerLauncher!!.launch(intent)
        } else {
            intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            imageUri = Uri.fromFile(createImageFile())
            intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri)
            imageCameraPickerLauncher!!.launch(intent)
        }
    }

    override fun removePhoto(uri : Uri) {
        photoB64List.removeAt(addedPhotosList.indexOf(uri))
        addedPhotosList.remove(uri)
        addImageAdapter.submitList(addedPhotosList)
    }

    override fun addPhoto() {
        openFileUploadDialog()
    }

    private fun validate() : Boolean {
        if (!isPartialCollect) {
            if (binding.spAmount.selectedItem.toString().isBlank() ||
                binding.spAmount.selectedItem.toString().isEmpty() ||
                binding.spAmount.selectedItem.toString() == "Select an Option"
            ) {
                toast("Please select amount type")
                return false
            }
        }
        if (binding.paymentAmountEt.text.toString().isBlank()
            || binding.paymentAmountEt.text.toString().isEmpty()
        ) {
            toast("Please enter payment amount")
            return false
        }
        val paymentAmount = binding.paymentAmountEt.text.toString().toInt()
        val collectableAmount =
            caseDetails.data?.totalDueAmount!!.minus(caseDetails.data?.amountCollected!!)
        if (isPartialCollect) {
            if (paymentAmount >= collectableAmount) {
                toast("Amount must be less than collectable amount")
                return false
            }
        } else {
            if (paymentAmount > collectableAmount) {
                toast("Amount should not be greater than collectable amount")
                return false
            }
        }

        if (binding.txtPaymentOrRecoveryDateValue.text.toString().isBlank()
            || binding.txtPaymentOrRecoveryDateValue.text.toString().isEmpty()
        ) {
            toast("Please enter payment recovery date")
            return false
        }

        when (binding.rgPayment.checkedRadioButtonId) {
            binding.rbOnline.id -> {
                if (binding.edtReferenceNo.text.toString().isBlank()
                    || binding.edtReferenceNo.text.toString().isEmpty()
                ) {
                    toast("Please enter reference number")
                    return false
                }
            }
            binding.rbCheque.id -> {
                if (binding.edtChequeNumber.text.toString().isBlank()
                    || binding.edtChequeNumber.text.toString().isEmpty()
                ) {
                    toast("Please enter cheque number")
                    return false
                }
            }
        }
        if (binding.remarksET.text.toString().isBlank()
            || binding.remarksET.text.toString().isEmpty()
        ) {
            toast("Please add remark")
            return false
        }
        return true
    }

    private fun nullSafeString(value : String?) : String {
        if (value.isNullOrEmpty() || value.isNullOrBlank() || value == "null" || value == "0") {
            return "-"
        }
        return value
    }

    override fun onBackPressed() {
        if (IS_PAYMENT_DONE) {
            val finishAllActivitiesExceptMain = Intent(this, MainActivity::class.java)
            finishAllActivitiesExceptMain.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            finishAllActivitiesExceptMain.putExtra("hasChangedPlanStatus", true)
            startActivity(finishAllActivitiesExceptMain)
            IS_PAYMENT_DONE = false
        }
        super.onBackPressed()
    }

}