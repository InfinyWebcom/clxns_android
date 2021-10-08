package com.clxns.app.ui.main.cases.casedetails.casestatus.checkin

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.net.Uri
import android.os.*
import android.provider.MediaStore
import android.provider.Settings
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
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
import com.clxns.app.ui.main.cases.casedetails.bottomsheets.AddMobileOrAddressBottomSheet
import com.clxns.app.ui.main.cases.casedetails.bottomsheets.SubStatusActionBottomSheet
import com.clxns.app.ui.main.cases.casedetails.casestatus.paymentcollection.PaymentCollectionActivity
import com.clxns.app.ui.main.cases.casedetails.casestatus.repossesions.AddImageAdapter
import com.clxns.app.utils.*
import com.clxns.app.utils.support.CropImageActivity
import com.clxns.app.utils.support.GridSpacingItemDecoration
import com.google.android.gms.location.*
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import com.nabinbhandari.android.permissions.PermissionHandler
import com.nabinbhandari.android.permissions.Permissions
import dagger.hilt.android.AndroidEntryPoint
import java.io.*
import java.util.*
import javax.inject.Inject
import kotlin.collections.ArrayList


@AndroidEntryPoint
class CheckInActivity : AppCompatActivity(), StatusAdapter.OnStatusListener,
    AddImageAdapter.removePhoto {

    private var IS_SUB_DIS: Boolean = false
    private var IS_SET_RESULT: Boolean = false
    var remark: String = ""
    var followUpDate: String = ""
    var additionalFields: AdditionalFieldModel? = null
    lateinit var binding: ActivityCheckInBinding
    lateinit var ctx: Context
    val viewModel: CheckInViewModel by viewModels()
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    var imageCameraPickerLauncher: ActivityResultLauncher<Intent>? = null
    var imageGalleryPickerLauncher: ActivityResultLauncher<Intent>? = null
    var cropImageLaucher: ActivityResultLauncher<Intent>? = null
    private var mCurrentPhotoPath: String? = null
    private var imageUri: Uri? = null
    lateinit var addImageAdapter: AddImageAdapter
    private var addedPhotosList: ArrayList<Uri> = ArrayList()
    private var photoB64List: ArrayList<String> = ArrayList()
    var caseDetails: CaseDetailsResponse? = null

    private var dispositionId: String = ""
    private var subDispositionId: String = ""

    @Inject
    lateinit var sessionManager: SessionManager

    companion object {
        private const val PERMISSION_ID = 101
        private const val CAMERA_REQUEST_CODE = 1
        private const val GALLERY_REQUEST_CODE = 2
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.statusBarColor = ContextCompat.getColor(this, R.color.colorPrimary)
        ctx = this
        binding = ActivityCheckInBinding.inflate(layoutInflater)
        setContentView(binding.root)
        viewModel.leadId = intent.getStringExtra("loan_account_number")
        setInit()
        setListeners()
        setObserver()

        if (viewModel.leadId != null) {
            viewModel.getCaseDetails(
                sessionManager.getString(Constants.TOKEN)!!,
                viewModel.leadId!!
            )
        } else {
            toast("Error while fetching details")
            onBackPressed()
        }
    }

    private fun setListeners() {

        binding.imgBack.setOnClickListener { onBackPressed() }


        binding.txtLocationCheckIn.setOnClickListener {
            if (binding.txtLocationCheckIn.text.equals("Check In")) {

                val permissions = arrayOf(Manifest.permission.ACCESS_FINE_LOCATION)
                val rationale = "Please provide location permission"
                val options: Permissions.Options = Permissions.Options()
                    .setRationaleDialogTitle("Info")
                    .setSettingsDialogTitle("Warning")
                Permissions.check(
                    this,
                    permissions,
                    rationale,
                    options,
                    object : PermissionHandler() {
                        override fun onGranted() {
                            getLastLocation()
//                            if (getLocationCord()) {
                            binding.txtLocationCheckIn.text = "Check In"
                            binding.txtLocationVerification.setCompoundDrawablesRelativeWithIntrinsicBounds(
                                ContextCompat.getDrawable(
                                    this@CheckInActivity,
                                    R.drawable.ic_verified_24
                                ),
                                null,
                                null,
                                null
                            )
                            Snackbar.make(
                                binding.root,
                                "Location has been checked in",
                                Snackbar.LENGTH_LONG
                            ).show()
//                            } else {
//                                mLastLocation = null
//                            }
                        }

                        override fun onDenied(
                            context: Context?,
                            deniedPermissions: ArrayList<String?>?
                        ) {
                            // permission denied, block the feature.
                        }
                    })
            }
        }

        binding.txtImageVerify.setOnClickListener {
            if (binding.txtImageVerify.text.equals("Upload")) {
                openFileUploadDialog()
            } else {
                binding.txtImageVerification.setCompoundDrawablesRelativeWithIntrinsicBounds(
                    ContextCompat.getDrawable(this, R.mipmap.ic_delete),
                    null,
                    null,
                    null
                )
                binding.verifiedImageUpload.setImageResource(android.R.color.transparent)
                binding.verifiedImageUpload.visibility = View.GONE
                binding.txtImageVerify.text = "Upload"
                binding.txtImageVerify.setTextColor(
                    ContextCompat.getColor(
                        this,
                        R.color.colorPrimary
                    )
                )
            }
        }

    }

    private fun setInit() {

        addImageAdapter = AddImageAdapter(this, ctx)
        binding.recyclerAddImage.adapter = addImageAdapter
        binding.recyclerAddImage.isNestedScrollingEnabled = false

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)

        binding.txtCheckInStatus.text = intent.getStringExtra("status")
        binding.txtCheckInName.text = intent.getStringExtra("name")

        getResultFromActivity()

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
            adapter = StatusAdapter(context, statusList, this@CheckInActivity)
        }
    }

    private fun saveCheckingData(
        comments: String,
        followUp: String,
        nextAction: String,
        additionalField: AdditionalFieldModel?
    ) {
        binding.progressBar.show()

        var body = CaseCheckInBody()
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
            sessionManager.getString(Constants.TOKEN)!!,
            body
        )

    }

    private fun setObserver() {
        viewModel.responseSaveCheckIn.observe(this) { response ->
            when (response) {
                is NetworkResult.Success -> {
                    binding.progressBar.hide()
                    if (!response.data?.error!!) {
                        photoB64List.clear()
                        addedPhotosList.clear()
                        addImageAdapter.submitList(addedPhotosList)
                        subDispositionId = ""
                        dispositionId = ""
                        binding.txtLocationVerification.setCompoundDrawablesRelativeWithIntrinsicBounds(
                            ContextCompat.getDrawable(
                                this@CheckInActivity,
                                R.mipmap.ic_delete
                            ),
                            null,
                            null,
                            null
                        )
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

        viewModel.responseLeadContactUpdate.observe(this) { response ->
            when (response) {
                is NetworkResult.Success -> {
                    binding.progressBar.hide()
                    toast(response.data?.title!!)
                    IS_SET_RESULT = true
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

        viewModel.responseCaseDetails.observe(this) { response ->
            when (response) {
                is NetworkResult.Success -> {
                    binding.progressBar.hide()
                    if (!response.data?.error!!) {
                        caseDetails = response.data
                        binding.txtCheckInName.text = response.data.data?.name
                        binding.txtCheckInStatus.text = response.data.data?.paymentStatus
                        if (response.data.data?.allocationDate != null) {
                            binding.txtDate.text = response.data.data?.allocationDate.toString()
                                .formatDate("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", "dd-MM-yyyy")
                        }

                    } else {
                        toast(response.data.title!!)
                        onBackPressed()
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

        viewModel.dispositionsIdResponse.observe(this) {
            dispositionId = it.toString()

            // adding delay to get both disposition and sub-disposition value from DB
            Handler(Looper.getMainLooper()).postDelayed(
                {
                    if (PAYMENT_MODE) {
                        PAYMENT_MODE = false
                        val demoCap = DemoCap()
                        demoCap.mainSupporting = photoB64List
                        sessionManager.saveAnyData("main_supporting", Gson().toJson(demoCap))
                        val intent =
                            Intent(this, PaymentCollectionActivity::class.java)
                        intent.putExtra("loan_account_number", viewModel.leadId)
                        intent.putExtra("disposition_id", dispositionId)
                        intent.putExtra("location", "${viewModel.lat},${viewModel.long}")

                        paymentLauncher.launch(intent)
                        binding.progressBar.hide()
                    } else {
                        saveCheckingData(
                            remark,
                            followUpDate,
                            followUpDate,
                            additionalFields
                        )
                    }
                },
                1500
            )

        }

        viewModel.subDispositionsIdResponse.observe(this) {
            subDispositionId = it.toString()
        }
    }

    private val paymentLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == Activity.RESULT_OK) {
                IS_SET_RESULT = true
                val data: Intent? = it.data
                if (data != null) {
                    val returnValue: Boolean = data!!.getBooleanExtra("close_app", false)
                    if (returnValue) {
                        val resultIntent = Intent()
                        resultIntent.putExtra("close_app", true)
                        setResult(RESULT_OK, resultIntent)

                        val uiHandler = Handler(Looper.getMainLooper())
                        uiHandler.postDelayed(Runnable { super.onBackPressed() }, 50)

                    }
                }
            }
        }

    private fun openFileUploadDialog() {
        val items = arrayOf<CharSequence>("Camera", "Choose from gallery")
        val builder = AlertDialog.Builder(ctx)
        builder.setTitle("Add File ")
        builder.setItems(items) { dialog: DialogInterface?, item: Int ->
            when (items[item].toString()) {
                "Camera" -> {
                    val permissions = arrayOf(
                        Manifest.permission.CAMERA,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                    )
                    val rationale = "Please provide camera permission"
                    val options: Permissions.Options = Permissions.Options()
                        .setRationaleDialogTitle("Info")
                        .setSettingsDialogTitle("Warning")
                    Permissions.check(this, permissions, rationale, options,
                        object : PermissionHandler() {
                            override fun onGranted() {
                                openCamera()
                            }

                            override fun onDenied(
                                context: Context?,
                                deniedPermissions: ArrayList<String?>?
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
                    val options: Permissions.Options = Permissions.Options()
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
                                context: Context?,
                                deniedPermissions: ArrayList<String?>?
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

    private fun getResultFromActivity() {
        imageCameraPickerLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult(),
            object : ActivityResultCallback<ActivityResult> {
                override fun onActivityResult(result: ActivityResult) {
                    if (result.resultCode == RESULT_OK) {
                        // There are no request codes
                        imageUri?.let {
                            startCropActivity(imageUri!!)
                        }

                    }
                }
            })

        imageGalleryPickerLauncher =
            registerForActivityResult(
                ActivityResultContracts.StartActivityForResult()
            ) { result ->
                if (result?.resultCode == RESULT_OK) {
                    val data = result.data
                    data?.data?.let { startCropActivity(data.data!!) }
                }
            }

        cropImageLaucher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
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
                                var filepathString: String? = null
                                try {
                                    filepathString = imageUri.getFileNameFromUri(ctx)
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                   toast("Error loading file")
                                }
                                if (filepathString != null) {
                                    try {
                                        photoB64List.add("data:image/jpeg;base64,${File(filepathString).getBase64StringFile()}")
                                    } catch (e: IOException) {
                                        e.printStackTrace()
                                    }
                                }
                            } catch (e: IOException) {
                                e.printStackTrace()
                            }
                        }
                    }
                }
            }
    }

    private fun startCropActivity(uri: Uri) {
        val intent = Intent(ctx, CropImageActivity::class.java)
        intent.putExtra("sourceUri", uri.toString())
        intent.putExtra("cropping", "disable")
        cropImageLaucher!!.launch(intent)
    }


    @SuppressLint("MissingPermission")
    private fun getLastLocation() {

        if (checkPermissions()) {
            if (isLocationEnabled()) {
                fusedLocationProviderClient.lastLocation.addOnCompleteListener(object :
                    OnCompleteListener<Location> {
                    override fun onComplete(task: Task<Location>) {
                        val location = task.result
                        if (location == null) {
                            toast("Error while fetching location try again")
                            binding.txtLocationCheckIn.text = "Check In"
                            binding.txtLocationVerification.setCompoundDrawablesRelativeWithIntrinsicBounds(
                                ContextCompat.getDrawable(
                                    this@CheckInActivity,
                                    R.mipmap.ic_delete
                                ),
                                null,
                                null,
                                null
                            )
                            requestNewLocationData()
                        } else {
                            viewModel.setLocation(location.latitude, location.longitude)
                        }


                    }
                })

            } else {
                Toast.makeText(this, "Please turn on" + " your location...", Toast.LENGTH_LONG)
                    .show()
                val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                startActivity(intent)
            }

        } else {
            requestPermission()
        }

    }

    @SuppressLint("MissingPermission")
    private fun requestNewLocationData() {
        val mLocationRequest = LocationRequest.create().apply {
            interval = 5
            fastestInterval = 0
            numUpdates = 1
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
//            maxWaitTime= 100
        }

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        fusedLocationProviderClient.requestLocationUpdates(
            mLocationRequest,
            mLocationCallback,
            Looper.myLooper()
        )
    }


    private fun isLocationEnabled(): Boolean {
        val locationManager: LocationManager =
            getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER
        )
    }

    private fun requestPermission() {
        ActivityCompat.requestPermissions(
            this, arrayOf(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
            ), PERMISSION_ID
        )
    }

    private fun checkPermissions(): Boolean {
        return ActivityCompat.checkSelfPermission(
            ctx,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    object mLocationCallback : LocationCallback() {
        override fun onLocationResult(locationRequest: LocationResult) {
            super.onLocationResult(locationRequest)

        }
    }

    override fun openAddDetailsBottomSheet(isMobile: Boolean) {
        val openAddDetailsBS = AddMobileOrAddressBottomSheet.newInstance(
            object : AddMobileOrAddressBottomSheet.OnClick {

                override fun onMobileClick(mobile: String) {
                    binding.progressBar.show()
                    viewModel.leadContactUpdate(
                        sessionManager.getString(Constants.TOKEN)!!,
                        viewModel.leadId!!,
                        "mobile",
                        mobile
                    )
                }

                override fun onAddressClick(address: String) {
                    binding.progressBar.show()
                    viewModel.leadContactUpdate(
                        sessionManager.getString(Constants.TOKEN)!!,
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


    override fun openSubStatusActionBottomSheet(isPTPAction: Boolean, dispositionType: String) {

        if (viewModel.lat.isNullOrEmpty() || viewModel.long.isNullOrEmpty() ||
            viewModel.lat.isNullOrBlank() || viewModel.long.isNullOrBlank()
        ) {
            binding.txtLocationCheckIn.text = "Check In"
            binding.txtLocationVerification.setCompoundDrawablesRelativeWithIntrinsicBounds(
                ContextCompat.getDrawable(
                    this@CheckInActivity,
                    R.mipmap.ic_delete
                ),
                null,
                null,
                null
            )
            binding.root.snackBar("Please check in location")
            return
        }

        val openSubStatusAction =
            SubStatusActionBottomSheet.newInstance(caseDetails,
                object : SubStatusActionBottomSheet.OnClick {
                    override fun onClick(
                        dispositionType: String,
                        subDispositionType: String,
                        followUpDate: String,
                        remark: String,
                        additionalFields: AdditionalFieldModel?
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
        openSubStatusAction.arguments = bundle
        openSubStatusAction.show(supportFragmentManager, SubStatusActionBottomSheet.TAG)
    }

    var PAYMENT_MODE = false
    override fun openPaymentScreen(dispositionType: String) {
        if (viewModel.lat.isNullOrEmpty() || viewModel.long.isNullOrEmpty() ||
            viewModel.lat.isNullOrBlank() || viewModel.long.isNullOrBlank()
        ) {
            binding.txtLocationCheckIn.text = "Check In"
            binding.txtLocationVerification.setCompoundDrawablesRelativeWithIntrinsicBounds(
                ContextCompat.getDrawable(
                    this@CheckInActivity,
                    R.mipmap.ic_delete
                ),
                null,
                null,
                null
            )
            binding.root.snackBar("Please check in location")
            return
        }
        PAYMENT_MODE = true
        binding.progressBar.show()
        when (dispositionType) {
            "Collect" -> {
                viewModel.getDispositionIdFromRoomDB("Collected")
            }
            "Partially Collect" -> {
                viewModel.getDispositionIdFromRoomDB("Partially Collected")
            }
            else -> {
                viewModel.getDispositionIdFromRoomDB(dispositionType)
            }

        }
    }

    override fun removePhoto(uri: Uri) {
        photoB64List.removeAt(addedPhotosList.indexOf(uri))
        addedPhotosList.remove(uri)
        addImageAdapter.submitList(addedPhotosList)
    }

    override fun addPhoto() {
        openFileUploadDialog()
    }

    private fun prepareDataForCheckIn(
        dispositionType: String,
        subDispositionType: String,
        followUpDate: String,
        remark: String,
        additionalFields: AdditionalFieldModel?
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
        }
        binding.progressBar.show()
    }

    override fun onBackPressed() {
        if (IS_SET_RESULT) {
            setResult(RESULT_OK)
        }
        super.onBackPressed()
    }
}







