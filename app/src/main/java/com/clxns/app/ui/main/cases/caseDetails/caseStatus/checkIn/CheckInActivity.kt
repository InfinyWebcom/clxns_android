package com.clxns.app.ui.main.cases.caseDetails.caseStatus.checkIn

import android.Manifest
import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.IntentSender
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.net.Uri
import android.os.*
import android.provider.MediaStore
import android.provider.Settings
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import com.clxns.app.R
import com.clxns.app.data.api.helper.NetworkResult
import com.clxns.app.data.model.AdditionalFieldModel
import com.clxns.app.data.model.CaseDetailsResponse
import com.clxns.app.data.model.StatusModel
import com.clxns.app.data.model.cases.CaseCheckInBody
import com.clxns.app.data.model.home.DemoCap
import com.clxns.app.data.preference.SessionManager
import com.clxns.app.databinding.ActivityCheckInBinding
import com.clxns.app.ui.main.cases.caseDetails.bottomsheets.AddMobileOrAddressBottomSheet
import com.clxns.app.ui.main.cases.caseDetails.bottomsheets.SubStatusActionBottomSheet
import com.clxns.app.ui.main.cases.caseDetails.caseStatus.AddImageAdapter
import com.clxns.app.ui.main.cases.caseDetails.caseStatus.paymentCollection.PaymentCollectionActivity
import com.clxns.app.utils.*
import com.clxns.app.utils.Constants.TOKEN
import com.clxns.app.utils.support.CropImageActivity
import com.clxns.app.utils.support.GridSpacingItemDecoration
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.tasks.Task
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.gson.Gson
import com.nabinbhandari.android.permissions.PermissionHandler
import com.nabinbhandari.android.permissions.Permissions
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.*
import java.util.*
import javax.inject.Inject
import kotlin.collections.ArrayList


