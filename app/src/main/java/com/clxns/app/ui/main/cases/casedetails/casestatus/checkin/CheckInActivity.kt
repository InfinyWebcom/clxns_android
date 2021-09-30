package com.clxns.app.ui.main.cases.casedetails.casestatus.checkin

import android.Manifest
import android.annotation.SuppressLint
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
import android.util.Base64
import android.util.Base64OutputStream
import android.util.Log
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
import com.clxns.app.data.model.CaseDetailsResponse
import com.clxns.app.data.model.StatusModel
import com.clxns.app.data.preference.SessionManager
import com.clxns.app.databinding.ActivityCheckInBinding
import com.clxns.app.ui.main.cases.casedetails.bottomsheets.AddMobileOrAddressBottomSheet
import com.clxns.app.ui.main.cases.casedetails.bottomsheets.SubStatusActionBottomSheet
import com.clxns.app.ui.main.cases.casedetails.casestatus.paymentcollection.PaymentCollectionActivity
import com.clxns.app.ui.main.cases.casedetails.casestatus.repossesions.AddImageAdapter
import com.clxns.app.utils.*
import com.clxns.app.utils.support.CropImageActivity
import com.clxns.app.utils.support.FileUtils
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
import java.net.URI
import java.net.URISyntaxException
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject
import kotlin.collections.ArrayList


