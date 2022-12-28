package pt.ua.openairgame.currentgame

import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.tasks.Task
import pt.ua.openairgame.databinding.FragmentCurrentGameBinding
import pt.ua.openairgame.model.GameData


class CurrentGameFragment : Fragment(), OnMapReadyCallback {

    private lateinit var googleMap: GoogleMap
    private val TAG = "CurrentGameFragment"
    private var LOCATION_REQUEST_CODE = 10001
    private var fusedLocationProviderClient: FusedLocationProviderClient? = null
    private var currLocation: Location? = null

    private lateinit var gameData: GameData

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = DataBindingUtil.inflate<FragmentCurrentGameBinding>(inflater, pt.ua.openairgame.R.layout.fragment_current_game, container, false)

        val mapFragment = childFragmentManager.findFragmentById(pt.ua.openairgame.R.id.fragmentCurrentGameMap) as SupportMapFragment?
        mapFragment?.getMapAsync(this)

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireActivity())

        return binding.root
    }

    @SuppressLint("MissingPermission")
    override fun onMapReady(googleMap: GoogleMap) {
        this.googleMap = googleMap
        googleMap.isMyLocationEnabled = true

        loadGame()

//        val riddles = gameData.riddles
//
//        for (r: Riddle in riddles) {
//            val latLng = LatLng(r.location.latitude, r.location.longitude)
//            googleMap.addMarker(
//                MarkerOptions()
//                    .position(latLng)
//                    .title(r.index.toString())
//            )
//        }
    }

    override fun onStart() {
        super.onStart()
        if (ContextCompat.checkSelfPermission(
                requireActivity(),
                ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            getLastLocation()
        } else {
            askLocationPermission()
        }
    }

    @SuppressLint("MissingPermission")
    private fun getLastLocation() {
        val locationTask: Task<Location> = fusedLocationProviderClient!!.lastLocation
        locationTask.addOnSuccessListener { location: Location? ->
            if (location != null) {
                //We have a location
                Log.d(TAG, "onSuccess: $location")
                Log.d(TAG, "onSuccess: " + location.latitude)
                Log.d(TAG, "onSuccess: " + location.longitude)

                currLocation = location
                setCameraPosition()
            } else {
                Log.d(TAG, "onSuccess: Location was null...")
            }
        }
        locationTask.addOnFailureListener { e ->
            Log.e(
                TAG,
                "onFailure: " + e.localizedMessage
            )
        }
    }

    private fun askLocationPermission() {
        if (ContextCompat.checkSelfPermission(
                requireActivity(),
                ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    requireActivity(),
                    ACCESS_FINE_LOCATION
                )
            ) {
                Log.d(TAG, "askLocationPermission: you should show an alert dialog...")
                ActivityCompat.requestPermissions(
                    requireActivity(),
                    arrayOf(ACCESS_FINE_LOCATION),
                    LOCATION_REQUEST_CODE
                )
            } else {
                ActivityCompat.requestPermissions(
                    requireActivity(),
                    arrayOf(ACCESS_FINE_LOCATION),
                    LOCATION_REQUEST_CODE
                )
            }
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String?>,
        grantResults: IntArray
    ) {
        if (requestCode == LOCATION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted
                getLastLocation()
            } else {
                //Permission not granted
            }
        }
    }

    private fun setCameraPosition() {
        if (currLocation != null) {
            val latitude: Double = currLocation!!.latitude
            val longitude: Double = currLocation!!.longitude
            val camPos = CameraPosition.Builder()
                .target(LatLng(latitude, longitude))
                .zoom(18f)
                .bearing(currLocation!!.bearing)
                .build()
            val camUpd3 = CameraUpdateFactory.newCameraPosition(camPos)

            googleMap.animateCamera(camUpd3)
        }
    }

    fun loadGame() {
//        gameData = ?

    }
}