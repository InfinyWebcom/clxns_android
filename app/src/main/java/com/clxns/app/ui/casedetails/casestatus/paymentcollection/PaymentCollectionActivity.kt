package com.clxns.app.ui.casedetails.casestatus.paymentcollection

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
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.LinearLayout
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
import androidx.lifecycle.ViewModelProvider
import com.clxns.app.R
import com.clxns.app.data.repository.PaymentCollectionRepository
import com.clxns.app.databinding.ActivityPaymentCollectionBinding
import com.clxns.app.ui.MainActivity
import com.clxns.app.ui.casedetails.casestatus.repossesions.AddImageAdapter
import com.clxns.app.utils.support.CropImageActivity
import com.clxns.app.utils.support.FileUtils
import dagger.hilt.android.AndroidEntryPoint
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

@AndroidEntryPoint
class PaymentCollectionActivity : AppCompatActivity(), AddImageAdapter.removePhoto {

    lateinit var paymentCollectionBinding: ActivityPaymentCollectionBinding
    lateinit var ctx: Context
    val paymentCollectionViewModel: PaymentCollectionViewModel by viewModels()
    private val REQUEST_IMAGE_CAPTURE = 854
    private var imageUri: Uri? = null
    private var mCurrentPhotoPath: String? = null
    private var addedPhotosList: ArrayList<Uri> = ArrayList()
    private var cropImageLauncher: ActivityResultLauncher<Intent>? = null
    lateinit var addImageAdapter: AddImageAdapter

    private var year = 0
    private var month = 0
    private var day = 0
    var imageCameraPickerLauncher: ActivityResultLauncher<Intent>? = null
    var imageGalleryPickerLauncher: ActivityResultLauncher<Intent>? = null

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
    }

    private lateinit var datePickerDialog: DatePickerDialog


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.statusBarColor = ContextCompat.getColor(this, R.color.colorPrimary)
        setInit()
        setListeners()
        getResultFromActivity()
    }

    private fun setListeners() {

        paymentCollectionBinding.rgPayment.setOnCheckedChangeListener { _, id ->
            when (id) {
                R.id.rbCheque -> setChequeVisibility()
                R.id.rbOnline -> setOnlineVisibility()
                R.id.rbCash -> setCashVisibility()
            }
        }

        paymentCollectionBinding.txtPaymentOrRecoveryDateValue.setOnClickListener {
            showDatePickerDialog()
        }

        paymentCollectionBinding.imgBack.setOnClickListener { finish() }

        paymentCollectionBinding.txtUploadScreenShot.setOnClickListener {
            openFileUploadDialog()
        }

        paymentCollectionBinding.generateReceiptBtn.setOnClickListener {
            if (paymentCollectionBinding.generateReceiptBtn.text.equals("back to my plan")) {
                val finishAllActivitiesExceptMain = Intent(this, MainActivity::class.java)
                finishAllActivitiesExceptMain.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                startActivity(finishAllActivitiesExceptMain)
            } else {
                val l: LinearLayout = findViewById(R.id.mainLinearLayout)
                l.forEachChildView {
                    it.isEnabled = false
                }
                Toast.makeText(this, "Receipt has been sent to the client", Toast.LENGTH_LONG)
                    .show()
                paymentCollectionBinding.generateReceiptBtn.text = "back to my plan"
            }

        }


    }

    fun View.forEachChildView(closure: (View) -> Unit) {
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
                paymentCollectionBinding.txtPaymentOrRecoveryDateValue.text =
                    "$day " + MONTHS[month] + " $year"
            }, year, month, day)

        datePickerDialog.show()
    }

    private fun setCashVisibility() {
        paymentCollectionBinding.txtReferenceNo.visibility = View.GONE
        paymentCollectionBinding.edtReferenceType.visibility = View.GONE
    }

    private fun setOnlineVisibility() {
        paymentCollectionBinding.txtReferenceNo.visibility = View.VISIBLE
        paymentCollectionBinding.edtReferenceType.visibility = View.VISIBLE
        paymentCollectionBinding.txtReferenceNo.text = "Reference No."


    }

    private fun setChequeVisibility() {
        paymentCollectionBinding.txtReferenceNo.text = "Cheque No."
        paymentCollectionBinding.txtReferenceNo.visibility = View.VISIBLE
        paymentCollectionBinding.edtReferenceType.visibility = View.VISIBLE

    }

    private fun setInit() {
        ctx = this
        paymentCollectionBinding = ActivityPaymentCollectionBinding.inflate(layoutInflater)
        setContentView(paymentCollectionBinding.root)

        addImageAdapter = AddImageAdapter(this, ctx)
        paymentCollectionBinding.recyclerAddImage.adapter = addImageAdapter
        paymentCollectionBinding.recyclerAddImage.isNestedScrollingEnabled = false

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
        paymentCollectionBinding.spAmount.adapter = amountTypeAdapter

        paymentCollectionBinding.spAmount.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    if (position == 1 || position == 2) {
                        paymentCollectionBinding.paymentAmountEt.isEnabled = false
                        paymentCollectionBinding.paymentAmountEt.setText("65000")
                    } else {
                        paymentCollectionBinding.paymentAmountEt.isEnabled = true

                    }
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {

                }
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
            val builder = AlertDialog.Builder(ctx, R.style.AlertDialogCustom)
            builder.setTitle("Add File ")
            builder.setItems(items) { _: DialogInterface?, item: Int ->
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
                            Log.i(
                                javaClass.name,
                                "Choose from gallery--->$imageGalleryPickerLauncher"
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
                                "imagePickerLaucher--->$imageGalleryPickerLauncher"
                            )
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





        cropImageLauncher =
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
        cropImageLauncher!!.launch(intent)
        //overridePendingTransition(R.anim.right_in,R.anim.left_out);
    }

    private fun openCamera() {
        Log.i(
            javaClass.name,
            "openCamera--->" + (ActivityCompat.checkSelfPermission(
                ctx,
                Manifest.permission.CAMERA
            ) != PackageManager.PERMISSION_GRANTED)
        )


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
                    ctx, ctx!!.applicationContext.packageName
                            + ".provider",
                    createImageFile()!!
                )
                Log.i(javaClass.name, "imageURI :$imageUri")
                intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri)
                imageCameraPickerLauncher!!.launch(intent)
            }
            else -> {
                Log.i(javaClass.name, "Else----------------->")
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

        Log.i(javaClass.name, "storageDir---->" + storageDir)
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