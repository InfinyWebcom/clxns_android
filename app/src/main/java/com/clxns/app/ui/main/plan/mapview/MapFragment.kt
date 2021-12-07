package com.clxns.app.ui.main.plan.mapview

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity.RESULT_OK
import android.app.Dialog
import android.app.ProgressDialog
import android.content.Intent
import android.content.IntentSender
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.text.style.RelativeSizeSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getColor
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.clxns.app.R
import com.clxns.app.data.api.helper.NetworkResult
import com.clxns.app.data.preference.SessionManager
import com.clxns.app.databinding.CustomInfoWindowBinding
import com.clxns.app.databinding.FragmentMapBinding
import com.clxns.app.ui.main.plan.plannedLeads.MyPlanViewModel
import com.clxns.app.utils.*
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.tasks.Task
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import java.io.IOException
import javax.inject.Inject


@AndroidEntryPoint
class MapFragment : Fragment(), OnMapReadyCallback {

    private val viewModel : MyPlanViewModel by activityViewModels()
    private var _binding : FragmentMapBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    @Inject
    lateinit var sessionManager : SessionManager

    private lateinit var mGoogleMap : GoogleMap
    private lateinit var mLocationClient : FusedLocationProviderClient
    private lateinit var mLocationCallback : LocationCallback
    private lateinit var mLocationRequest : LocationRequest

    private lateinit var userName : String

    private lateinit var myLocationLatLng : LatLng

    private lateinit var progressDialog : ProgressDialog

    private lateinit var noInternetLayout : RelativeLayout

    companion object {
        private const val DEFAULT_ZOOM = 12

        private const val PLAY_SERVICES_ERROR_CODE = 9001
        private var mLocationPermissionGranted = false
    }


    override fun onCreateView(
        inflater : LayoutInflater,
        container : ViewGroup?,
        savedInstanceState : Bundle?,
    ) : View {
        _binding = FragmentMapBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view : View, savedInstanceState : Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        noInternetLayout = binding.mapNoInternetLayout.root

        userName = sessionManager.getString(Constants.USER_NAME).toString()
        progressDialog = getProgressDialog(
            requireContext(),
            "Loading map...",
            "Please wait while we're initializing the map"
        )

        requestLocationPermission()
        createLocationRequest()
        initGoogleMap()

        checkInternetAvailability()

        binding.mapNoInternetLayout.retryBtn.setOnClickListener {
            checkInternetAvailability()
        }
    }

    private fun checkInternetAvailability() {
        if (ViewUtils.isInternetAvailable(requireContext())) {
            checkIfPermissionAndLocationHasProvided()
            noInternetLayout.hide()
        } else {
            noInternetLayout.show()
            binding.mapNoInternetLayout.noDataContainer.setBackgroundColor(
                getColor(
                    requireContext(),
                    R.color.white
                )
            )
            val s = "No Internet Connection!!.\nPlease provide internet access to continue."
            val span = SpannableString(s)
            span.setSpan(RelativeSizeSpan(1.2f), 0, 25, 0)
            span.setSpan(
                ForegroundColorSpan(getColor(requireContext(), R.color.red_orange)),
                0,
                25,
                0
            )
            binding.mapNoInternetLayout.noDataTxt.text = span
        }
    }

    private fun checkIfPermissionAndLocationHasProvided() {
        if (isServicesOk()) {
            if (mLocationPermissionGranted) {
                if (isGPSEnabled()) {
                    getLocationUpdates()
                } else {
                    openLocationToggleDialog()
                }
            }
        }
    }