@AndroidEntryPoint
class CheckInActivity : AppCompatActivity(), StatusAdapter.OnStatusListener,
    AddImageAdapter.RemovePhotoListener {

    private var remark : String = ""
    private var followUpDate : String = ""
    private var additionalFields : AdditionalFieldModel? = null
    lateinit var binding : ActivityCheckInBinding

    val viewModel : CheckInViewModel by viewModels()
    private var imageCameraPickerLauncher : ActivityResultLauncher<Intent>? = null
    var imageGalleryPickerLauncher : ActivityResultLauncher<Intent>? = null
    private var cropImageLauncher : ActivityResultLauncher<Intent>? = null
    private var imageUri : Uri? = null
    private lateinit var addImageAdapter : AddImageAdapter
    private var addedPhotosList : ArrayList<Uri> = ArrayList()
    private var photoB64List : ArrayList<String> = ArrayList()
    private lateinit var caseDetails : CaseDetailsResponse

    private var dispositionId : String = ""
    private var subDispositionId : String = ""

    @Inject
    lateinit var sessionManager : SessionManager

    private lateinit var progressDialog : ProgressDialog

    private lateinit var mLocationClient : FusedLocationProviderClient
    private lateinit var mLocationCallback : LocationCallback
    private lateinit var mLocationRequest : LocationRequest

    companion object {

        private var PAYMENT_MODE = false
        private var IS_SUB_DIS : Boolean = false
        private var IS_SET_RESULT : Boolean = false

        private var IS_PARTIALLY_COLLECT = false
        private var mLocationPermissionGranted = false
    }


    override fun onCreate(savedInstanceState : Bundle?) {
        super.onCreate(savedInstanceState)
        initView()
        initStatusAdapter()
        initLocationCallback()
        setListeners()
        setObserver()
    }

    private fun initStatusAdapter() {

        addImageAdapter = AddImageAdapter(this, this)
        binding.recyclerAddImage.adapter = addImageAdapter
        binding.recyclerAddImage.isNestedScrollingEnabled = false

        val statusList = ArrayList<StatusModel>()
        statusList.add(
            StatusModel(
                "PTP",
                ContextCompat.getDrawable(this, R.drawable.ic_thumb_up_24)
            )
        )
        statusList.add(
            StatusModel(
                "RTP",
                ContextCompat.getDrawable(this, R.drawable.ic_thumb_down_24)
            )
        )
        statusList.add(
            StatusModel(
                "Broken PTP",
                ContextCompat.getDrawable(this, R.drawable.ic_thumb_up_cross)
            )
        )
        statusList.add(
            StatusModel(
                "Dispute",
                ContextCompat.getDrawable(this, R.drawable.ic_lock_24)
            )
        )
        statusList.add(
            StatusModel(
                "Customer Not Found",
                ContextCompat.getDrawable(this, R.drawable.ic_wrong_location_24)
            )
        )
        statusList.add(
            StatusModel(
                "Call Back",
                ContextCompat.getDrawable(this, R.drawable.ic_notifications_24)
            )
        )
        statusList.add(
            StatusModel(
                "Add Mobile",
                ContextCompat.getDrawable(this, R.drawable.ic_round_note_add_24)
            )
        )
        statusList.add(
            StatusModel(
                "Add Address",
                ContextCompat.getDrawable(this, R.drawable.ic_round_note_add_24)
            )
        )
        statusList.add(
            StatusModel(
                "Collect",
                ContextCompat.getDrawable(this, R.drawable.ic_receipt_24)
            )
        )
        statusList.add(
            StatusModel(
                "Partially Collect",
                ContextCompat.getDrawable(this, R.drawable.ic_receipt_24)
            )
        )
        statusList.add(
            StatusModel(
                "Settlement/Foreclosure",
                ContextCompat.getDrawable(this, R.drawable.ic_phone_android_24)
            )
        )
        statusList.add(
            StatusModel(
                "Customer Deceased",
                ContextCompat.getDrawable(this, R.drawable.ic_round_person_off_24)
            )
        )
        binding.statusesRV.apply {
            layoutManager = GridLayoutManager(context, 3)
            addItemDecoration(GridSpacingItemDecoration(3, 40, true))
            adapter = StatusAdapter(statusList, this@CheckInActivity)
        }
    }

    private fun initLocationCallback() {
        mLocationClient =
            LocationServices.getFusedLocationProviderClient(this)

        mLocationCallback = object : LocationCallback() {
            override fun onLocationResult(result : LocationResult) {
                super.onLocationResult(result)
                val location : Location? = result.lastLocation
                if (location != null) {
                    viewModel.setLocation(location.latitude, location.longitude)
                    updateLocationCheckInUI(
                        R.drawable.ic_verified_24,
                        R.color.green,
                        getString(R.string.checked_in)
                    )
                } else {
                    updateLocationCheckInUI(
                        R.drawable.ic_baseline_cancel_24,
                        R.color.colorPrimary,
                        getString(R.string.check_in)
                    )
                }
                progressDialog.dismiss()
                mLocationClient.removeLocationUpdates(mLocationCallback)
            }
        }
    }

    private fun updateLocationCheckInUI(
        @DrawableRes img : Int,
        @ColorRes color : Int,
        txt : String
    ) {

        binding.txtLocationVerification.setCompoundDrawablesRelativeWithIntrinsicBounds(
            ContextCompat.getDrawable(this, img),
            null,
            null,
            null
        )
        binding.txtLocationCheckIn.text = txt
        binding.txtLocationCheckIn.setTextColor(
            ContextCompat.getColor(
                this,
                color
            )
        )

    }

    private fun isGPSEnabled() : Boolean {
        val locationManager =
            getSystemService(LOCATION_SERVICE) as LocationManager

        val providerEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)

        if (providerEnabled) {
            return true
        }
        return false
    }

    private fun checkLocationPermission() : Boolean {
        return (ActivityCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED)

    }


    private fun requestLocationPermission() {
        if (checkLocationPermission()) {
            mLocationPermissionGranted = true
        } else {
            permissionCallback.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    private fun createLocationRequest() {
        mLocationRequest = LocationRequest.create()
            .setInterval(2000)
            .setFastestInterval(5000)
            .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
    }

    @SuppressLint("MissingPermission")
    private fun getLocationUpdates() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            progressDialog.show()
            mLocationClient.requestLocationUpdates(
                mLocationRequest,
                mLocationCallback,
                Looper.getMainLooper()
            )
        }
    }

    override fun onPause() {
        super.onPause()
        if (::mLocationClient.isInitialized) {
            mLocationClient.removeLocationUpdates(mLocationCallback)
        }
    }


    private fun openLocationToggleDialog() {
        val builder = LocationSettingsRequest.Builder().addLocationRequest(mLocationRequest)

        val client : SettingsClient = LocationServices.getSettingsClient(this)
        val task : Task<LocationSettingsResponse> = client.checkLocationSettings(builder.build())

//        task.addOnSuccessListener {
//            // All location settings are satisfied. The client can initialize
//            // location requests here.
//            // ...
//            requireContext().toast("LOCATION SETTINGS RESPONSE SUCCESS")
//
//        }

        task.addOnFailureListener { exception ->
            if (exception is ResolvableApiException) {
                // Location settings are not satisfied, but this can be fixed
                // by showing the user a dialog.
                try {
                    // Show the dialog by calling startResolutionForResult(),
                    // and check the result in onActivityResult().

                    locationSettingCallback.launch(
                        IntentSenderRequest.Builder(exception.resolution).build()
                    )
                } catch (sendEx : IntentSender.SendIntentException) {
                    // Ignore the error.
                }
            }
        }
    }

    private val permissionCallback =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) {
            if (it) {
                mLocationPermissionGranted = true
                if (isGPSEnabled()) {
                    getLocationUpdates()
                } else {
                    openLocationToggleDialog()
                }
            } else {
                var isSelectedNeverAskAgain = false
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    isSelectedNeverAskAgain =
                        shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION)
                }
                if (isSelectedNeverAskAgain) {
                    //User has selected the denied option
                    showPermissionRequiredPopUp(
                        "Please provide location permission as it is mandatory to continue",
                        false
                    )
                } else {
                    //User has selected the denied & never ask again option
                    showPermissionRequiredPopUp(
                        "You've chosen never ask again for this permission.Please provide location permission from this app's permission settings.",
                        true
                    )
                }
            }
        }

    private val locationSettingCallback =
        registerForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) {
            if (it.resultCode == RESULT_OK) {
                getLocationUpdates()
            } else {
                toast("Please provide location access to continue")
                openLocationToggleDialog()
            }
        }

    private val forceRequestPermissionCallBack =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (checkLocationPermission()) {
                mLocationPermissionGranted = true
                if (isGPSEnabled()) {
                    getLocationUpdates()
                } else {
                    openLocationToggleDialog()
                }
            } else {
                binding.root.snackBar("Please provide location permission to continue.")
            }
        }

    private fun showPermissionRequiredPopUp(message : String, goToSettings : Boolean) {
        val dialog = MaterialAlertDialogBuilder(this)
        dialog.apply {
            setTitle("Location Permission Required")
            setMessage(message)
            create()
            setPositiveButton(
                "Set Permission"
            ) { d, _ ->
                if (goToSettings) {
                    val intentToSetting = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                    intentToSetting.data = Uri.fromParts("package", packageName, null)
                    forceRequestPermissionCallBack.launch(intentToSetting)
                } else {
                    requestLocationPermission()
                }
                d.dismiss()
            }
        }
        dialog.show()
    }

    private fun setListeners() {
        binding.imgBack.setOnClickListener { onBackPressed() }

        binding.txtLocationCheckIn.setOnClickListener {
            if (binding.txtLocationCheckIn.text.equals(getString(R.string.check_in))) {
                createLocationRequest()
                requestLocationPermission()
                if (mLocationPermissionGranted) {
                    if (isGPSEnabled()) {
                        getLocationUpdates()
                    } else {
                        openLocationToggleDialog()
                    }
                }
            }
        }

        binding.txtImageVerify.setOnClickListener {
            if (binding.txtImageVerify.text.equals("Upload")) {
                openFileUploadDialog()
            } else {
                showConfirmRemoveAllImageDialog()
            }
        }

    }

    private fun updateImageUploadUI(isImageAdded : Boolean) {
        if (isImageAdded) {
            binding.txtImageVerification.setCompoundDrawablesRelativeWithIntrinsicBounds(
                ContextCompat.getDrawable(this, R.drawable.ic_verified_24),
                null,
                null,
                null
            )
            binding.txtImageVerify.text = getString(R.string.clear)
            binding.txtImageVerify.setTextColor(
                ContextCompat.getColor(
                    this,
                    R.color.light_red
                )
            )
        } else {
            binding.txtImageVerification.setCompoundDrawablesRelativeWithIntrinsicBounds(
                ContextCompat.getDrawable(this, R.drawable.ic_baseline_cancel_24),
                null,
                null,
                null
            )
            binding.txtImageVerify.text = getString(R.string.upload)
            binding.txtImageVerify.setTextColor(
                ContextCompat.getColor(
                    this,
                    R.color.colorPrimary
                )
            )
        }
    }


    private fun showConfirmRemoveAllImageDialog() {
        val dialog = AlertDialog.Builder(this)
        dialog.setTitle("Remove All Images")
        dialog.setMessage("Are you sure you want to remove all the images?")

        dialog.setPositiveButton("Remove") { d, _ ->
            addedPhotosList.clear()
            photoB64List.clear()
            addImageAdapter.submitList(addedPhotosList)
            updateImageUploadUI(false)
            d.dismiss()
        }.setNegativeButton("Cancel") { d, _ ->
            d.dismiss()
        }

        val d = dialog.create()
        d.show()
        d.getButton(AlertDialog.BUTTON_POSITIVE).isAllCaps = false
        d.getButton(AlertDialog.BUTTON_POSITIVE)
            .setTextColor(ContextCompat.getColorStateList(this, R.color.light_red))
        d.getButton(AlertDialog.BUTTON_NEGATIVE).isAllCaps = false
    }

    private fun initView() {
        binding = ActivityCheckInBinding.inflate(layoutInflater)
        setContentView(binding.root)

        caseDetails = intent.getSerializableExtra("caseDetail") as CaseDetailsResponse

        viewModel.leadId = caseDetails.data?.loanAccountNo.toString()
        binding.txtCheckInName.text = caseDetails.data?.name
        binding.txtCheckInPaymentStatus.text = intent.getStringExtra("status")

        binding.txtCheckInName.text = caseDetails.data?.name
        binding.txtCheckInPaymentStatus.text = caseDetails.data?.paymentStatus
        val loanId = "Loan ID : ${caseDetails.data?.loanAccountNo.toString()}"
        binding.txtCheckInLoanId.text = loanId
        if (caseDetails.data?.allocationDate != null) {
            binding.txtDate.text = caseDetails.data!!.allocationDate
                ?.convertServerDateToNormal("dd, MMM yyyy") ?: ""
        }

        getResultFromActivity()

        progressDialog =
            getProgressDialog(this, "Please wait...", "We're fetching the current location.")
    }

    private fun saveCheckingData(
        comments : String,
        followUp : String,
        nextAction : String,
        additionalField : AdditionalFieldModel?
    ) {
        val body = CaseCheckInBody()
        body.loanAccountNo = viewModel.leadId!!
        body.dispositionId = dispositionId
        body.subDispositionId =
            (if (subDispositionId.isEmpty() || subDispositionId.isBlank()) null else subDispositionId)
        body.comments = comments
        body.followUp = followUp
        body.nextAction = nextAction
        body.additionalField = additionalField
        body.location = "${viewModel.lat},${viewModel.long}"
        body.supporting = photoB64List
        body.payment = null

        viewModel.saveCheckInData(
            sessionManager.getString(TOKEN)!!,
            body
        )

    }

    private fun setObserver() {
        viewModel.responseSaveCheckIn.observe(this) { response ->
            when (response) {
                is NetworkResult.Success -> {
                    binding.checkInProgressBar.hide()
                    if (!response.data?.error!!) {
                        photoB64List.clear()
                        addedPhotosList.clear()
                        addImageAdapter.submitList(addedPhotosList)
                        subDispositionId = ""
                        dispositionId = ""
                        updateLocationCheckInUI(
                            R.drawable.ic_baseline_cancel_24,
                            R.color.colorPrimary,
                            getString(R.string.check_in)
                        )
                        updateImageUploadUI(false)
                        viewModel.lat = ""
                        viewModel.long = ""


                        toast(response.data.title!!)
                        IS_SET_RESULT = true
                    } else {
                        subDispositionId = ""
                        dispositionId = ""
                        toast(response.data.title!!)
                    }
                }
                is NetworkResult.Error -> {
                    binding.checkInProgressBar.hide()
                    toast(response.message!!)
                    // show error message
                }
                is NetworkResult.Loading -> {
                    binding.checkInProgressBar.show()
                    // show a progress bar
                }
            }
        }

        viewModel.responseLeadContactUpdate.observe(this) { response ->
            when (response) {
                is NetworkResult.Success -> {
                    binding.checkInProgressBar.hide()
                    toast(response.data?.title!!)
                    IS_SET_RESULT = true
                }
                is NetworkResult.Error -> {
                    binding.checkInProgressBar.hide()
                    toast(response.message!!)
                    // show error message
                }
                is NetworkResult.Loading -> {
                    binding.checkInProgressBar.show()
                    // show a progress bar
                }
            }
        }

//        viewModel.responseCaseDetails.observe(this) { response ->
//            when (response) {
//                is NetworkResult.Success -> {
//                    binding.checkInProgressBar.hide()
//                    if (!response.data?.error!!) {
//                        caseDetails = response.data
//                        binding.txtCheckInName.text = response.data.data?.name
//                        binding.txtCheckInStatus.text = response.data.data?.paymentStatus
//                        if (response.data.data?.allocationDate != null) {
//                            binding.txtDate.text = response.data.data.allocationDate
//                                .convertServerDateToNormal("dd, MMM yyyy")
//                        }
//
//                    } else {
//                        toast(response.data.title!!)
//                        onBackPressed()
//                    }
//                    // bind data to the view
//                }
//                is NetworkResult.Error -> {
//                    binding.checkInProgressBar.hide()
//                    toast(response.message!!)
//                    // show error message
//                }
//                is NetworkResult.Loading -> {
//                    binding.checkInProgressBar.show()
//                    // show a progress bar
//                }
//            }
//        }

        viewModel.dispositionsIdResponse.observe(this) {
            dispositionId = it.toString()

            // adding delay to get both disposition and sub-disposition value from DB
            lifecycleScope.launch {
                delay(1000)
                if (PAYMENT_MODE) {
                    PAYMENT_MODE = false
                    val demoCap = DemoCap()
                    demoCap.mainSupporting = photoB64List
                    sessionManager.saveAnyData("main_supporting", Gson().toJson(demoCap))
                    val goToPaymentActivity =
                        Intent(applicationContext, PaymentCollectionActivity::class.java)
                    goToPaymentActivity.putExtra("caseDetail", caseDetails)
                    //goToPaymentActivity.putExtra("loan_account_number", viewModel.leadId)
                    goToPaymentActivity.putExtra("disposition_id", dispositionId)
                    goToPaymentActivity.putExtra(
                        "location",
                        "${viewModel.lat},${viewModel.long}"
                    )
                    goToPaymentActivity.putExtra("isPartialCollect", IS_PARTIALLY_COLLECT)
                    //goToPaymentActivity.putExtra("fosAssignedDate", caseDetails.data?.fosAssignedDate)
                    startActivity(goToPaymentActivity)
                    //paymentLauncher.launch(goToPaymentActivity)
                    binding.checkInProgressBar.hide()
                } else {
                    saveCheckingData(
                        remark,
                        followUpDate,
                        followUpDate,
                        additionalFields
                    )
                }
            }
        }

        viewModel.subDispositionsIdResponse.observe(this) {
            subDispositionId = it.toString()
        }
    }

