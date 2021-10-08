package com.clxns.app.ui.main.cases.casedetails.casestatus.repossesions.map

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.widget.FrameLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.clxns.app.R
import com.clxns.app.databinding.ActivityReposseionMapBinding
import com.clxns.app.databinding.CustomInfoContentsBinding
import com.clxns.app.ui.main.cases.casedetails.casestatus.repossesions.RepossessionsSubmitActivity
import com.clxns.app.ui.main.plan.mapview.MapViewFragment
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.net.PlacesClient

class RepossessionMapActivity : AppCompatActivity(), OnMapReadyCallback {

    private var map : GoogleMap? = null
    private var cameraPosition : CameraPosition? = null
    private val defaultLocation = LatLng(-33.8523341, 151.2106085)
    lateinit var view1 : View

    // The geographical location where the device is currently located. That is, the last-known
    // location retrieved by the Fused Location Provider.
    private var lastKnownLocation : Location? = null
    private var likelyPlaceNames : Array<String?> = arrayOfNulls(0)
    private var likelyPlaceAddresses : Array<String?> = arrayOfNulls(0)
    private var likelyPlaceAttributions : Array<List<*>?> = arrayOfNulls(0)
    private var likelyPlaceLatLngs : Array<LatLng?> = arrayOfNulls(0)

    val latlang = LatLng(19.0434696, 73.021306)
    val latlang1 = LatLng(19.0441479, 73.0214671)
    val latlang2 = LatLng(19.0453153, 73.0244009)

    lateinit var locationArrayList : ArrayList<LatLng>

    lateinit var ctx : Context
    lateinit var reposseionMapBinding : ActivityReposseionMapBinding
    val reposessionMapViewModel : RepossesionMapViewModel by viewModels()
    private var locationPermissionGranted = false
    private lateinit var placesClient : PlacesClient
    private lateinit var fusedLocationProviderClient : FusedLocationProviderClient

    override fun onCreate(savedInstanceState : Bundle?) {
        super.onCreate(savedInstanceState)

        setInit()
        setObserver()
        setListener()

    }

    private fun setListener() {
        reposseionMapBinding.btnDropOffSet.setOnClickListener {
            startActivity(Intent(this, RepossessionsSubmitActivity::class.java))
        }

    }

    private fun setObserver() {


    }

    private fun setInit() {
        ctx = this
        reposseionMapBinding = ActivityReposseionMapBinding.inflate(layoutInflater)
        setContentView(reposseionMapBinding.root)

        locationArrayList = ArrayList<LatLng>()
        locationArrayList.add(latlang)
        locationArrayList.add(latlang1)
        locationArrayList.add(latlang2)

        getLocationPermission()

        Places.initialize(ctx, getString(R.string.google_maps_key))
        placesClient = Places.createClient(ctx)

//        showCurrentPlace()
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(ctx)

        //val mapFragment =
        //    supportFragmentManager.findFragmentById(R.id.mapFrag) as SupportMapFragment?
        //mapFragment?.getMapAsync(this)

        reposseionMapBinding.repossessionMapCustomToolbar.bringToFront()

    }


    private fun getLocationPermission() {
        /*
         * Request location permission, so that we can get the location of the
         * device. The result of the permission request is handled by a callback,
         * onRequestPermissionsResult.
         */

        if (ContextCompat.checkSelfPermission(
                ctx,
                Manifest.permission.ACCESS_FINE_LOCATION
            )
            == PackageManager.PERMISSION_GRANTED
        ) {
            locationPermissionGranted = true
        } else {
            ActivityCompat.requestPermissions(
                this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                MapViewFragment.PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION
            )
        }

        if (!isLocationEnabled()) {
            Toast.makeText(ctx, "Please turn on" + " your location...", Toast.LENGTH_LONG).show()
            val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
            startActivity(intent)
        }
    }

    private fun isLocationEnabled() : Boolean {
        val locationManager : LocationManager =
            ctx.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER
        )
    }

    override fun onMapReady(map : GoogleMap) {
        this.map = map
        for (latLong in locationArrayList) {

            map.addMarker(MarkerOptions().position(latLong).title("marker"))
            map.animateCamera(CameraUpdateFactory.zoomTo(18.0f))
            map.moveCamera(CameraUpdateFactory.newLatLng(latLong));


        }


        this.map?.setInfoWindowAdapter(object : GoogleMap.InfoWindowAdapter {
            // Return null here, so that getInfoContents() is called next.
            override fun getInfoWindow(arg0 : Marker) : View? {
                return null
            }

            override fun getInfoContents(marker : Marker) : View {
                // Inflate the layouts for the info window, title and snippet.

                val customInfoContentsBinding = CustomInfoContentsBinding.inflate(layoutInflater)

                val infoWindow = layoutInflater.inflate(
                    R.layout.custom_info_contents,
                    customInfoContentsBinding.root.findViewById<FrameLayout>(R.id.map), false
                )
                val title = infoWindow.findViewById<TextView>(R.id.title)
                title.text = marker.title
                val snippet = infoWindow.findViewById<TextView>(R.id.snippet)
                snippet.text = marker.snippet
                return infoWindow
            }
        })

        getLocationPermission()

        // Turn on the My Location layer and the related control on the map.
        updateLocationUI()

        // Get the current location of the device and set the position of the map.
        getDeviceLocation()


    }


    private fun getDeviceLocation() {
        try {
            if (locationPermissionGranted) {
                val locationResult = fusedLocationProviderClient.lastLocation
                locationResult.addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        // Set the map's camera position to the current location of the device.
                        lastKnownLocation = task.result
                        if (lastKnownLocation != null) {
                            map?.moveCamera(
                                CameraUpdateFactory.newLatLngZoom(
                                    LatLng(
                                        lastKnownLocation!!.latitude,
                                        lastKnownLocation!!.longitude
                                    ), MapViewFragment.DEFAULT_ZOOM.toFloat()
                                )
                            )
                        }
                    } else {
                        map?.moveCamera(
                            CameraUpdateFactory
                                .newLatLngZoom(
                                    defaultLocation,
                                    MapViewFragment.DEFAULT_ZOOM.toFloat()
                                )
                        )
                        map?.uiSettings?.isMyLocationButtonEnabled = false
                    }
                }
            }
        } catch (e : SecurityException) {
        }
    }

    private fun updateLocationUI() {
        if (map == null) {
            return
        }
        try {
            if (locationPermissionGranted) {
                map?.isMyLocationEnabled = true
                map?.uiSettings?.isMyLocationButtonEnabled = true
            } else {
                map?.isMyLocationEnabled = false
                map?.uiSettings?.isMyLocationButtonEnabled = false
                lastKnownLocation = null
            }
        } catch (e : SecurityException) {
        }
    }

}