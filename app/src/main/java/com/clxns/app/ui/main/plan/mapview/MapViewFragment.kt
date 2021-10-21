package com.clxns.app.ui.main.plan.mapview

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.clxns.app.R
import com.clxns.app.databinding.CustomInfoContentsBinding
import com.clxns.app.databinding.FragmentMapViewBinding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.FindCurrentPlaceRequest
import com.google.android.libraries.places.api.net.PlacesClient
import timber.log.Timber


class MapViewFragment : Fragment(), OnMapReadyCallback {

    private var map : GoogleMap? = null
    private var cameraPosition : CameraPosition? = null
    private lateinit var placesClient : PlacesClient
    private lateinit var fusedLocationProviderClient : FusedLocationProviderClient
    private val defaultLocation = LatLng(-33.8523341, 151.2106085)
    private var locationPermissionGranted = false
    lateinit var ctx : Context
    lateinit var view1 : View
    lateinit var mapViewBinding : FragmentMapViewBinding

    // The geographical location where the device is currently located. That is, the last-known
    // location retrieved by the Fused Location Provider.
    private var lastKnownLocation : Location? = null
    private var likelyPlaceNames : Array<String?> = arrayOfNulls(0)
    private var likelyPlaceAddresses : Array<String?> = arrayOfNulls(0)
    private var likelyPlaceAttributions : Array<List<*>?> = arrayOfNulls(0)
    private var likelyPlaceLatLngs : Array<LatLng?> = arrayOfNulls(0)

    private val latlang = LatLng(19.0434696, 73.021306)
    private val latlang1 = LatLng(19.0441479, 73.0214671)
    private val latlang2 = LatLng(19.0453153, 73.0244009)

    lateinit var locationArrayList : ArrayList<LatLng>


    override fun onCreateView(
        inflater : LayoutInflater,
        container : ViewGroup?,
        savedInstanceState : Bundle?
    ) : View? {

        setInit()
        setListeners()

        return view1
    }


    private fun setListeners() {
        mapViewBinding.btnCreateBestRoute.setOnClickListener { }

    }


    private fun setInit() {

        locationArrayList = ArrayList()
        locationArrayList.add(latlang)
        locationArrayList.add(latlang1)
        locationArrayList.add(latlang2)

        Timber.i("locationArrayList---->" + locationArrayList.size)

        getLocationPermission()

        mapViewBinding = FragmentMapViewBinding.inflate(layoutInflater)
        view1 = mapViewBinding.root

        Places.initialize(ctx, "444245")
        placesClient = Places.createClient(ctx)

//        showCurrentPlace()
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(ctx)

        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(this)


    }


    @SuppressLint("MissingPermission")
    private fun showCurrentPlace() {
        if (map == null) {
            return
        }
        if (locationPermissionGranted) {
            // Use fields to define the data types to return.
            val placeFields = listOf(Place.Field.NAME, Place.Field.ADDRESS, Place.Field.LAT_LNG)

            // Use the builder to create a FindCurrentPlaceRequest.
            val request = FindCurrentPlaceRequest.newInstance(placeFields)

            // Get the likely places - that is, the businesses and other points of interest that
            // are the best match for the device's current location.
            val placeResult = placesClient.findCurrentPlace(request)
            placeResult.addOnCompleteListener { task ->
                if (task.isSuccessful && task.result != null) {
                    val likelyPlaces = task.result

                    Timber.i("likelyPlaces--->$likelyPlaces")

                    // Set the count, handling cases where less than 5 entries are returned.
                    val count =
                        if (likelyPlaces != null && likelyPlaces.placeLikelihoods.size < M_MAX_ENTRIES) {
                            likelyPlaces.placeLikelihoods.size
                        } else {
                            M_MAX_ENTRIES
                        }

                    Timber.i("count--->$count")
                    var i = 0
                    likelyPlaceNames = arrayOfNulls(count)
                    likelyPlaceAddresses = arrayOfNulls(count)
                    likelyPlaceAttributions = arrayOfNulls<List<*>?>(count)
                    likelyPlaceLatLngs = arrayOfNulls(count)
                    for (placeLikelihood in likelyPlaces?.placeLikelihoods ?: emptyList()) {
                        // Build a list of likely places to show the user.
                        likelyPlaceNames[i] = placeLikelihood.place.name
                        likelyPlaceAddresses[i] = placeLikelihood.place.address
                        likelyPlaceAttributions[i] = placeLikelihood.place.attributions
                        likelyPlaceLatLngs[i] = placeLikelihood.place.latLng
                        i++
                        if (i > count - 1) {
                            break
                        }
                    }

                    // Show a dialog offering the user the list of likely places, and add a
                    // marker at the selected place.
                    openPlacesDialog()
                } else {
                    Timber.e(task.exception, "Exception: %s")
                }
            }
        } else {
            // The user has not granted permission.
            Timber.i("The user did not grant location permission.")

            // Add a default marker, because the user hasn't selected a place.
            map?.addMarker(
                MarkerOptions()
                    .title("")
                    .position(defaultLocation)
                    .snippet("")
            )

            // Prompt the user for permission.
            getLocationPermission()
        }
    }

