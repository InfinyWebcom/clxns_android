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
import android.widget.ArrayAdapter
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.FileProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.clxns.app.R
import com.clxns.app.databinding.ActivityAssetRepossesionBinding
import com.clxns.app.ui.main.cases.casedetails.casestatus.repossesions.map.RepossessionMapActivity
import com.clxns.app.utils.support.CropImageActivity
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class AssetRepossessionActivity : AppCompatActivity(), AddImageAdapter.removePhoto {

    private lateinit var blankUri : Uri

    companion object {
        private const val REQUEST_IMAGE_CAPTURE = 854
    }

    lateinit var activityAssetRepossessionBinding : ActivityAssetRepossesionBinding
    lateinit var ctx : Context
    lateinit var datePickerDialog : DatePickerDialog
    lateinit var addAssetImageAdapter : AddImageAdapter
    lateinit var nearestAddressAdapter : NearestAddressAdapter

    private var imageUri : Uri? = null
    private var mCurrentPhotoPath : String? = null
    var addedPhotosList : ArrayList<Uri> = ArrayList()
    var cropImageLauncher : ActivityResultLauncher<Intent>? = null

    var year = 0
    var month = 0
    var day = 0
    var imageCameraPickerLauncher : ActivityResultLauncher<Intent>? = null
    var imageGalleryPickerLauncher : ActivityResultLauncher<Intent>? = null

    override fun onCreate(savedInstanceState : Bundle?) {
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

        activityAssetRepossessionBinding.imgBack.setOnClickListener {
            onBackPressed()
        }

        activityAssetRepossessionBinding.repoGoToDropAreaBtn.setOnClickListener {
            startActivity(Intent(this, RepossessionMapActivity::class.java))
        }

    }

    private fun openFileUploadDialog() {
        if (ActivityCompat.checkSelfPermission(
                ctx,
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
            val builder = AlertDialog.Builder(ctx)
            builder.setTitle("Add File ")
            builder.setItems(items) { _ : DialogInterface?, item : Int ->
                when (items[item].toString()) {
                    "Camera" ->
                        if (ActivityCompat.checkSelfPermission(
                                ctx,
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
                                ctx,
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
            ActivityResultContracts.StartActivityForResult()
        ) {
            if (it.resultCode == RESULT_OK) {
                // There are no request codes
                imageUri?.let {
                    startCropActivity(imageUri!!)
                }

            }
        }

        imageGalleryPickerLauncher =
            registerForActivityResult(
                ActivityResultContracts.StartActivityForResult()
            ) {
                if (it?.resultCode == RESULT_OK) {
                    val data = it.data
                    data?.data?.let { startCropActivity(data.data!!) }
                }
            }





        cropImageLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == RESULT_OK) {
                    // There are no request codes
                    val data = result.data
                    if (data != null) {
                        val imageUri = data.getParcelableExtra<Uri>("sourceuri")
                        // Uri imageUri = data.getData();
                        if (imageUri != null) {
                            try {

                                addedPhotosList.add(addedPhotosList.size - 1, imageUri)

                                val newArraylist = ArrayList<Uri>()
                                newArraylist.addAll(addedPhotosList)

                                addAssetImageAdapter.submitList(addedPhotosList)

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
        intent.putExtra("sourceUri", uri.toString())
        intent.putExtra("cropping", "disable")
        cropImageLauncher!!.launch(intent)
    }

    private fun openCamera() {
        when {
            ActivityCompat.checkSelfPermission(
                ctx,
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
                    ctx, ctx.applicationContext.packageName
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

    private fun createImageFile() : File? {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val imageFileName = "JPEG_" + timeStamp + "_"
        val storageDir = File(
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM), "Camera"
        )
        var image : File? = null

        try {
            image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",  /* suffix */
                storageDir /* directory */
            )
            mCurrentPhotoPath = "file:" + image.absolutePath
        } catch (e : IOException) {
            e.printStackTrace()
        }

        // Save a file: path for use with ACTION_VIEW intents
        return image
    }

    private fun setInit() {
        ctx = this
        activityAssetRepossessionBinding = ActivityAssetRepossesionBinding.inflate(layoutInflater)
        setContentView(activityAssetRepossessionBinding.root)

        if (addedPhotosList.size == 0) {
            blankUri = Uri.parse("blank_uri")
            addedPhotosList.add(blankUri)
        }

        val assetTypeArrayAdapter = ArrayAdapter.createFromResource(
            ctx,
            R.array.assetType_sample,
            android.R.layout.simple_spinner_item
        )
        assetTypeArrayAdapter.setDropDownViewResource(android.R.layout.select_dialog_singlechoice)
        activityAssetRepossessionBinding.spAssetType.adapter = assetTypeArrayAdapter

        addAssetImageAdapter = AddImageAdapter(this, ctx)
        activityAssetRepossessionBinding.recyclerAddImage.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)

        activityAssetRepossessionBinding.recyclerSlipImage.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        activityAssetRepossessionBinding.recyclerAddImage.adapter = addAssetImageAdapter
        activityAssetRepossessionBinding.recyclerSlipImage.adapter = addAssetImageAdapter

        nearestAddressAdapter = NearestAddressAdapter()
        activityAssetRepossessionBinding.nearestAddressRecyclerView.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        activityAssetRepossessionBinding.nearestAddressRecyclerView.adapter = nearestAddressAdapter

        addAssetImageAdapter.submitList(addedPhotosList)
    }

    override fun removePhoto(uri : Uri) {
        addedPhotosList.remove(uri)
        addAssetImageAdapter.submitList(addedPhotosList)
    }

    override fun addPhoto() {
        openFileUploadDialog()
    }
}