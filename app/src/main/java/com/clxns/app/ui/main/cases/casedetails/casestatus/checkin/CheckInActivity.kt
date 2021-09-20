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
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.os.Looper
import android.provider.MediaStore
import android.provider.Settings
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
import com.clxns.app.databinding.ActivityCheckInBinding
import com.clxns.app.ui.main.cases.casedetails.casestatus.repossesions.AddImageAdapter
import com.clxns.app.R
import com.clxns.app.data.model.StatusModel
import com.clxns.app.ui.main.cases.casedetails.bottomsheets.AddMobileOrAddressBottomSheet
import com.clxns.app.ui.main.cases.casedetails.bottomsheets.SubStatusActionBottomSheet
import com.clxns.app.ui.main.cases.casedetails.bottomsheets.SubStatusBottomSheet
import com.clxns.app.utils.support.CropImageActivity
import com.clxns.app.utils.support.FileUtils
import com.clxns.app.utils.support.GridSpacingItemDecoration
import com.google.android.gms.location.*
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


@AndroidEntryPoint
class CheckInActivity : AppCompatActivity(), StatusAdapter.OnStatusListener,
    AddImageAdapter.removePhoto {

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

        setInit()
        setListeners()
    }

    private fun setListeners() {

        binding.imgBack.setOnClickListener { finish() }


        binding.txtLocationCheckIn.setOnClickListener {
            if (binding.txtLocationCheckIn.text.equals("Upload")) {
                binding.txtLocationCheckIn.text = "Check In"
                binding.txtLocationVerification.setCompoundDrawablesRelativeWithIntrinsicBounds(
                    ContextCompat.getDrawable(this, R.drawable.ic_verified_24),
                    null,
                    null,
                    null
                )
                Snackbar.make(
                    binding.root,
                    "Location has been verified",
                    Snackbar.LENGTH_LONG
                ).show()
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

    private fun openFileUploadDialog() {
        if (ActivityCompat.checkSelfPermission(
                ctx!!,
                Manifest.permission.CAMERA
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                (ctx as Activity?)!!,
                arrayOf(
                    Manifest.permission.CAMERA,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                ),
                CAMERA_REQUEST_CODE
            )
        } else {
            val items = arrayOf<CharSequence>("Camera", "Choose from gallery")
            val builder = AlertDialog.Builder(ctx!!, R.style.AlertDialogCustom)
            builder.setTitle("Add File ")
            builder.setItems(items) { dialog: DialogInterface?, item: Int ->
                when (items[item].toString()) {
                    "Camera" ->
                        if (ActivityCompat.checkSelfPermission(
                                ctx!!,
                                Manifest.permission.CAMERA
                            ) != PackageManager.PERMISSION_GRANTED
                        ) {
                            ActivityCompat.requestPermissions(
                                (ctx as Activity?)!!,
                                arrayOf(
                                    Manifest.permission.CAMERA,
                                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                                ), CAMERA_REQUEST_CODE
                            )
                        } else {
                            openCamera()
                        }
                    "Choose from gallery" ->                         //ACTION_GET_CONTENT

                        if (ActivityCompat.checkSelfPermission(
                                ctx!!,
                                Manifest.permission.WRITE_EXTERNAL_STORAGE
                            ) != PackageManager.PERMISSION_GRANTED
                        ) {
                            ActivityCompat.requestPermissions(
                                (ctx as Activity?)!!,
                                arrayOf(
                                    Manifest.permission.CAMERA,
                                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                    Manifest.permission.READ_EXTERNAL_STORAGE
                                ), GALLERY_REQUEST_CODE
                            )
                        } else {
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
                }
            }
            builder.show()
        }
    }

    private fun openCamera() {
        when {
            ActivityCompat.checkSelfPermission(
                ctx!!,
                Manifest.permission.CAMERA
            ) != PackageManager.PERMISSION_GRANTED -> {
                ActivityCompat.requestPermissions(
                    (ctx as AppCompatActivity?)!!,
                    arrayOf(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE),
                    CAMERA_REQUEST_CODE
                )
            }
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.N -> {
                intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                imageUri = FileProvider.getUriForFile(
                    ctx!!, ctx!!.applicationContext.packageName
                            + ".provider",
                    createImageFile()!!
                )
                intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri)
                imageCameraPickerLauncher!!.launch(intent)
            }
            else -> {
                intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                imageUri = Uri.fromFile(createImageFile())
                intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri)
                imageCameraPickerLauncher!!.launch(intent)
            }
        }
    }

    private fun createImageFile(): File? {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val imageFileName = "JPEG_" + timeStamp + "_"
        val storageDir = File(
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM), "Camera"
        )
        var image: File? = null

        Log.i(javaClass.name, "storageDir---->$storageDir")
        try {
            image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",  /* suffix */
                storageDir /* directory */
            )
            mCurrentPhotoPath = "file:" + image.absolutePath
        } catch (e: IOException) {
            e.printStackTrace()
        }

        // Save a file: path for use with ACTION_VIEW intents
        Log.i(javaClass.name, "createImage file log  $image")
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

                            viewModel.getLatLong(location.latitude, location.longitude)
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
        val openAddDetailsBS = AddMobileOrAddressBottomSheet().newInstance()
        val bundle = Bundle()
        bundle.putBoolean("isMobile", isMobile)
        openAddDetailsBS.arguments = bundle
        openAddDetailsBS.show(
            supportFragmentManager,
            AddMobileOrAddressBottomSheet.TAG
        )
    }

    override fun openSubStatusBottomSheet() {
        val openSubStatus = SubStatusBottomSheet().newInstance()
        openSubStatus.show(
            supportFragmentManager,
            SubStatusBottomSheet.TAG
        )
    }

    override fun openSubStatusActionBottomSheet(isPTPAction: Boolean) {
        val openSubStatusAction = SubStatusActionBottomSheet().newInstance()
        val bundle = Bundle()
        bundle.putBoolean("isPTPAction", isPTPAction)
        openSubStatusAction.arguments = bundle
        openSubStatusAction.show(supportFragmentManager, SubStatusActionBottomSheet.TAG)
    }


    override fun removePhoto(uri: Uri) {
        Log.i(javaClass.name, "removePhoto----->" + uri.path)
        Log.i(javaClass.name, "addedphotosList----->" + addedPhotosList.size)

        addedPhotosList.remove(uri)
        Log.i(javaClass.name, "addedphotosList--afterremove--->" + addedPhotosList.size)
        addImageAdapter.submitList(addedPhotosList)
    }

    override fun addPhoto() {
        openFileUploadDialog()
    }
}







