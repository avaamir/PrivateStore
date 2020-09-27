package com.amir.ir.privatestore.ui.activities

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.os.PersistableBundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.amir.ir.privatestore.R
import com.amir.ir.privatestore.models.Address
import com.amir.ir.privatestore.models.requests.SearchAddressItem
import com.amir.ir.privatestore.repository.apiservice.ApiService
import com.amir.ir.privatestore.repository.apiservice.MapService
import com.amir.ir.privatestore.ui.dialogs.LocationPermissionDialog
import com.amir.ir.privatestore.ui.dialogs.NoNetworkDialog
import com.amir.ir.privatestore.utils.*
import com.amir.ir.privatestore.viewmodels.MapsActivityViewModel
import com.google.android.material.snackbar.Snackbar
import com.mapbox.mapboxsdk.Mapbox
import com.mapbox.mapboxsdk.annotations.MarkerOptions
import com.mapbox.mapboxsdk.camera.CameraPosition
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory
import com.mapbox.mapboxsdk.geometry.LatLng
import com.mapbox.mapboxsdk.geometry.LatLngBounds
import com.mapbox.mapboxsdk.location.LocationComponentActivationOptions
import com.mapbox.mapboxsdk.location.LocationComponentOptions
import com.mapbox.mapboxsdk.location.modes.CameraMode
import com.mapbox.mapboxsdk.location.modes.RenderMode
import com.mapbox.mapboxsdk.maps.MapboxMap
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback
import com.mapbox.mapboxsdk.maps.Style
import kotlinx.android.synthetic.main.activity_map.*


