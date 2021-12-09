package com.clxns.app.ui.main.account

import android.Manifest
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.clxns.app.R
import com.clxns.app.data.api.helper.NetworkResult
import com.clxns.app.data.preference.SessionManager
import com.clxns.app.databinding.FragmentAccountBinding
import com.clxns.app.ui.login.LoginActivity
import com.clxns.app.ui.main.account.changePassword.ChangePasswordActivity
import com.clxns.app.utils.*
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.nabinbhandari.android.permissions.PermissionHandler
import com.nabinbhandari.android.permissions.Permissions
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import javax.inject.Inject
import android.graphics.Bitmap
import android.graphics.BitmapFactory

import android.util.Base64
import androidx.lifecycle.lifecycleScope
import java.io.ByteArrayOutputStream
import android.net.Uri
import com.clxns.app.utils.support.CropImageActivity


@AndroidEntryPoint
class AccountFragment : Fragment() {

    private val accountViewModel : AccountViewModel by viewModels()
    private lateinit var binding : FragmentAccountBinding
    private var bankNames = arrayOf<String>()

    @Inject
    lateinit var sessionManager : SessionManager

    lateinit var imageGalleryPickerLauncher : ActivityResultLauncher<Intent>
    private lateinit var imageCropImageLauncher : ActivityResultLauncher<Intent>
    lateinit var imageCameraLauncher : ActivityResultLauncher<Intent>


    override fun onCreateView(
        inflater : LayoutInflater,
        container : ViewGroup?,
        savedInstanceState : Bundle?,
    ) : View {
        accountViewModel.getUserDetails(sessionManager.getString(Constants.TOKEN)!!)
        binding = FragmentAccountBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onCreate(savedInstanceState : Bundle?) {
        super.onCreate(savedInstanceState)
        accountViewModel.getBankNameList()
    }

    override fun onViewCreated(view : View, savedInstanceState : Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()
        subscribeObserver()
        setListeners()
        setBankImage()
        initIntentLauncherCallbacks()
    }

    private fun initIntentLauncherCallbacks() {
        imageGalleryPickerLauncher =
            registerForActivityResult(
                ActivityResultContracts.StartActivityForResult()
            ) { result ->
                if (result?.resultCode == AppCompatActivity.RESULT_OK) {
                    val uriData = result.data?.data
                    if (uriData != null) {
                        performImageCropping(uriData)
                        Timber.i("PATH -> ${uriData.path}")
                    }
                }
            }

        imageCropImageLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == AppCompatActivity.RESULT_OK) {
                    val imageUri = result.data?.getParcelableExtra<Uri>("sourceUri")
                    if (imageUri != null) {
                        val inputStream = imageUri.let {
                            requireActivity().contentResolver.openInputStream(
                                it
                            )
                        }
                        val bitmap = BitmapFactory.decodeStream(inputStream)
                        val base64 = encodeImage(bitmap)
                        inputStream?.close()
                        Timber.i("BASE 64 -> $base64")
                        binding.userNameInitials.hide()
                        if (base64 != null) {
                            sessionManager.saveAnyData(Constants.USER_IMAGE, base64)
                            lifecycleScope.launchWhenResumed {
                                loadProfileImage()
                            }
                        }
                    }
                }
            }

        imageCameraLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == AppCompatActivity.RESULT_OK) {
                    val imageAsBitmap = result.data?.extras?.get("data") as Bitmap
                    val imageAsBase64 = encodeImage(imageAsBitmap, 100)
                    binding.userNameInitials.hide()
                    if (imageAsBase64 != null) {
                        sessionManager.saveAnyData(Constants.USER_IMAGE, imageAsBase64)
                        lifecycleScope.launchWhenResumed {
                            loadProfileImage()
                        }
                    }
                }
            }
    }

    private fun loadProfileImage() {
        val imageInBase64 = sessionManager.getString(Constants.USER_IMAGE)
        Timber.i("SP BASE 64 -> $imageInBase64")
        Timber.i("Length ${imageInBase64?.length}")
        val decodedString = Base64.decode(imageInBase64, Base64.DEFAULT)
        val imageInBitmap = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.size)
        binding.userProfileImg.setImageBitmap(imageInBitmap)
    }

    private fun setListeners() {
        binding.profileMoreOptionBtn.setOnClickListener {
            it.preventDoubleClick(500)
            onMenuClick(it)
        }

        binding.uploadProfileImageFab.setOnClickListener {
            openFileUploadDialog()
        }
    }

    private fun encodeImage(bm : Bitmap, quality : Int = 10) : String? {
        val byteArray = ByteArrayOutputStream()
        bm.compress(Bitmap.CompressFormat.JPEG, quality, byteArray)
        val b = byteArray.toByteArray()
        return Base64.encodeToString(b, Base64.DEFAULT)
    }

    private fun performImageCropping(picUri : Uri) {
        val intent = Intent(requireContext(), CropImageActivity::class.java)
        intent.putExtra("sourceUri", picUri.toString())
        intent.putExtra("cropping", "enable")
        imageCropImageLauncher.launch(intent)
    }


    private fun initViews() {
        val userName = sessionManager.getString(Constants.USER_NAME)
        val managerName =
            "Reporting Manager : " + sessionManager.getString(Constants.USER_REPORTING_MANAGER)
        val userBloodGroup = "Blood Group : " + sessionManager.getString(Constants.USER_BLOOD_GROUP)
        val userEmployeeCode =
            "Employee Code : " + sessionManager.getString(Constants.USER_EMPLOYEE_ID)
        val managerContact =
            "Manager Contact : " + sessionManager.getString(Constants.USER_REPORTING_MANAGER_CONTACT)
        binding.userNameTv.text = userName
        binding.userEmployeeCodeTv.text = userEmployeeCode
        binding.userEmployeeBloodGroupTv.text = userBloodGroup
        binding.reportingManagerTv.text = managerName
        binding.managerContactTv.text = managerContact

        if (!sessionManager.getString(Constants.USER_IMAGE).isNullOrBlank()) {
            lifecycleScope.launchWhenResumed {
                loadProfileImage()
            }
        } else {
            val names = userName?.split(" ")
            var initials = ""
            if (!names.isNullOrEmpty()) {
                initials = names[0].substring(0, 1)
                initials += if (names[1].isNotEmpty()) {
                    names[1].substring(0, 1)
                } else {
                    names[0].substring(1, 2)
                }
            }
            binding.userNameInitials.show()
            binding.userNameInitials.text = initials
        }
    }

    private fun setBankImage() {
        val bankImageUrl = sessionManager.getString(Constants.SELECTED_BANK)
        if (!bankImageUrl.isNullOrBlank()) {
            binding.selectedBankImage.loadImage(bankImageUrl)
        }
    }

    private fun onMenuClick(view : View) {
        val popup = PopupMenu(requireContext(), view)
        popup.menuInflater.inflate(R.menu.account_menu, popup.menu)
        popup.setOnMenuItemClickListener { item ->

            when (item.title) {
                "Change Password" -> {
                    startActivity(Intent(requireContext(), ChangePasswordActivity::class.java))
                }
                "Logout" -> {
                    showConfirmLogoutDialog()
                }
                "Select Bank" -> {
                    val bankDialogBuilder = MaterialAlertDialogBuilder(requireContext())
                        .setTitle("Select your bank")
                        .setItems(bankNames) { _ : DialogInterface, i : Int ->
                            accountViewModel.getBankImage(bankNames[i])
                        }
                    bankDialogBuilder.show()
                }
            }

            true
        }
        popup.show()
    }

    private fun showConfirmLogoutDialog() {
        val logoutDialog = AlertDialog.Builder(requireContext())
        logoutDialog.setTitle("Confirm Logout")
        logoutDialog.setMessage("Are you sure you want to logout?")

        logoutDialog.setPositiveButton("Yes") { dialog, _ ->
            dialog.dismiss()
            accountViewModel.logout(sessionManager.getString(Constants.TOKEN)!!)
        }.setNegativeButton("Cancel") { dialog, _ -> dialog.dismiss() }

        val d = logoutDialog.create()
        d.show()
        d.getButton(AlertDialog.BUTTON_POSITIVE).isAllCaps = false
        d.getButton(AlertDialog.BUTTON_NEGATIVE).isAllCaps = false
    }

    private fun subscribeObserver() {
        accountViewModel.responseUserDetails.observe(viewLifecycleOwner) {
            when (it) {
                is NetworkResult.Success -> {
                    if (it.data?.error == false) {
                        val loginData = it.data.loginData
                        val name = loginData?.firstName + " " + loginData?.lastName
                        val managerName =
                            loginData?.reportingDetails?.firstName + " " + loginData?.reportingDetails?.lastName
                        sessionManager.saveAnyData(
                            Constants.USER_NAME,
                            name.makeFirstLetterCapital()
                        )
                        sessionManager.saveAnyData(
                            Constants.USER_EMPLOYEE_ID,
                            loginData?.employeeId!!
                        )
//                        if (!loginData.profileImage.isNullOrEmpty()) {
//                            sessionManager.saveAnyData(
//                                Constants.USER_IMAGE,
//                                Constants.PROFILE_IMAGE_URL + loginData.profileImage
//                            )
//                        }
                        sessionManager.saveAnyData(Constants.USER_EMAIL, loginData.email)
                        sessionManager.saveAnyData(Constants.USER_MOBILE, loginData.phone)
                        sessionManager.saveAnyData(Constants.USER_ADDRESS, loginData.address)
                        sessionManager.saveAnyData(Constants.USER_LOCATION, loginData.location)
                        sessionManager.saveAnyData(
                            Constants.USER_REPORTING_MANAGER,
                            managerName.makeFirstLetterCapital()
                        )
                        sessionManager.saveAnyData(
                            Constants.USER_REPORTING_MANAGER_CONTACT,
                            loginData.reportingDetails?.phone!!
                        )
                    }
                    initViews()
                }

                is NetworkResult.Error -> {
                    binding.root.snackBar(it.message!!)
                }

                is NetworkResult.Loading -> {
                }
            }
        }

        accountViewModel.responseLogout.observe(viewLifecycleOwner) { response ->
            when (response) {
                is NetworkResult.Success -> {
                    // update UI
                    requireContext().toast(response.data?.title!!)
                    if (response.data.error == false) {
                        //Removing authentication token
                        sessionManager.removeData(Constants.IS_USER_LOGGED_IN)
                        sessionManager.removeData(Constants.TOKEN)
                        //start login screen
                        val intent = Intent(requireActivity(), LoginActivity::class.java)
                        intent.flags =
                            Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_SINGLE_TOP
                        startActivity(intent)
                        requireActivity().finish()
                    }
                    // bind data to the view
                }
                is NetworkResult.Error -> {
                    // show error message
                    binding.root.snackBar(response.message!!)
                }
                is NetworkResult.Loading -> {
                    binding.root.snackBar("Logging out...")
                }
            }
        }

        accountViewModel.responseBankNames.observe(viewLifecycleOwner) {
            bankNames = it.toTypedArray()
        }

        accountViewModel.responseBankImage.observe(viewLifecycleOwner) {
            sessionManager.saveAnyData(Constants.SELECTED_BANK, it)
            setBankImage()
        }
    }

    private fun openFileUploadDialog() {
        val items = arrayOf<CharSequence>("Camera", "Choose from gallery")
        val builder = AlertDialog.Builder(requireContext())
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
                    Permissions.check(requireContext(), permissions, rationale, options,
                        object : PermissionHandler() {
                            override fun onGranted() {
                                val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                                imageCameraLauncher.launch(intent)
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
                    Permissions.check(requireContext(), permissions, rationale, options,
                        object : PermissionHandler() {
                            override fun onGranted() {
                                val intent = Intent(
                                    Intent.ACTION_PICK,
                                    MediaStore.Images.Media.INTERNAL_CONTENT_URI
                                )
                                imageGalleryPickerLauncher.launch(intent)
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


}