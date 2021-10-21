package com.clxns.app.ui

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.clxns.app.databinding.ActivityMapsBinding
import com.clxns.app.utils.toast
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.tasks.Task
import timber.log.Timber


class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mGoogleMap : GoogleMap
    private lateinit var mLocationClient : FusedLocationProviderClient
    private lateinit var mLocationCallback : LocationCallback
    private lateinit var binding : ActivityMapsBinding

    companion object {
        private const val DEFAULT_ZOOM = 15
        private const val MUMBAI_LAT = 19.0633271
        private const val MUMBAI_LNG = 72.8799367

        private const val PERMISSION_REQUEST_CODE = 9001
        private const val GPS_REQUEST_CODE = 9002
        private const val PLAY_SERVICES_ERROR_CODE = 9003
        private var mLocationPermissionGranted = false
    }

    override fun onCreate(savedInstanceState : Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initGoogleMap()
    }

    private fun showMarker(latitude : Double, longitude : Double) {
        val markerOptions = MarkerOptions()
        markerOptions.position(LatLng(latitude, longitude))
        mGoogleMap.addMarker(markerOptions)
    }

    private fun gotoLocation(latitude : Double, longitude : Double) {
        val latLng = LatLng(latitude, longitude)

        val cameraUpdate = CameraUpdateFactory.newLatLngZoom(
            latLng,
            DEFAULT_ZOOM.toFloat()
        )

        mGoogleMap.moveCamera(cameraUpdate)
//        mGoogleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
    }

    private fun initGoogleMap() {
        if (isServicesOk()) {
            if (isGPSEnabled()) {
                if (checkLocationPermission()) {
                    Toast.makeText(this, "Ready to Map", Toast.LENGTH_SHORT).show()
                    val supportMapFragment = supportFragmentManager
                        .findFragmentById(com.clxns.app.R.id.map) as SupportMapFragment?
                    supportMapFragment!!.getMapAsync(this)

                    mLocationClient = LocationServices.getFusedLocationProviderClient(this)
                    mLocationCallback = object : LocationCallback() {
                        override fun onLocationResult(locationResult : LocationResult) {
                            val location : Location = locationResult.lastLocation
                            Timber.i(
                                location.latitude.toString() + " : " + location.longitude
                            )
                            gotoLocation(location.latitude, location.longitude)
                            showMarker(location.latitude, location.longitude)
                        }
                    }

                    // Obtain the SupportMapFragment and get notified when the map is ready to be used.
                    getLocationUpdates()
                } else {
                    requestLocationPermission()
                }
            }
        }
    }

    private fun requestLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED
        ) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                    PERMISSION_REQUEST_CODE
                )
            }
        }
    }

    private fun isServicesOk() : Boolean {
        val googleApi = GoogleApiAvailability.getInstance()

        val result = googleApi.isGooglePlayServicesAvailable(this)

        when {
            result == ConnectionResult.SUCCESS -> {
                return true
            }
            googleApi.isUserResolvableError(result) -> {
                val dialog : Dialog? =
                    googleApi.getErrorDialog(this, result, PLAY_SERVICES_ERROR_CODE) {
                        toast("Dialog is cancelled by User")
                    }
                dialog?.show()
            }
            else -> {
                toast("Play services are required by this application")
            }
        }
        return false
    }

    private fun checkLocationPermission() : Boolean {
        return (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED)
    }

    private fun isGPSEnabled() : Boolean {
        val locationManager = getSystemService(LOCATION_SERVICE) as LocationManager

        val providerEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)

        if (providerEnabled) {
            return true
        } else {
            val alertDialog : AlertDialog = AlertDialog.Builder(this)
                .setTitle("GPS Permissions")
                .setMessage("GPS is required for this app to work. Please enable GPS.")
                .setPositiveButton("Yes") { _, _ ->
                    val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                    startActivityForResult(intent, GPS_REQUEST_CODE)
                }
                .setCancelable(false)
                .show()
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
        gotoLocation(MUMBAI_LAT, MUMBAI_LNG)
        showMarker(MUMBAI_LAT, MUMBAI_LNG)
        mGoogleMap.uiSettings.isZoomControlsEnabled = true
        mGoogleMap.uiSettings.isMapToolbarEnabled = true
    }


    /*private fun getCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            mLocationClient.lastLocation.addOnCompleteListener { task : Task<Location> ->
                if (task.isSuccessful) {
                    val location = task.result
                    gotoLocation(location.latitude, location.longitude)
                }
            }
        }

    }*/

    @SuppressLint("MissingPermission")
    private fun getLocationUpdates() {
        val locationRequest = LocationRequest.create()
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        locationRequest.interval = 5000
        locationRequest.fastestInterval = 2000

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }

        Thread(Runnable {
            Looper.prepare()
            if (ActivityCompat.checkSelfPermission(
                    this, Manifest.permission.ACCESS_FINE_LOCATION
                )
                != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
                != PackageManager.PERMISSION_GRANTED
            ) {

                return@Runnable
            }
            mLocationClient.requestLocationUpdates(
                locationRequest,
                mLocationCallback,
                Looper.getMainLooper()
            )
        }).start()
    }


    override fun onActivityResult(requestCode : Int, resultCode : Int, data : Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == GPS_REQUEST_CODE) {
            val locationManager = getSystemService(LOCATION_SERVICE) as LocationManager
            val providerEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
            if (providerEnabled) {
                toast("GPS is enabled")
            } else {
                toast("GPS not enabled. Unable to show user location")
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode : Int,
        permissions : Array<String?>,
        grantResults : IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_REQUEST_CODE && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionGranted = true
            toast("Permission granted")
        } else {
            toast("Permission not granted")
        }
    }

    override fun onPause() {
        super.onPause()
        mLocationClient.removeLocationUpdates(mLocationCallback)
    }
}