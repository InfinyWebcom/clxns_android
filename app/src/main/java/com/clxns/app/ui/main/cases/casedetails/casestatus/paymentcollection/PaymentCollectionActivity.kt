package com.clxns.app.ui.main.cases.casedetails.casestatus.paymentcollection

import android.Manifest
import android.app.DatePickerDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.LinearLayout
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
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
import com.clxns.app.ui.main.cases.casedetails.casestatus.repossesions.AddImageAdapter
import com.clxns.app.utils.*
import com.clxns.app.utils.support.CropImageActivity
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.nabinbhandari.android.permissions.PermissionHandler
import com.nabinbhandari.android.permissions.Permissions
import dagger.hilt.android.AndroidEntryPoint
import java.io.File
import java.io.IOException
import java.util.*
import javax.inject.Inject
import kotlin.collections.ArrayList

@AndroidEntryPoint
class PaymentCollectionActivity : AppCompatActivity(), AddImageAdapter.removePhoto {

    lateinit var binding : ActivityPaymentCollectionBinding
    lateinit var ctx : Context
    val viewModel : PaymentCollectionViewModel by viewModels()

    @Inject
    lateinit var sessionManager : SessionManager
    private val REQUEST_IMAGE_CAPTURE = 854
    private var imageUri : Uri? = null
    private var mCurrentPhotoPath : String? = null
    private var addedPhotosList : ArrayList<Uri> = ArrayList()
    private var photoB64List : ArrayList<String> = ArrayList()
    private var cropImageLauncher : ActivityResultLauncher<Intent>? = null
    lateinit var addImageAdapter : AddImageAdapter
    var recoveryDate = ""
    var paymentType = "ONLINE"
    var caseDetails : CaseDetailsResponse? = null

    private var year = 0
    private var month = 0
    private var day = 0
    var imageCameraPickerLauncher : ActivityResultLauncher<Intent>? = null
    var imageGalleryPickerLauncher : ActivityResultLauncher<Intent>? = null

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