@AndroidEntryPoint
class CheckInActivity : AppCompatActivity(), StatusAdapter.OnStatusListener,
    AddImageAdapter.removePhoto {

    var IS_SUB_DIS: Boolean = false
    var remark: String = ""
    var followUpDate: String = ""
    var additionalFields: String = ""
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
            finish()
        }
    }

    private fun setListeners() {

        binding.imgBack.setOnClickListener { finish() }


        binding.txtLocationCheckIn.setOnClickListener {
            if (binding.txtLocationCheckIn.text.equals("Verify")) {

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
                                "Location has been verified",
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

        //getLastLocation()
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
        additionalField: String
    ) {
        binding.progressBar.show()
        viewModel.saveCheckInData(
            sessionManager.getString(Constants.TOKEN)!!,
            viewModel.leadId!!,
            dispositionId,
            (if (subDispositionId.isEmpty() || subDispositionId.isBlank()) null else subDispositionId),
            comments,
            followUp,
            nextAction,
            additionalField,
            "${viewModel.lat},${viewModel.long}",
            photoB64List
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


                        toast(response.data.title!!)

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

        viewModel.responseLeadContactUpdate.observe(this) { response ->
            when (response) {
                is NetworkResult.Success -> {
                    binding.progressBar.hide()
                    toast(response.data?.title!!)
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
                        binding.txtDate.text = formatDate(
                            response.data.data?.dateOfDefault.toString(),
                            "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",
                            "dd-MM-yyyy"
                        )
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

        viewModel.dispositionsIdResponse.observe(this) {
            dispositionId = it.toString()

            // adding delay to get both disposition and sub-disposition value from DB
            Handler(Looper.getMainLooper()).postDelayed(
                {
                    saveCheckingData(
                        remark,
                        followUpDate,
                        followUpDate,
                        additionalFields
                    )
                },
                1500
            )

        }

        viewModel.subDispositionsIdResponse.observe(this) {
            subDispositionId = it.toString()
        }
    }

    private fun openFileUploadDialog() {
        val items = arrayOf<CharSequence>("Camera", "Choose from gallery")
        val builder = AlertDialog.Builder(ctx!!, R.style.AlertDialogCustom)
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

    private fun createImageFile(): File? {
        // Create an image file name
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val imageFileName = "JPEG_" + timeStamp + "_"
        val storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        val image = File.createTempFile(
            imageFileName,  /* prefix */
            ".jpg",  /* suffix */
            storageDir /* directory */
        )
        // Save a file: path for use with ACTION_VIEW intents
        val currentPhotoPath = image.absolutePath
        return image
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

        cropImageLaucher =
//            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
//                if (result.resultCode == RESULT_OK) {
//                    // There are no request codes
//                    val data = result.data
//                    if (data != null) {
//                        val imageUri = data.getParcelableExtra<Uri>("sourceUri")
//                        // Uri imageUri = data.getData();
//                        if (imageUri != null) {
//                            try {
//                                val bitmap = MediaStore.Images.Media.getBitmap(
//                                    ctx.contentResolver,
//                                    imageUri
//                                )
//                                val file = FileUtils.getFile(this, imageUri)
//                                checkInBinding.txtImageVerification.setCompoundDrawablesRelativeWithIntrinsicBounds(
//                                    ContextCompat.getDrawable(this, R.drawable.ic_verified_24),
//                                    null,
//                                    null,
//                                    null
//                                )
//                                checkInBinding.verifiedImageUpload.visibility = View.VISIBLE
//                                checkInBinding.verifiedImageUpload.setImageBitmap(bitmap)
//                                checkInBinding.txtImageVerify.text = "Remove"
//                                checkInBinding.txtImageVerify.setTextColor(
//                                    ContextCompat.getColor(
//                                        this,
//                                        R.color.light_red
//                                    )
//                                )
//
//                            } catch (e: IOException) {
//                                e.printStackTrace()
//                            }
//                        }
//                    }
//                }
//            }
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == RESULT_OK) {
                    // There are no request codes
                    val data = result.data
                    if (data != null) {
                        val imageUri = data.getParcelableExtra<Uri>("sourceUri")
                        // Uri imageUri = data.getData();
                        Log.i("CROP_ACTIVITY", " CROP_ACTIVITY bitmap ==> $imageUri")
                        if (imageUri != null) {
                            try {
                                val bitmap = MediaStore.Images.Media.getBitmap(
                                    ctx!!.contentResolver,
                                    imageUri
                                )
                                val file: File? = FileUtils.getFile(ctx, imageUri)
                                Log.i(
                                    "CROP_ACTIVITY",
                                    " CROP_ACTIVITY bitmap ==> $imageUri Bitmap  $bitmap"
                                )

                                addedPhotosList.add(imageUri)
                                Log.i(javaClass.name, "addedphotosList---" + addedPhotosList.size)

                                var newArraylist = ArrayList<Uri>();
                                newArraylist.addAll(addedPhotosList)


                                addImageAdapter.submitList(addedPhotosList)


                                //Convert Base64
                                var filepathString: String? = null
                                val filePath: File
                                try {
                                    filepathString = getFileNameByUri(ctx, imageUri)
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                    Toast.makeText(
                                        ctx,
                                        "Error loading file",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                                if (filepathString != null) {
                                    filePath = File(filepathString)
                                    try {
                                        val fileString: String = getStringFile(filePath)!!
                                        photoB64List.add("data:image/jpeg;base64,$fileString")

                                        Log.d(
                                            "dvsdvsdvsv",
                                            "getResultFromActivity: " + Gson().toJson(photoB64List)
                                        )
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
        // Log.i(TAG,"startCropActivity  -- > "+uri.toString());
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
                            requestNewLocationData()
                        } else {
                            Log.i(javaClass.name, "lat---->" + location.latitude + "")
                            Log.i(javaClass.name, "getLongitude---->" + location.longitude + "")

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
//            viewModel.setLocation(
//                locationRequest.lastLocation.latitude,
//                locationRequest.lastLocation.longitude
//            )
            Log.i(
                javaClass.name,
                "locationRequest----latitude---" + locationRequest.lastLocation.latitude
            )
            Log.i(
                javaClass.name,
                "locationRequest----longitude---" + locationRequest.lastLocation.longitude
            )


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

//    override fun openSubStatusBottomSheet() {
//        val openSubStatus =
//            SubStatusBottomSheet.newInstance(object : SubStatusActionBottomSheet.OnClick {
//                override fun onClick(
//                    dispositionType: String,
//                    followUpDate: String,
//                    remark: String,
//                    assignTracker: Boolean,
//                    customNotFoundReason: String
//                ) {
//                    prepareDataForCheckIn(
//                        dispositionType,
//                        followUpDate,
//                        remark,
//                        assignTracker,
//                        customNotFoundReason
//                    )
//                }
//            })
//        openSubStatus.show(
//            supportFragmentManager,
//            SubStatusBottomSheet.TAG
//        )
//    }

    override fun openSubStatusActionBottomSheet(isPTPAction: Boolean, dispositionType: String) {

        if (viewModel.lat.isNullOrEmpty() || viewModel.long.isNullOrEmpty() ||
            viewModel.lat.isNullOrBlank() || viewModel.long.isNullOrBlank()
        ) {
            binding.root.snackBar("Please verify location")
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
                        additionalFields: String
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

    override fun openPaymentScreen() {
        val intent =
            Intent(this, PaymentCollectionActivity::class.java)
        intent.putExtra("loan_account_number", viewModel.leadId)
        startActivity(intent)
    }

    override fun removePhoto(uri: Uri) {
        Log.i(javaClass.name, "removePhoto----->" + uri.path)
        Log.i(javaClass.name, "addedphotosList----->" + addedPhotosList.size)

        photoB64List.removeAt(addedPhotosList.indexOf(uri))
        addedPhotosList.remove(uri)
        Log.i(javaClass.name, "addedphotosList--afterremove--->" + addedPhotosList.size)
        addImageAdapter.submitList(addedPhotosList)
    }

    override fun addPhoto() {
        openFileUploadDialog()
    }

    private fun formatDate(dateString: String, format: String, newFormat: String): String {
        var date: String = dateString
        var spf = SimpleDateFormat(format, Locale.getDefault())
        spf.timeZone = TimeZone.getTimeZone("UTC")
        var newDate: Date? = null
        try {
            newDate = spf.parse(date)
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        spf = SimpleDateFormat(newFormat, Locale.getDefault())
        date = spf.format(newDate)
        return date
    }

    private fun getFileNameByUri(context: Context, uri: Uri): String? {
        var filepath: String? = ""
        if (uri.scheme.toString().compareTo("content") == 0) {
            val contentResolver = context.contentResolver ?: return null
            // Create file path inside app's data dir
            val filePath = (context.applicationInfo.dataDir + File.separator
                    + System.currentTimeMillis())
            val file = File(filePath)
            try {
                val inputStream = contentResolver.openInputStream(uri) ?: return null
                val outputStream: OutputStream = FileOutputStream(file)
                val buf = ByteArray(1024)
                var len: Int
                while (inputStream.read(buf).also { len = it } > 0) outputStream.write(
                    buf,
                    0,
                    len
                )
                outputStream.close()
                inputStream.close()
            } catch (ignore: IOException) {
                return null
            }
            filepath = file.absolutePath
        } else if (uri.scheme!!.compareTo("file") == 0) {
            try {
                val file = File(URI(uri.toString()))
                if (file.exists()) filepath = file.absolutePath
            } catch (e: URISyntaxException) {
                e.printStackTrace()
            }
        } else {
            filepath = uri.path
        }
        return filepath
    }

    @Throws(IOException::class)
    fun getStringFile(f: File): String? {
        var inputStream: InputStream? = null
        var encodedFile = ""
        val lastVal: String
        try {
            inputStream = FileInputStream(f.absolutePath)
            val buffer = ByteArray(10240) //specify the size to allow
            var bytesRead: Int
            val output = ByteArrayOutputStream()
            val output64 = Base64OutputStream(output, Base64.DEFAULT)
            while (inputStream.read(buffer).also { bytesRead = it } != -1) {
                output64.write(buffer, 0, bytesRead)
            }
            output64.close()
            encodedFile = output.toString()
        } catch (e1: IOException) {
            e1.printStackTrace()
        } finally {
            inputStream?.close()
        }
        lastVal = encodedFile
        return lastVal
    }

    private fun prepareDataForCheckIn(
        dispositionType: String,
        subDispositionType: String,
        followUpDate: String,
        remark: String,
        additionalFields: String
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
}







