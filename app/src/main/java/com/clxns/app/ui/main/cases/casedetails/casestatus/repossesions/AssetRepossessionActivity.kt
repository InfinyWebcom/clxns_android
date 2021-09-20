package com.clxns.app.ui.main.cases.casedetails.casestatus.repossesions

import android.Manifest
import android.app.Activity
import android.app.DatePickerDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.widget.ArrayAdapter
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.FileProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.clxns.app.R
import com.clxns.app.databinding.ActivityAssetRepossesionBinding
import com.clxns.app.ui.main.cases.casedetails.casestatus.repossesions.map.RepossesionMapActivity
import com.clxns.app.utils.support.CropImageActivity
import com.clxns.app.utils.support.FileUtils
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class AssetRepossessionActivity : AppCompatActivity(), AddImageAdapter.removePhoto {

    private lateinit var blankUri: Uri
    private val MONTHS = arrayOf(
        "January",
        "February",
        "March",
        "April",
        "May",
        "June",
        "July",
        "August",
        "September",
        "October",
        "November",
        "December"
    )
    lateinit var activityAssetRepossesionBinding: ActivityAssetRepossesionBinding
    lateinit var ctx: Context
    lateinit var datePickerDialog: DatePickerDialog
    lateinit var addAssetImageAdapter: AddImageAdapter
    lateinit var nearestAddressAdapter: NearestAddressAdapter
    private val REQUEST_IMAGE_CAPTURE = 854
    private var imageUri: Uri? = null
    private var mCurrentPhotoPath: String? = null
    var addedPhotosList: ArrayList<Uri> = ArrayList()
    var cropImageLaucher: ActivityResultLauncher<Intent>? = null

    var year = 0
    var month = 0
    var day = 0
    var imageCameraPickerLauncher: ActivityResultLauncher<Intent>? = null
    var imageGalleryPickerLauncher: ActivityResultLauncher<Intent>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setInit()
        setListeners()
        getResultFromActivity()

    }

    private fun setListeners() {

        val calendar = Calendar.getInstance()
        year = calendar.get(Calendar.YEAR)
        day = calendar.get(Calendar.DAY_OF_MONTH)
        month = calendar.get(Calendar.MONTH)


        activityAssetRepossesionBinding.txtDate.setOnClickListener {
            /*datePickerDialog =
                DatePickerDialog(ctx, DatePickerDialog.OnDateSetListener { view, year, month, day ->

                    Log.i(javaClass.name, "month--->" + MONTHS[month])
                    activityAssetRepossesionBinding.txtDate.text =
                        " $day " + MONTHS[month] + " year"
                }, year, month, day)

            datePickerDialog.show()*/
        }



        activityAssetRepossesionBinding.imgBack.setOnClickListener {
            onBackPressed()
        }

        activityAssetRepossesionBinding.repoGoToDropAreaBtn.setOnClickListener {
            startActivity(Intent(this, RepossesionMapActivity::class.java))
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
                REQUEST_IMAGE_CAPTURE
            )
        } else {
            val items = arrayOf<CharSequence>("Camera", "Choose from gallery")
            val builder = AlertDialog.Builder(ctx!!, R.style.AlertDialogCustom)
            builder.setTitle("Add File ")
            builder.setItems(items) { _: DialogInterface?, item: Int ->
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
                                ), REQUEST_IMAGE_CAPTURE
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
                                ), REQUEST_IMAGE_CAPTURE
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

    private fun getResultFromActivity() {
        imageCameraPickerLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult(),
            object : ActivityResultCallback<ActivityResult> {
                override fun onActivityResult(result: ActivityResult) {
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
                override fun onActivityResult(result: ActivityResult?) {
                    if (result?.resultCode == RESULT_OK) {
                        val data = result.data
                        Log.i(javaClass.name, "data-->$data")
                        data?.data?.let { startCropActivity(data.data!!) }
                    }
                }
            })





        cropImageLaucher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == RESULT_OK) {
                    // There are no request codes
                    val data = result.data
                    if (data != null) {
                        val imageUri = data.getParcelableExtra<Uri>("sourceuri")
                        // Uri imageUri = data.getData();
                        Log.i("CROP_ACTIVITY", " CROP_ACTIVITY bitmap ==> $imageUri")
                        if (imageUri != null) {
                            try {
                                val bitmap = MediaStore.Images.Media.getBitmap(
                                    ctx!!.contentResolver,
                                    imageUri
                                )
                                val file: File? = FileUtils.getFile(ctx!!, imageUri)
                                Log.i(
                                    "CROP_ACTIVITY",
                                    " CROP_ACTIVITY bitmap ==> $imageUri Bitmap  $bitmap"
                                )

                                Log.i(
                                    javaClass.name,
                                    "addedphotosList--->" + addedPhotosList.contains(blankUri)
                                )
                                /*if(addedphotosList.contains(blankUri)){
                                    addedphotosList.clear()
                                }*/

                                addedPhotosList.add(addedPhotosList.size - 1, imageUri)
                                Log.i(javaClass.name, "addedphotosList---" + addedPhotosList.size)

                                var newArraylist = ArrayList<Uri>();
                                newArraylist.addAll(addedPhotosList)


                                addAssetImageAdapter.submitList(addedPhotosList)

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
        //overridePendingTransition(R.anim.right_in,R.anim.left_out);
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
                    REQUEST_IMAGE_CAPTURE
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

    private fun setInit() {
        ctx = this
        activityAssetRepossesionBinding = ActivityAssetRepossesionBinding.inflate(layoutInflater)
        setContentView(activityAssetRepossesionBinding.root)

        if (addedPhotosList.size == 0) {
            blankUri = Uri.parse("blank_uri")
            addedPhotosList.add(blankUri)
        }

        var assetTypeArrayAdapter = ArrayAdapter.createFromResource(
            ctx,
            R.array.assetType_sample,
            android.R.layout.simple_spinner_item
        )
        assetTypeArrayAdapter.setDropDownViewResource(android.R.layout.select_dialog_singlechoice)
        activityAssetRepossesionBinding.spAssetType.adapter = assetTypeArrayAdapter

        addAssetImageAdapter = AddImageAdapter(this, ctx)
        activityAssetRepossesionBinding.recyclerAddImage.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)

        activityAssetRepossesionBinding.recyclerSlipImage.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        activityAssetRepossesionBinding.recyclerAddImage.adapter = addAssetImageAdapter
        activityAssetRepossesionBinding.recyclerSlipImage.adapter = addAssetImageAdapter

        nearestAddressAdapter = NearestAddressAdapter()
        activityAssetRepossesionBinding.nearestAddressRecyclerView.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        activityAssetRepossesionBinding.nearestAddressRecyclerView.adapter = nearestAddressAdapter

        addAssetImageAdapter.submitList(addedPhotosList)
    }

    override fun removePhoto(uri: Uri) {

        Log.i(javaClass.name, "removePhoto----->" + uri.path)
        Log.i(javaClass.name, "addedphotosList----->" + addedPhotosList.size)

        addedPhotosList.remove(uri)
        Log.i(javaClass.name, "addedphotosList--afterremove--->" + addedPhotosList.size)
        addAssetImageAdapter.submitList(addedPhotosList)


    }

    override fun addPhoto() {
        openFileUploadDialog()
    }
}