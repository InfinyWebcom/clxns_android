package com.clxns.app.utils.support

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Bitmap.CompressFormat
import android.graphics.RectF
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.clxns.app.R
import com.clxns.app.databinding.ActivityCropImageBinding
import com.isseiaoki.simplecropview.CropImageView
import com.isseiaoki.simplecropview.callback.CropCallback
import com.isseiaoki.simplecropview.callback.LoadCallback
import com.isseiaoki.simplecropview.callback.SaveCallback
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.RejectedExecutionException

class CropImageActivity : AppCompatActivity() {

    private var path: String? = null
    var activityCropImageBinding: ActivityCropImageBinding? = null
    var file: File? = null
    private var mFrameRect: RectF? = null
    var context: Context? = null
    var mSourceUri: Uri? = null

    private val mCompressFormat = CompressFormat.JPEG

    companion object{
        var TAG = "cropImageActivityLog"
        private var KEY_FRAME_RECT = "FrameRect"
        private val CROP_ACTIVITY = 15
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_crop_image)
        activityCropImageBinding = ActivityCropImageBinding.inflate(layoutInflater)

        val view: View = activityCropImageBinding!!.root

        setContentView(view)

        context = this
        if (savedInstanceState != null) {
            // restore data
            mFrameRect = savedInstanceState.getParcelable(KEY_FRAME_RECT)
        }

        val intent = intent
        mSourceUri = Uri.parse(intent.getStringExtra("sourceUri"))
        val cropping = intent.getStringExtra("cropping")?.equals("disable")
        if (!cropping!!) {
            activityCropImageBinding!!.cropImageView.setCropMode(CropImageView.CropMode.CIRCLE)
        }
        val fromDoc = intent.getBooleanExtra("from_documents", false)
        if (fromDoc) {
            Log.i(TAG, "======CUSTOM==>")
            activityCropImageBinding!!.cropImageView.setCropMode(CropImageView.CropMode.FREE)
        } /*else{
            Log.i(TAG, "=====SQUARE=====>" );
            activityCropImageBinding.cropImageView.setCropMode(CropImageView.CropMode.SQUARE);
        }*/

        Log.i(TAG, "==mSourceUri==>$mSourceUri")
        //activityCropImageBinding.cropImageView.setCropMode();


        //activityCropImageBinding.cropImageView.setCropMode();
        activityCropImageBinding!!.cropImageView.load(mSourceUri)
            .initialFrameRect(mFrameRect)
            .useThumbnail(true)
            .execute(mLoadCallback)

        activityCropImageBinding!!.rotateRight.setOnClickListener {
            activityCropImageBinding!!.cropImageView.rotateImage(CropImageView.RotateDegrees.ROTATE_90D) // rotate clockwise by 90 degrees
        }

        activityCropImageBinding!!.rotateLeft.setOnClickListener {
            activityCropImageBinding!!.cropImageView.rotateImage(CropImageView.RotateDegrees.ROTATE_M90D) // rotate counter-clockwise by 90 degrees
        }

        activityCropImageBinding!!.buttonDone.setOnClickListener { //progressBar.setVisibility(View.VISIBLE);
            activityCropImageBinding!!.cropImageView.crop(mSourceUri).execute(mCropCallback)
        }

        activityCropImageBinding!!.buttonCencel.setOnClickListener {
            val intent = Intent()
            setResult(2, intent)
            finish()
        }

    }

    private val mLoadCallback: LoadCallback = object : LoadCallback {
        override fun onSuccess() {
            activityCropImageBinding!!.imgProg.visibility = View.GONE
        }

        override fun onError(e: Throwable) {
            Toast.makeText(applicationContext, "error", Toast.LENGTH_SHORT).show()
        }
    }
    private val mCropCallback: CropCallback = object : CropCallback {
        override fun onSuccess(cropped: Bitmap) {
            try {
                activityCropImageBinding!!.cropImageView.save(cropped)
                    .compressFormat(mCompressFormat)
                    .execute(createSaveUri(), mSaveCallback)
            } catch (e: RejectedExecutionException) {
            }
        }

        override fun onError(e: Throwable) {}
    }

    fun createSaveUri(): Uri? {
        return createNewUri(context!!, mCompressFormat)
    }

    private fun createNewUri(context: Context, format: CompressFormat?): Uri? {
        val currentTimeMillis = System.currentTimeMillis()
        val today = Date(currentTimeMillis)
        val dateFormat = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault())
        val title = dateFormat.format(today)
        val dirPath = getDirPath()
        val fileName = "scv" + title + "." + getMimeType(format)
        path = "$dirPath/$fileName"
        val file = File(path!!)
        val values = ContentValues()
        values.put(MediaStore.Images.Media.TITLE, title)
        values.put(MediaStore.Images.Media.DISPLAY_NAME, fileName)
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/" + getMimeType(format))
        values.put(MediaStore.Images.Media.DATA, path)
        val time = currentTimeMillis / 1000
        values.put(MediaStore.MediaColumns.DATE_ADDED, time)
        values.put(MediaStore.MediaColumns.DATE_MODIFIED, time)
        if (file.exists()) {
            values.put(MediaStore.Images.Media.SIZE, file.length())
        }
        val resolver = context.contentResolver
        //        Logger.i("SaveUri = " + uri);
        return resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
    }

    private fun getDirPath(): String {
        var dirPath = ""
        var imageDir: File? = null
        val extStorageDir = Environment.getExternalStorageDirectory()
        if (extStorageDir.canWrite()) {
            imageDir = File(extStorageDir.path + "/simplecropview")
        }
        if (imageDir != null) {
            if (!imageDir.exists()) {
                imageDir.mkdirs()
            }
            if (imageDir.canWrite()) {
                dirPath = imageDir.path
            }
        }
        return dirPath
    }

    private fun getMimeType(format: CompressFormat?): String {
//        Logger.i("getMimeType CompressFormat = " + format);
        return when (format) {
            CompressFormat.JPEG -> "jpeg"
            CompressFormat.PNG -> "png"
            else -> "png"
        }
    }

    private val mSaveCallback: SaveCallback = object : SaveCallback {
        override fun onSuccess(outputUri: Uri) {
            val intent = Intent()
            Log.i(TAG, "sourceUri" + outputUri.toString() + "path_---------" + path)
            intent.putExtra("sourceUri", outputUri)
            intent.putExtra("path", path)
            setResult(RESULT_OK, intent)
            finish()
        }

        override fun onError(e: Throwable) {
            Toast.makeText(this@CropImageActivity, "error", Toast.LENGTH_SHORT).show()
        }
    }

}