    private fun openPlacesDialog() {
        // Ask the user to choose the place where they are now.
        val listener =
            DialogInterface.OnClickListener { _, which -> // The "which" argument contains the position of the selected item.
                val markerLatLng = likelyPlaceLatLngs[which]
                var markerSnippet = likelyPlaceAddresses[which]
                if (likelyPlaceAttributions[which] != null) {
                    markerSnippet = """
                    $markerSnippet
                    ${likelyPlaceAttributions[which]}
                    """.trimIndent()
                }

                // Add a marker for the selected place, with an info window
                // showing information about that place.
                map?.addMarker(
                    MarkerOptions()
//                    .title(likelyPlaceNames[which])
                        .title(likelyPlaceNames[which])
                        .position(markerLatLng!!)
                        .snippet(markerSnippet)
                )

                // Position the map's camera at the location of the marker.
                map?.moveCamera(
                    CameraUpdateFactory.newLatLngZoom(
                        markerLatLng,
                        DEFAULT_ZOOM.toFloat()
                    )
                )
            }

        // Display the dialog.
        AlertDialog.Builder(ctx)
            .setTitle("Pick Place")
            .setItems(likelyPlaceNames, listener)
            .show()

    }

    private fun getLocationPermission() {
        /*
         * Request location permission, so that we can get the location of the
         * device. The result of the permission request is handled by a callback,
         * onRequestPermissionsResult.
         */

        ctx = requireActivity()
        if (ContextCompat.checkSelfPermission(ctx, Manifest.permission.ACCESS_FINE_LOCATION)
            == PackageManager.PERMISSION_GRANTED
        ) {
            locationPermissionGranted = true
        } else {
            ActivityCompat.requestPermissions(
                requireActivity(), arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION
            )
        }

        if (!isLocationEnabled()) {
            Toast.makeText(ctx, "Please turn on" + " your location...", Toast.LENGTH_LONG).show()
            val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
            startActivity(intent)
        }
    }


    companion object {
        const val DEFAULT_ZOOM = 15
        const val PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1

        // Used for selecting the current place.
        private const val M_MAX_ENTRIES = 5
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
        Timber.i("onMapReady---------->")

        map.uiSettings.isZoomControlsEnabled = true
        val titleArr = listOf(
            "Ali Shaikh, Mumbai 400024",
            "Priti, Navi Mumbai 400687",
            "Navi Mumbai 400706"
        )
        for ((i, latLong) in locationArrayList.withIndex()) {
            map.addMarker(
                MarkerOptions().position(latLong).title(titleArr[i])
            )
//            map.animateCamera(CameraUpdateFactory.zoomTo(50.0f))
//            map.moveCamera(CameraUpdateFactory.newLatLng(latLong))
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLong, 14.5f))
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
                Timber.i("getInfoContent Count--->" + marker.title)
                title.text = marker.title
                val snippet = infoWindow.findViewById<TextView>(R.id.snippet)
                snippet.text = marker.snippet
                return infoWindow
            }
        })

        // Turn on the My Location layer and the related control on the map.
        updateLocationUI()

        // Get the current location of the device and set the position of the map.
        getDeviceLocation()
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
                //getLocationPermission()
            }
        } catch (e : SecurityException) {
            Timber.e(e, e.message)
        }
    }

    private fun getDeviceLocation() {
        /*
         * Get the best and most recent location of the device, which may be null in rare
         * cases when a location is not available.
         */
        try {
            if (locationPermissionGranted) {
                val locationResult = fusedLocationProviderClient.lastLocation
                locationResult.addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        // Set the map's camera position to the current location of the device.

                        Timber.i("task.result--->" + task.result)
                        lastKnownLocation = task.result
                        if (lastKnownLocation != null) {
                            map?.moveCamera(
                                CameraUpdateFactory.newLatLngZoom(
                                    LatLng(
                                        lastKnownLocation!!.latitude,
                                        lastKnownLocation!!.longitude
                                    ), DEFAULT_ZOOM.toFloat()
                                )
                            )
                        }
                    } else {
                        Timber.d("Current location is null. Using defaults.")
                        Timber.e(task.exception, "Exception: %s")
                        map?.moveCamera(
                            CameraUpdateFactory
                                .newLatLngZoom(defaultLocation, DEFAULT_ZOOM.toFloat())
                        )
                        map?.uiSettings?.isMyLocationButtonEnabled = false
                    }
                }
            }
        } catch (e : SecurityException) {
            Timber.e(e, e.message)
        }
    }


}