//    private val paymentLauncher =
//        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
//            if (it.resultCode == Activity.RESULT_OK) {
//                IS_SET_RESULT = true
//                val data : Intent? = it.data
//                if (data != null) {
//                    val returnValue : Boolean = data.getBooleanExtra("close_app", false)
//                    if (returnValue) {
//                        val resultIntent = Intent()
//                        resultIntent.putExtra("close_app", true)
//                        setResult(RESULT_OK, resultIntent)
//
//                        val uiHandler = Handler(Looper.getMainLooper())
//                        uiHandler.postDelayed({ super.onBackPressed() }, 50)
//
//                    }
//                }
//            }
//        }

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

    private fun getResultFromActivity() {
        imageCameraPickerLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == RESULT_OK) {
                // There are no request codes
                imageUri?.let {
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
                                updateImageUploadUI(true)
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
        intent.putExtra("sourceUri", uri.toString())
        intent.putExtra("cropping", "disable")
        cropImageLauncher!!.launch(intent)
    }


    override fun openAddDetailsBottomSheet(isMobile : Boolean) {
        val openAddDetailsBS = AddMobileOrAddressBottomSheet.newInstance(
            object : AddMobileOrAddressBottomSheet.OnClick {

                override fun onMobileClick(mobile : String) {
                    viewModel.leadContactUpdate(
                        sessionManager.getString(TOKEN)!!,
                        viewModel.leadId!!,
                        "mobile",
                        mobile
                    )
                }

                override fun onAddressClick(address : String) {
                    viewModel.leadContactUpdate(
                        sessionManager.getString(TOKEN)!!,
                        viewModel.leadId!!,
                        "address",
                        address
                    )
                }
            })
        val bundle = Bundle()
        bundle.putBoolean("isMobile", isMobile)
        openAddDetailsBS.arguments = bundle
        openAddDetailsBS.show(
            supportFragmentManager,
            AddMobileOrAddressBottomSheet.TAG
        )
    }


    override fun openSubStatusActionBottomSheet(isPTPAction : Boolean, dispositionType : String) {

        if (viewModel.lat.isNullOrEmpty() || viewModel.long.isNullOrEmpty() ||
            viewModel.lat.isNullOrBlank() || viewModel.long.isNullOrBlank()
        ) {
            updateLocationCheckInUI(
                R.drawable.ic_baseline_cancel_24,
                R.color.colorPrimary,
                getString(R.string.check_in)
            )
            binding.root.snackBar("Please check in location")
            return
        }

        val openSubStatusAction =
            SubStatusActionBottomSheet(caseDetails,
                object : SubStatusActionBottomSheet.OnDispositionDoneClickListener {
                    override fun onClick(
                        dispositionType : String,
                        subDispositionType : String,
                        followUpDate : String,
                        remark : String,
                        additionalFields : AdditionalFieldModel?
                    ) {
                        prepareDataForCheckIn(
                            dispositionType,
                            subDispositionType,
                            followUpDate,
                            remark,
                            additionalFields
                        )
                    }
                })
        val bundle = Bundle()
        bundle.putBoolean("isPTPAction", isPTPAction)
        bundle.putString("dispositionType", dispositionType)
        bundle.putString("fosAssignedDate", caseDetails.data?.fosAssignedDate)
        openSubStatusAction.arguments = bundle
        openSubStatusAction.show(supportFragmentManager, SubStatusActionBottomSheet.TAG)
    }


    override fun openPaymentScreen(dispositionType : String) {
        IS_PARTIALLY_COLLECT = false
        if (viewModel.lat.isNullOrEmpty() || viewModel.long.isNullOrEmpty() ||
            viewModel.lat.isNullOrBlank() || viewModel.long.isNullOrBlank()
        ) {
            updateLocationCheckInUI(
                R.drawable.ic_baseline_cancel_24,
                R.color.colorPrimary,
                getString(R.string.check_in)
            )
            binding.root.snackBar("Please check in location")
            return
        }
        PAYMENT_MODE = true
        binding.checkInProgressBar.show()
        when (dispositionType) {
            "Collect" -> {
                viewModel.getDispositionIdFromRoomDB("Collected")
            }
            "Partially Collect" -> {
                IS_PARTIALLY_COLLECT = true
                viewModel.getDispositionIdFromRoomDB("Partially Collected")
            }
        }
    }

    override fun removePhoto(uri : Uri) {
        photoB64List.removeAt(addedPhotosList.indexOf(uri))
        addedPhotosList.remove(uri)
        addImageAdapter.submitList(addedPhotosList)
        if (addedPhotosList.isEmpty()) {
            updateImageUploadUI(false)
        }
    }

    override fun addPhoto() {
        openFileUploadDialog()
    }

    private fun prepareDataForCheckIn(
        dispositionType : String,
        subDispositionType : String,
        followUpDate : String,
        remark : String,
        additionalFields : AdditionalFieldModel?
    ) {
        this.remark = remark
        this.followUpDate = followUpDate
        this.additionalFields = additionalFields

        when (dispositionType) {
            "PTP" -> {
                viewModel.getSubDispositionIdFromRoomDB(subDispositionType)
                viewModel.getDispositionIdFromRoomDB("Promise to Pay")
            }
            "RTP" -> {
                viewModel.getDispositionIdFromRoomDB("Denial/RTP (Refused to Pay)")
            }
            "Broken PTP" -> {
                viewModel.getDispositionIdFromRoomDB(dispositionType)
            }
            "Dispute" -> {
                viewModel.getSubDispositionIdFromRoomDB(subDispositionType)
                viewModel.getDispositionIdFromRoomDB(dispositionType)
            }
            "Customer Not Found" -> {
                IS_SUB_DIS = true
                viewModel.getSubDispositionIdFromRoomDB(subDispositionType)
                viewModel.getDispositionIdFromRoomDB(dispositionType)
            }
            "Call Back" -> {
                viewModel.getDispositionIdFromRoomDB(dispositionType)
            }
            "Customer Deceased" -> {
                viewModel.getDispositionIdFromRoomDB(dispositionType)
            }
            "Settlement/Foreclosure" -> {
                viewModel.getDispositionIdFromRoomDB(dispositionType)
            }
        }
        binding.checkInProgressBar.show()
    }

    override fun onBackPressed() {
        if (IS_SET_RESULT) {
            setResult(RESULT_OK)
            IS_SET_RESULT = false
        }
        super.onBackPressed()
    }
}







