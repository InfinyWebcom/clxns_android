package com.clxns.app.ui.main.cases.casedetails.casestatus.repossesions

import android.Manifest
import android.app.Activity
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
import com.clxns.app.databinding.ActivityRepossessionsSubmitBinding
import com.clxns.app.utils.support.CropImageActivity
import com.clxns.app.utils.support.FileUtils
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class RepossessionsSubmitActivity : AppCompatActivity(), AddImageAdapter.removePhoto {

    lateinit var activityRepossessionsSubmitBinding: ActivityRepossessionsSubmitBinding
    lateinit var ctx: Context
    private lateinit var blankUri: Uri
    lateinit var addImageAdapter: AddImageAdapter
    private val REQUEST_IMAGE_CAPTURE = 854
    private var imageUri: Uri? = null
    private var mCurrentPhotoPath: String? = null
    var addedphotosList: ArrayList<Uri> = ArrayList()
    var cropImageLaucher: ActivityResultLauncher<Intent>? = null
    var imageCameraPickerLaucher: ActivityResultLauncher<Intent>? = null
    var imageGalleryPickerLaucher: ActivityResultLauncher<Intent>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initUI()
        initClickListener()
        getResultFromActivity()

    }

    private fun initUI() {
        ctx = this
        activityRepossessionsSubmitBinding =
            ActivityRepossessionsSubmitBinding.inflate(layoutInflater)
        setContentView(activityRepossessionsSubmitBinding.root)

        addImageAdapter = AddImageAdapter(this, ctx)
        activityRepossessionsSubmitBinding.recyclerAssetImage.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        activityRepossessionsSubmitBinding.recyclerSlip.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        activityRepossessionsSubmitBinding.recyclerAssetImage.adapter = addImageAdapter
        activityRepossessionsSubmitBinding.recyclerSlip.adapter = addImageAdapter

        if (addedphotosList.size == 0) {
            blankUri = Uri.parse("blank_uri")
            addedphotosList.add(blankUri)
        }
        addImageAdapter.submitList(addedphotosList)
    }

    private fun initClickListener() {
        activityRepossessionsSubmitBinding.imgBack.setOnClickListener {
            onBackPressed()
        }
    }

    override fun removePhoto(uri: Uri) {
        Log.i(javaClass.name, "removePhoto----->" + uri.path)
        Log.i(javaClass.name, "addedphotosList----->" + addedphotosList.size)

        addedphotosList.remove(uri)
        Log.i(javaClass.name, "addedphotosList--afterremove--->" + addedphotosList.size)
        addImageAdapter.submitList(addedphotosList)
    }

    override fun addPhoto() {

        openFileUploadDailog()

    }

    private fun openFileUploadDailog() {
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
            val builder = AlertDialog.Builder(ctx)
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
                            Log.i(
                                javaClass.name,
                                "Choose from gallery--->" + imageGalleryPickerLaucher
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
                            Log.i(
                                javaClass.name,
                                "imagePickerLaucher--->" + imageGalleryPickerLaucher
                            )
                            imageGalleryPickerLaucher!!.launch(intent)
                            //SELECT_PICTURES is simply a global int used to check the calling intent in onActivityResult
                        }
                }
            }
            builder.show()
        }
    }

    private fun getResultFromActivity() {
        imageCameraPickerLaucher = registerForActivityResult(
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

        imageGalleryPickerLaucher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult(), object :
                ActivityResultCallback<ActivityResult> {
                override fun onActivityResult(result: ActivityResult?) {
                    if (result?.resultCode == RESULT_OK) {
                        val data = result.data
                        Log.i(javaClass.name, "data-->" + data)

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
                                    "addedphotosList--->" + addedphotosList.contains(blankUri)
                                )
                                /*if(addedphotosList.contains(blankUri)){
                                    addedphotosList.clear()
                                }*/

                                addedphotosList.add(addedphotosList.size - 1, imageUri)
                                Log.i(javaClass.name, "addedphotosList---" + addedphotosList.size)

                                var newArraylist = ArrayList<Uri>();
                                newArraylist.addAll(addedphotosList)


                                addImageAdapter.submitList(addedphotosList)

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
        intent.putExtra("croping", "disable")
        cropImageLaucher!!.launch(intent)
        //overridePendingTransition(R.anim.right_in,R.anim.left_out);
    }

    private fun openCamera() {
        Log.i(
            javaClass.name,
            "openCamera--->" + (ActivityCompat.checkSelfPermission(
                ctx!!,
                Manifest.permission.CAMERA
            ) != PackageManager.PERMISSION_GRANTED)
        )


        if (ActivityCompat.checkSelfPermission(
                ctx!!,
                Manifest.permission.CAMERA
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                (ctx as AppCompatActivity?)!!,
                arrayOf(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE),
                REQUEST_IMAGE_CAPTURE
            )
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            imageUri = FileProvider.getUriForFile(
                ctx!!, ctx!!.applicationContext.packageName
                        + ".provider",
                createImageFile()!!
            )
            Log.i(javaClass.name, "imageURI :$imageUri")
            intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri)
            imageCameraPickerLaucher!!.launch(intent)
        } else {
            Log.i(javaClass.name, "Else----------------->")
            intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            imageUri = Uri.fromFile(createImageFile())
            intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri)
            imageCameraPickerLaucher!!.launch(intent)
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
}