class MapsActivity : AppCompatActivity(), OnMapReadyCallback,
    LocationPermissionHelper.Interactions,
    MapboxMap.OnMapClickListener, LocationListener, ApiService.InternetConnectionListener {

    private var snackbar: Snackbar? = null

    companion object {
        private const val DEFAULT_ZOOM = 15f
        const val REQUEST_CODE_MAPS_ACTIVITY = 1234
    }

    private lateinit var viewModel: MapsActivityViewModel

    private lateinit var locationPermissionHelper: LocationPermissionHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Mapbox.getInstance(this, resources.getString(R.string.map_box_api_token))
        setContentView(R.layout.activity_map)
        mapView.onCreate(savedInstanceState)

        viewModel = ViewModelProvider(this).get(MapsActivityViewModel::class.java)
        viewModel.isFirstMapClickedFlag = true

        val address: Address? = intent.getParcelableExtra(Constants.INTENT_MAP_ACTIVITY_ADDRESS)
        println("debug: MapActivityGetIntent: $address")
        address?.let {
            viewModel.setAddressFromIntent(address)
        }

        hideSoftKeyboard()
        subscribeObservers()


        locationPermissionHelper =
            object : LocationPermissionHelper(this@MapsActivity, this@MapsActivity) {
                override fun requiredPermissionAction() {
                    mapView.getMapAsync(this@MapsActivity)
                }
            }
        locationPermissionHelper.checkPermissionAndPickImage()
    }

    private fun subscribeObservers() {
        viewModel.reverseGeo.observe(this, Observer {
            btnSubmit.showProgressBar(false)
            if (it != null) {
                if (it.hasError) {
                    it.message?.let { errorMsg ->
                        toast(errorMsg)
                    }
                } else {
                    animateButtonSaveAddress()
                    etSearch.text = it.address_compact
                    //TODO hide the progress bar
                }
            } else {
                if (snackbar == null) {
                    snack(Constants.SERVER_ERROR) {
                        viewModel.tryAgain()
                        snackbar = null
                    }
                }
            }

        })
    }

    private fun enableLocationComponent(loadedMapStyle: Style) {
        if (!viewModel.isFirstLoadUserLocUI) {
            return
        }
        val customLocationComponentOptions = LocationComponentOptions.builder(this)
            .trackingGesturesManagement(true)
            .accuracyColor(ContextCompat.getColor(this, R.color.green))
            .build()

        val locationComponentActivationOptions =
            LocationComponentActivationOptions.builder(this, loadedMapStyle)
                .locationComponentOptions(customLocationComponentOptions)
                .build()

        // Get an instance of the LocationComponent and then adjust its settings
        viewModel.mapboxMap!!.locationComponent.apply {
            // Activate the LocationComponent with options
            activateLocationComponent(locationComponentActivationOptions)
            // Enable to make the LocationComponent visible
            isLocationComponentEnabled = true
            // Set the LocationComponent's camera mode
            cameraMode = CameraMode.TRACKING
            // Set the LocationComponent's render mode
            renderMode = RenderMode.COMPASS
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String?>,
        grantResults: IntArray
    ) {
        locationPermissionHelper.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }


    @SuppressLint("MissingPermission")
    private fun setupLocationManager() {

        val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager

        if (
            locationManager.allProviders.contains(LocationManager.NETWORK_PROVIDER)
        ) {
            println("debug: allProviders contains NETWORK_PROVIDER")
            locationManager.requestLocationUpdates(
                LocationManager.NETWORK_PROVIDER,
                0L,
                0f,
                this
            )

            val lastNetworkKnownLocation =
                locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
            if (lastNetworkKnownLocation != null && viewModel.userLocation == null) {
                onLocationChanged(lastNetworkKnownLocation)
            }
            println("debug: lastNetworkKnownLocation: $lastNetworkKnownLocation")
        }

        if (
            locationManager.allProviders.contains(LocationManager.GPS_PROVIDER)
        ) {
            println("debug: allProviders contains GPS_PROVIDER")
            locationManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER,
                0L,
                0f,
                this
            )
            val lastGpsKnownLocation =
                locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
            if (lastGpsKnownLocation != null) {
                onLocationChanged(lastGpsKnownLocation)
            }
            println("debug: lastGpsKnownLocation: $lastGpsKnownLocation")
        }

        if (
            locationManager.allProviders.contains(LocationManager.PASSIVE_PROVIDER)
        ) {
            println("debug: allProviders contains GPS_PROVIDER")
            locationManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER,
                0L,
                0f,
                this
            )
            val lastGpsKnownLocation =
                locationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER)
            if (lastGpsKnownLocation != null) {
                onLocationChanged(lastGpsKnownLocation)
            }
            println("debug: lastGpsKnownLocation: $lastGpsKnownLocation")
        }

    }

    private fun moveCameraAndAddMarker(
        latLng: LatLng,
        zoom: Float = DEFAULT_ZOOM,
        //title: String? = null,
        shouldAddMarker: Boolean = true,
        shouldAnimate: Boolean = true
    ) {
        val position: CameraPosition = CameraPosition.Builder()
            .target(latLng)
            .zoom(zoom.toDouble()) // Sets the zoom
            //.bearing(180.toDouble()) // Rotate the camera
            //.tilt(30.toDouble()) // Set the camera tilt
            .build() // Creates a CameraPosition from the builder

        if (shouldAnimate) {
            viewModel.mapboxMap!!.animateCamera(
                CameraUpdateFactory.newCameraPosition(position),
                1000
            )
        } else {
            viewModel.mapboxMap!!.easeCamera(
                CameraUpdateFactory.newLatLngZoom(
                    latLng,
                    DEFAULT_ZOOM.toDouble()
                )
            )
        }

        if (shouldAddMarker) {
            if (viewModel.selectedLocationMarker == null) { //make marker and set position
                //todo icon monaseb entekhab shavad, PNG bashad behtar ast
                /*todo val icon = IconFactory.getInstance(this).fromBitmap(getBitmapFromVectorDrawable(this, R.drawable.ic_marker_1))*/
                viewModel.selectedLocationMarker = viewModel.mapboxMap!!.addMarker(
                    MarkerOptions()
                        .position(latLng)
                        .title("محل تحویل")
                    //todo .icon(icon)
                )
            } else { //change the position
                viewModel.selectedLocationMarker!!.position = latLng
            }

        }
    }

    private fun moveCameraToBounds(point1: LatLng, point2: LatLng) {
        val sampleLatLngList = listOf(point1, point2)
        val samplePadding =
            10 // فاصله ای است که محدوده ی مشخص شده در نقشه پس از جابه جایی با اطراف صفحه ی نمایش دارد
        //val sampleBearing = 30 // جهت نقشه را مشخص می کند
        //val sampleTilt = 45 // زاویه ی نقشه را مشخص می کند
        val sampleLatLngBounds: LatLngBounds =
            LatLngBounds.Builder().includes(sampleLatLngList).build()
        viewModel.mapboxMap!!.easeCamera(
            CameraUpdateFactory.newLatLngBounds(
                sampleLatLngBounds,
                //sampleBearing.toDouble(),
                //sampleTilt.toDouble(),
                samplePadding
            )
        )
    }

    private fun initViews() {
        btnSubmit.setOnClickListener {
            setResult(
                Activity.RESULT_OK,
                Intent().apply {
                    val address = viewModel.getAddressForSubmit()
                    putExtra(Constants.INTENT_MAP_ACTIVITY_ADDRESS, address)
                }
            )
            finish()
        }

        btnMyLocation.setOnClickListener {
            enableLocationComponent(viewModel.mapboxMap!!.style!!) //move to user location //todo vaghti loc darim yaani tu chand khat bad nemikham hedayat beshe be loc user
            if (viewModel.isProviderEnabled || viewModel.userLocation != null) {
                viewModel.userLocation?.let {
                    println("debug: btnFindMe Clicked: UserLoc=$it")
                    moveCameraAndAddMarker(latLng = it, shouldAddMarker = false)
                }
            } else {
                //TODO show a dialog
                toast("لطفا GPS خود را روشن کنید")
            }
        }

        val onClick = View.OnClickListener {
            val intent = Intent(this, SearchMapActivity::class.java)
            intent.putExtra(Constants.INTENT_SEARCH_MAP_ACTIVITY_LAT_LNG, viewModel.userLocation)
            startActivityForResult(intent, Constants.INTENT_SEARCH_MAP_ACTIVITY_REQ_FOR_RESULT)
            overridePendingTransition(R.anim.activity_start_enter_top, R.anim.nothing)
        }
        btnSearch.setOnClickListener(onClick)
        etSearch.setOnClickListener(onClick)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == Constants.INTENT_SEARCH_MAP_ACTIVITY_REQ_FOR_RESULT) {
            if (resultCode == Activity.RESULT_OK) {
                val searchAddressItem =
                    data!!.getParcelableExtra<SearchAddressItem>(Constants.INTENT_SEARCH_MAP_ACTIVITY_RESULT_ADDRESS)

                etSearch.text = searchAddressItem.address

                val coordinates = searchAddressItem?.geom?.coordinates!! //todo momkene null bashe??
                val latLng = LatLng(coordinates[1].toDouble(), coordinates[0].toDouble())

                moveCameraAndAddMarker(latLng, shouldAnimate = false)
                viewModel.setAddressItem(searchAddressItem)
                animateButtonSaveAddress()
            }
        }
    }

    override fun onMapReady(mapboxMap: MapboxMap) {
        viewModel.mapboxMap = mapboxMap
        mapboxMap.setMaxZoomPreference(20.toDouble()) //todo check this line

        val style = Style.MAPBOX_STREETS
        mapboxMap.setStyle(style) { _style -> //onStyleLoaded Callback

            initViews()

            if (!viewModel.isMapMarked) { //todo test purpose :: check next line todo
                enableLocationComponent(_style) //move to user location //todo vaghti loc darim yaani tu chand khat bad nemikham hedayat beshe be loc user
            }
            setupLocationManager()
            btnMyLocation.visibility = View.VISIBLE
            mapboxMap.addOnMapClickListener(this)
        }
        viewModel.addressFromIntent?.let { address ->
            if (address.address.isNotEmpty()) {
                etSearch.text = address.address
            }
            if (viewModel.isMapMarked) {
                moveCameraAndAddMarker(
                    LatLng(
                        address.lat!!.toDouble(),
                        address.lng!!.toDouble()
                    )
                    , shouldAnimate = false
                )
            }
        }

    }

    override fun onLocationChanged(location: Location) {
        viewModel.userLocation = LatLng(location.latitude, location.longitude)
        if (!viewModel.isMapMarked && viewModel.locationFoundFlag) {
            viewModel.locationFoundFlag = true
            moveCameraAndAddMarker(
                latLng = viewModel.userLocation!!,
                shouldAnimate = false,
                shouldAddMarker = false
            )
        }
    }

    override fun onStatusChanged(
        provider: String?,
        status: Int,
        extras: Bundle?
    ) {
        println("debug:onStatusChanged: $provider")
    }

    @SuppressLint("MissingPermission")
    override fun onProviderEnabled(provider: String?) {
        //TODO in bayad hatman gps bashe
        println("debug:onProviderEnabled: $provider")
        btnMyLocation.setImage(R.drawable.ic_gps)
        viewModel.isProviderEnabled = true
    }

    override fun onProviderDisabled(provider: String?) {
        //TODO in bayad hatman gps bashe
        println("debug:onProviderDisabled: $provider")
        btnMyLocation.setImage(R.drawable.ic_gps_off)
        viewModel.isProviderEnabled = false
    }


    override fun onMapClick(point: LatLng): Boolean {
        moveCameraAndAddMarker(point)
        viewModel.setSelectedLatLng(point)
        btnSubmit.showProgressBar(true)
        return true
    }

    private fun animateButtonSaveAddress() {
        if (viewModel.isFirstMapClickedFlag) {
            frameCheckSaveAddress.setOnClickListener {
                viewModel.saveAddressTextFromHere = !viewModel.saveAddressTextFromHere
                checkboxSaveAddress.isChecked = viewModel.saveAddressTextFromHere
            }
            checkboxSaveAddress.setOnCheckedChangeListener { _, isChecked ->
                viewModel.saveAddressTextFromHere = isChecked
            }
            frameSave.apply {
                if (visibility != View.VISIBLE) {
                    val firstPlace = translationY
                    translationY += height

                    visibility = View.VISIBLE
                    animate().alpha(1f)
                        .setDuration(500)
                        .translationY(firstPlace)
                        .start()
                }
            }
        }
    }

    override fun beforeRequestPermissionDialogMessage(permissionRequesterFunction: () -> Unit) {
        LocationPermissionDialog(this, R.style.my_alert_dialog) { isGranted, dialog ->
            if (isGranted) {
                dialog.dismiss()
                permissionRequesterFunction.invoke()
            } else {
                toast("برای استفاده از نقشه نیاز به دسترسی به موقعیت شما میباشد", true)
                finish()
            }
        }.show()
    }

    override fun onPermissionDenied() {
        toast("دسترسی داده نشد")
        finish()
    }

    override fun onInternetUnavailable() {
        NoNetworkDialog(this, R.style.my_alert_dialog).show()
    }

    //......................................
    override fun onStart() {
        super.onStart()
        mapView.onStart()
    }

    override fun onResume() {
        super.onResume()
        mapView.onResume()
        MapService.setInternetConnectionListener(this)
    }

    override fun onPause() {
        super.onPause()
        mapView.onPause()
        MapService.removeInternetConnectionListener(this)
    }

    override fun onStop() {
        super.onStop()
        mapView.onStop()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView.onLowMemory()
    }

    override fun onDestroy() {
        super.onDestroy()
        if (viewModel.mapboxMap != null) {
            viewModel.mapboxMap!!.removeOnMapClickListener(this)
        }
        mapView.onDestroy()

        val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        locationManager.removeUpdates(this)
    }

    override fun onSaveInstanceState(outState: Bundle?, outPersistentState: PersistableBundle?) {
        super.onSaveInstanceState(outState, outPersistentState)
        mapView.onSaveInstanceState(outState!!)
    }
}