    private fun initViewModel() {
        viewModel.response.observe(viewLifecycleOwner) { response ->
            when (response) {
                is NetworkResult.Success -> {
                    mGoogleMap.clear()
                    if (::myLocationLatLng.isInitialized) {
                        showMarker(myLocationLatLng, userName, "MY LOCATION")
                    }
                    if (!response.data?.error!!) {
                        val planList = response.data.data
                        if (!planList.isNullOrEmpty()) {
                            planList.forEach {
                                if (it.lead != null) {
                                    if (!it.lead.address.isNullOrEmpty()) {
                                        val address = it.lead.address.makeFirstLetterCapital()
                                        val lat = getLocationFromAddress(it.lead.address)
                                        if (lat != null) {
                                            val distanceInKM =
                                                getDistanceInKM(myLocationLatLng, lat).toInt()
                                            showMarker(
                                                lat,
                                                "${it.lead.name.toString()} - $distanceInKM KM Away",
                                                address
                                            )
                                        } else {
                                            Timber.i("Address returned was null")
                                        }
                                    }
                                }
                            }
                        } else {
                            Timber.i("Empty plan list for Map")
                        }
                    }
                    // bind data to the view
                }
                else -> {}
            }
        }
    }

    private fun showMarker(latLong : LatLng, title : String, address : String) {
        val markerOptions = MarkerOptions()
        markerOptions.position(latLong).title(title).snippet(address)
        mGoogleMap.addMarker(markerOptions)
    }

    private fun gotoLocation(latitude : Double, longitude : Double) {
        val latLng = LatLng(latitude, longitude)

        val cameraUpdate = CameraUpdateFactory.newLatLngZoom(
            latLng,
            DEFAULT_ZOOM.toFloat()
        )

        mGoogleMap.animateCamera(cameraUpdate)
    }

    private fun initGoogleMap() {
        val supportMapFragment = childFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment?
        supportMapFragment!!.getMapAsync(this)

        mLocationClient =
            LocationServices.getFusedLocationProviderClient(requireContext())
        mLocationCallback = object : LocationCallback() {
            @SuppressLint("MissingPermission")
            override fun onLocationResult(locationResult : LocationResult) {
                val location : Location? = locationResult.lastLocation
                if (location != null) {
                    //mGoogleMap.uiSettings.isMyLocationButtonEnabled = true
                    //mGoogleMap.isMyLocationEnabled = true
                    Timber.i(
                        location.latitude.toString() + " : " + location.longitude
                    )
                    myLocationLatLng = LatLng(location.latitude, location.longitude)
                    initViewModel()
                    gotoLocation(myLocationLatLng.latitude, myLocationLatLng.longitude)
                    showMarker(myLocationLatLng, userName, "MY LOCATION")

                } else {
                    Timber.i("Location returned was null")
                }
                progressDialog.dismiss()
                //Removing callback after getting the current location coz we don't wanna keep requesting the location
                mLocationClient.removeLocationUpdates(mLocationCallback)
            }
        }
    }

    private fun getDistanceInKM(
        startPoint : LatLng,
        endPoint : LatLng,
    ) : Float {
        val locStart = Location("")
        locStart.latitude = startPoint.latitude
        locStart.longitude = startPoint.longitude
        val locEnd = Location("")
        locEnd.latitude = endPoint.latitude
        locEnd.longitude = endPoint.longitude
        return (locStart.distanceTo(locEnd) / 1000)
    }