    override fun onCreate(savedInstanceState : Bundle?) {
        super.onCreate(savedInstanceState)
        window.statusBarColor = ContextCompat.getColor(this, R.color.colorPrimary)
        setInit()
        setListeners()
        getResultFromActivity()
        setObserver()
        viewModel.loanAccountNumber = intent.getStringExtra("loan_account_number")
        viewModel.dispositionId = intent.getStringExtra("disposition_id")
        viewModel.location = intent.getStringExtra("location")
        if (sessionManager.getString("main_supporting") != null) {
            val myType = object : TypeToken<ArrayList<String>>() {}.type
            val mainSupporting =
                Gson().fromJson(sessionManager.getString("main_supporting"), DemoCap::class.java)

            viewModel.mainSupporting = mainSupporting.mainSupporting!!
        }

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
//                val finishAllActivitiesExceptMain = Intent(this, MainActivity::class.java)
//                finishAllActivitiesExceptMain.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
//                startActivity(finishAllActivitiesExceptMain)

                val resultIntent = Intent()
                resultIntent.putExtra("close_app", true)
                setResult(RESULT_OK, resultIntent)
                super.onBackPressed()

            } else {
                if (validate()) {
                    binding.progressBar.show()

                    //additional fields
                    val additionalField = AdditionalFieldModel()
                    additionalField.recoveryDate = recoveryDate
                    additionalField.paymentMode = paymentType
                    additionalField.recoveryType = binding.spAmount.selectedItem.toString()
                    additionalField.refChequeNo =
                        if (paymentType == "CHEQUE") binding.edtReferenceType.text.toString() else (if (paymentType == "ONLINE") binding.edtReferenceType.text.toString() else "")
                    additionalField.recoveredAmount =
                        binding.paymentAmountEt.text.toString().toString()

                    //payment details
                    val payment = PaymentModel()
                    payment.leadId = viewModel.loanAccountNumber
                    payment.loanNo = viewModel.loanAccountNumber
                    payment.amtType = binding.spAmount.selectedItem.toString()
                    payment.paymentMode = paymentType
                    payment.recoveryDate = recoveryDate
                    payment.refNo =
                        if (paymentType == "ONLINE") binding.edtReferenceType.text.toString() else ""
                    payment.chequeNo =
                        if (paymentType == "CHEQUE") binding.edtReferenceType.text.toString() else ""
                    payment.remark = binding.remarksET.text.toString()
                    payment.supporting = photoB64List
                    payment.collectedAmt = binding.paymentAmountEt.text.toString().toLong()

                    //body
                    val body = CaseCheckInBody()
                    body.loanAccountNo = viewModel.loanAccountNumber!!
                    body.dispositionId = viewModel.dispositionId!!
                    body.subDispositionId = null
                    body.comments = binding.remarksET.text.toString()
                    body.followUp = recoveryDate
                    body.nextAction = recoveryDate
                    body.additionalField = additionalField
                    body.location = viewModel.location!!
                    body.supporting = viewModel.mainSupporting
                    body.payment = payment

                    viewModel.saveCheckInData(
                        sessionManager.getString(Constants.TOKEN)!!,
                        body
                    )
                }
            }

        }

        binding.collectableAmountEt.setOnClickListener {
            it.showDialog(caseDetails?.data?.totalDueAmount!!, caseDetails?.data?.amountCollected!!)
        }

    }

    private fun setObserver() {
        viewModel.responseCaseDetails.observe(this) { response ->
            when (response) {
                is NetworkResult.Success -> {
                    binding.progressBar.hide()
                    if (!response.data?.error!!) {
                        caseDetails = response.data
                        binding.txtName.text = nullSafeString(response.data.data?.name)
                        binding.txtStatus.text = nullSafeString(response.data.data?.paymentStatus)
                        binding.txtProductValue.text = nullSafeString(response.data.data?.loanType)
                        if (response.data.data?.allocationDate != null) {
                            binding.txtDate.text =
                                response.data.data?.allocationDate.toString().formatDate(
                                    "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",
                                    "dd-MM-yyyy"
                                )
                        }
                        binding.txtBankName.text = nullSafeString(response.data.data?.fiData?.name)
                        var amount =
                            (response.data.data?.totalDueAmount?.minus(response.data.data?.amountCollected!!))
                        binding.collectableAmountEt.text =
                            "₹${amount}"/*{nullSafeString(amount.toString())}*/
                    } else {
                        toast(response.data.title!!)
                        finish()
                    }
                    // bind data to the view
                }
                is NetworkResult.Error -> {
                    binding.progressBar.hide()
                    toast(response.message!!)
                    // show error message
                }
                is NetworkResult.Loading -> {
                    binding.progressBar.show()
                    // show a progress bar
                }
            }
        }

        viewModel.responseSaveCheckIn.observe(this) { response ->
            when (response) {
                is NetworkResult.Success -> {
                    binding.progressBar.hide()
                    if (!response.data?.error!!) {
                        toast(response.data.title!!)
                        val l : LinearLayout = findViewById(R.id.mainLinearLayout)
                        l.forEachChildView {
                            it.isEnabled = false
                        }
                        IS_PAYMENT_DONE = true
                        binding.generateReceiptBtn.text = "back to my plan"
                    } else {
                        toast(response.data.title!!)
                    }
                }
                is NetworkResult.Error -> {
                    binding.progressBar.hide()
                    toast(response.message!!)
                    // show error message
                }
                is NetworkResult.Loading -> {
                    binding.progressBar.show()
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
            DatePickerDialog(ctx, { _, year, month, day ->
                Log.i(javaClass.name, "month--->" + MONTHS[month])

                //set current time as default time
                val millis = System.currentTimeMillis()
                val c = Calendar.getInstance()
                c.timeInMillis = millis
                val hours = c.get(Calendar.HOUR)
                val minutes = c.get(Calendar.MINUTE)

//                T${String.format("%02d", hours + 12)}:${
//                String.format(
//                    "%02d",
//                    minutes
//                )
//            }:00.000Z

                binding.txtPaymentOrRecoveryDateValue.text =
                    "$day " + MONTHS[month] + " $year"
                recoveryDate =
                    "${year}-${String.format("%02d", month + 1)}-${
                        String.format(
                            "%02d",
                            day
                        )
                    }"

            }, year, month, day)
        //datePickerDialog.datePicker.minDate = System.currentTimeMillis()
        datePickerDialog.show()
    }

    private fun setCashVisibility() {
        paymentType = "CASH"
        binding.txtReferenceNo.visibility = View.GONE
        binding.edtReferenceType.visibility = View.GONE
    }

    private fun setOnlineVisibility() {
        paymentType = "ONLINE"
        binding.txtReferenceNo.visibility = View.VISIBLE
        binding.edtReferenceType.visibility = View.VISIBLE
        binding.txtReferenceNo.text = "Reference No."


    }

    private fun setChequeVisibility() {
        paymentType = "CHEQUE"
        binding.txtReferenceNo.text = "Cheque No."
        binding.txtReferenceNo.visibility = View.VISIBLE
        binding.edtReferenceType.visibility = View.VISIBLE

    }

    private fun setInit() {
        ctx = this
        binding = ActivityPaymentCollectionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        addImageAdapter = AddImageAdapter(this, ctx)
        binding.recyclerAddImage.adapter = addImageAdapter
        binding.recyclerAddImage.isNestedScrollingEnabled = false

        val calendar = Calendar.getInstance()
        year = calendar.get(Calendar.YEAR)
        day = calendar.get(Calendar.DAY_OF_MONTH)
        month = calendar.get(Calendar.MONTH)


        val amountTypeAdapter = ArrayAdapter.createFromResource(
            ctx,
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
                            binding.paymentAmountEt.setText(caseDetails?.data?.totalDueAmount.toString())
                        } else {
                            binding.paymentAmountEt.setText(caseDetails?.data?.principalOutstandingAmount.toString())
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
        val builder = AlertDialog.Builder(ctx, R.style.AlertDialogCustom)
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
            ActivityResultContracts.StartActivityForResult(),
            object : ActivityResultCallback<ActivityResult> {
                override fun onActivityResult(result : ActivityResult) {
                    Log.i(javaClass.name, "getResultCode---->" + result.resultCode)
                    if (result.resultCode == RESULT_OK) {
                        // There are no request codes
                        val data = result.data
                        Log.i(javaClass.name, "data---->$imageUri")

                        imageUri?.let {
                            Log.i("onActivity", " ------------- > $imageUri")
                            startCropActivity(imageUri!!)
                        }

                    }
                }
            })

        imageGalleryPickerLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult(), object :
                ActivityResultCallback<ActivityResult> {
                override fun onActivityResult(result : ActivityResult?) {
                    if (result?.resultCode == RESULT_OK) {
                        val data = result.data
                        Log.i(javaClass.name, "data-->$data")

                        data?.data?.let { startCropActivity(data.data!!) }

                    }


                }

            })

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
                                    filepathString = imageUri.getFileNameFromUri(ctx)
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
        val intent = Intent(ctx, CropImageActivity::class.java)
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
                ctx, ctx!!.applicationContext.packageName
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
        if (binding.spAmount.selectedItem.toString().isBlank() ||
            binding.spAmount.selectedItem.toString().isEmpty() ||
            binding.spAmount.selectedItem.toString() == "Select an Option"
        ) {
            toast("Please select amount type")
            return false
        }
        if (binding.paymentAmountEt.text.toString().isBlank()
            || binding.paymentAmountEt.text.toString().isEmpty()
        ) {
            toast("Please enter payment amount")
            return false
        }
        if (binding.paymentAmountEt.text.toString()
                .toInt() > (caseDetails!!.data?.totalDueAmount!!.minus(caseDetails!!.data?.amountCollected!!)
                    )
        ) {
            toast("Amount cannot be greater than collectable amount")
            return false
        }

        if (binding.txtPaymentOrRecoveryDateValue.text.toString().isBlank()
            || binding.txtPaymentOrRecoveryDateValue.text.toString().isEmpty()
        ) {
            toast("Please enter payment recovery date")
            return false
        }

        when (binding.rgPayment.checkedRadioButtonId) {
            binding.rbOnline.id -> {
                if (binding.edtReferenceType.text.toString().isBlank()
                    || binding.edtReferenceType.text.toString().isEmpty()
                ) {
                    toast("Please enter reference number")
                    return false
                }
            }
            binding.rbCheque.id -> {
                if (binding.edtReferenceType.text.toString().isBlank()
                    || binding.edtReferenceType.text.toString().isEmpty()
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
            setResult(RESULT_OK)
        }
        super.onBackPressed()
    }

}