    private fun openLocationToggleDialog() {
        val builder = LocationSettingsRequest.Builder().addLocationRequest(mLocationRequest)

        val client : SettingsClient = LocationServices.getSettingsClient(requireContext())
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

    private fun createLocationRequest() {
        mLocationRequest = LocationRequest.create()
            .setInterval(2000)
            .setFastestInterval(5000)
            .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
    }

    private fun getLocationFromAddress(strAddress : String?) : LatLng? {
        val coder = Geocoder(requireContext())
        val address : List<Address>?
        var p1 : LatLng? = null
        try {
            // May throw an IOException
            address = coder.getFromLocationName(strAddress, 5)
            if (address == null || address.isEmpty()) {
                return null
            }
            val location : Address = address[0]
            p1 = LatLng(location.latitude, location.longitude)
        } catch (ex : IOException) {
            ex.printStackTrace()
        }
        return p1
    }

    private fun requestLocationPermission() {
        if (checkLocationPermission()) {
            mLocationPermissionGranted = true
        } else {
            permissionCallback.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    private val locationSettingCallback =
        registerForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) {
            if (it.resultCode == RESULT_OK) {
                getLocationUpdates()
            } else {
                requireContext().toast("Please provide location access to continue")
                openLocationToggleDialog()
            }
        }

    private val permissionCallback =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) {
            if (it) {
                mLocationPermissionGranted = true
                checkIfPermissionAndLocationHasProvided()
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

    private val forceRequestPermissionCallBack =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (checkLocationPermission()) {
                mLocationPermissionGranted = true
                checkIfPermissionAndLocationHasProvided()
            } else {
                binding.root.snackBar("Please provide location permission to continue.")
            }
        }


    private fun isServicesOk() : Boolean {
        val googleApi = GoogleApiAvailability.getInstance()

        val result = googleApi.isGooglePlayServicesAvailable(requireContext())

        when {
            result == ConnectionResult.SUCCESS -> {
                return true
            }
            googleApi.isUserResolvableError(result) -> {
                val dialog : Dialog? =
                    googleApi.getErrorDialog(this, result, PLAY_SERVICES_ERROR_CODE) {
                        requireContext().toast("Dialog is cancelled by User")
                    }
                dialog?.show()
                progressDialog.dismiss()
            }
            else -> {
                requireContext().toast("Play services are required by this application")
            }
        }
        return false
    }

    private fun checkLocationPermission() : Boolean {
        return (ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED)
    }

    private fun isGPSEnabled() : Boolean {
        val locationManager =
            requireContext().getSystemService(AppCompatActivity.LOCATION_SERVICE) as LocationManager

        val providerEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)

        if (providerEnabled) {
            return true
        }
        return false
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap : GoogleMap) {
        mGoogleMap = googleMap
        mGoogleMap.uiSettings.isZoomControlsEnabled = true
        mGoogleMap.setInfoWindowAdapter(CustomInfoAdapter())
    }


    @SuppressLint("MissingPermission")
    private fun getLocationUpdates() {
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            progressDialog.show()
            mLocationClient.requestLocationUpdates(
                mLocationRequest,
                mLocationCallback,
                Looper.getMainLooper()
            )
        } else {
            binding.root.snackBar("Failed to fetch the current location")
        }

    }


    private fun showPermissionRequiredPopUp(message : String, goToSettings : Boolean) {
        val dialog = MaterialAlertDialogBuilder(requireContext())
        dialog.apply {
            setTitle("Location Permission Required")
            setMessage(message)
            setCancelable(false)
            create()
            setPositiveButton(
                "Set Permission"
            ) { d, _ ->
                if (goToSettings) {
                    val intentToSetting = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                    intentToSetting.data =
                        Uri.fromParts("package", requireActivity().packageName, null)
                    forceRequestPermissionCallBack.launch(intentToSetting)
                } else {
                    requestLocationPermission()
                }
                d.dismiss()
            }
        }
        dialog.show()
    }

    override fun onPause() {
        super.onPause()
        mLocationClient.removeLocationUpdates(mLocationCallback)
    }

    internal inner class CustomInfoAdapter : GoogleMap.InfoWindowAdapter {
        private val window : CustomInfoWindowBinding =
            CustomInfoWindowBinding.inflate(layoutInflater)
        private val contents : CustomInfoWindowBinding =
            CustomInfoWindowBinding.inflate(layoutInflater)

        override fun getInfoWindow(marker : Marker) : View {
            render(marker, window)
            return window.root
        }

        private fun render(marker : Marker, window : CustomInfoWindowBinding) {
            window.leadNameTxt.text = marker.title
            window.leadAddressTxt.text = marker.snippet
        }

        override fun getInfoContents(marker : Marker) : View {
            render(marker, contents)
            return contents.root
        }

    }

}