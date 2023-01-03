package pt.ua.openairgame.currentgame

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.findNavController
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.GoogleMap.InfoWindowAdapter
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import pt.ua.openairgame.databinding.FragmentCurrentGameBinding
import pt.ua.openairgame.getNiceRandomMarkerIcon
import pt.ua.openairgame.model.GameData
import pt.ua.openairgame.model.GameDataViewModel
import pt.ua.openairgame.resizeBitmap
import pt.ua.openairgame.toast
import java.util.*
import kotlin.math.sqrt


class CurrentGameFragment : Fragment(), OnMapReadyCallback {

    private lateinit var googleMap: GoogleMap
    private val TAG = "CurrentGameFragment"

    private var sensorManager: SensorManager? = null
    private var acceleration = 0f
    private var currentAcceleration = 0f
    private var lastAcceleration = 0f
    private var isCameraSet: Boolean = false
    private var isShakeDetected : Boolean = false

    private val gameDataViewModel: GameDataViewModel by activityViewModels()
    private var gameData: GameData? = null
    private lateinit var binding: FragmentCurrentGameBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sensorManager = requireActivity().getSystemService(Context.SENSOR_SERVICE) as SensorManager
        isShakeDetected = false
        Objects.requireNonNull(sensorManager)?.registerListener(sensorListener, sensorManager!!
            .getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL)

        acceleration = 10f
        currentAcceleration = SensorManager.GRAVITY_EARTH
        lastAcceleration = SensorManager.GRAVITY_EARTH
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate<FragmentCurrentGameBinding>(inflater, pt.ua.openairgame.R.layout.fragment_current_game, container, false)

        val mapFragment = childFragmentManager.findFragmentById(pt.ua.openairgame.R.id.fragmentCurrentGameMap) as SupportMapFragment?
        mapFragment?.getMapAsync(this)
        return binding.root
    }

    @SuppressLint("MissingPermission")
    override fun onMapReady(googleMap: GoogleMap) {
        this.googleMap = googleMap
        googleMap.isMyLocationEnabled = true
        googleMap.mapType = GoogleMap.MAP_TYPE_SATELLITE
        googleMap.setMaxZoomPreference(30f)
        setupMarkersInfoContentView()
        gameDataViewModel.location.observe(viewLifecycleOwner) {
            if (!isCameraSet){
                setupCameraPosition()
                Log.d(TAG, "Setting map for location: $it")
            }else{
                Log.d(TAG, "Map was already set for location $it")
            }
        }
        gameDataViewModel.updateLocation()
        loadGameData()
        putRiddleMarkersOnTheMap()
    }

    private fun loadGameData() {
        // TODO request to get active game data
        gameData = gameDataViewModel.gameData.value
        if (gameData != null){
            gameDataViewModel.setupFirstRiddleAsCurrent()
        }
    }

    private fun putRiddleMarkersOnTheMap(){
        // update markers depending on the role
        if (gameData == null){
            Log.d(TAG, "addRiddleMarkersToMap() -> gameData has not been initialized")
            return
        }
        var cnt = 1
        for (riddle in gameData?.riddles!!) {
            val position = LatLng(riddle.location.latitude, riddle.location.longitude)
            val title = "Riddle #$cnt"
            val snippet = "Question: ${riddle.question}\nAnswer: ${riddle.answer}"
            var markerOptions = MarkerOptions()
                .position(position)
                .title(title)
                .snippet(snippet)
                .icon(getNiceRandomMarkerIcon())

            if(gameDataViewModel.isUserCreatingGame() == true || gameDataViewModel.isGameOwner()){
                Log.d(TAG, "Marker[$cnt] added (visible): $position," +
                        "(User is creating game or is the game owner. Allowing to see all the markers)")
            }else if(cnt == gameDataViewModel.currentRiddleIndex){
                Log.d(TAG, "Marker[$cnt] added (visible): $position")
            }else{
                markerOptions.visible(false)
                Log.d(TAG, "Marker[$cnt] added (invisible): $position")
            }
            googleMap.addMarker(markerOptions)
            cnt += 1
        }
    }

    private fun unlockNextRiddle(){
        putRiddleMarkersOnTheMap()
        gameDataViewModel.nextRiddle()
        // check if it is the last riddle
        if (gameDataViewModel.isLastRiddle()){
            toast(requireContext(), "Congratulations! You finished the game!")
            // TODO send request to deregister current user from the game
            // go to GameStats view
        }else{
            toast(requireContext(), "Congratulations! You are now at Riddle #${gameDataViewModel.currentRiddleIndex}!")
        }
    }

    private fun setupMarkersInfoContentView(){
        googleMap.setInfoWindowAdapter(object : InfoWindowAdapter {
            override fun getInfoWindow(marker: Marker): View? {
                return null
            }

            override fun getInfoContents(marker: Marker): View? {
                val info = LinearLayout(requireContext())
                info.orientation = LinearLayout.VERTICAL
                val title = TextView(requireContext())
                title.setTextColor(Color.BLACK)
                title.gravity = Gravity.CENTER
                title.setTypeface(null, Typeface.BOLD)
                title.text = marker.title
                val snippet = TextView(requireContext())
                snippet.setTextColor(Color.GRAY)
                snippet.text = marker.snippet

                var cnt = 1
                val photoHint = ImageView(requireContext())
                for (riddle in gameDataViewModel.getRiddles()!!) {
                    if(marker.title == "Riddle #${cnt}"){
                        Log.d(TAG, "Adding marker photo hint preview for Riddle: ${marker.title}")
                        snippet.text = marker.snippet + "\nPhoto Hint:"
                        photoHint.setImageBitmap(riddle.bitmapPhotoHint?.let { resizeBitmap(it, requireActivity()) })
                    }
                    cnt += 1
                }
                info.addView(title)
                info.addView(snippet)
                info.addView(photoHint)
                return info
            }
        })
    }

    private fun setupCameraPosition() {
        val location = gameDataViewModel.location.value
        Log.d(TAG, "Setting camera position for location: ${location}")
        if (location != null) {
            val latitude: Double = location.latitude
            val longitude: Double = location.longitude
            val camPos = CameraPosition.Builder()
                .target(LatLng(latitude, longitude))
                .zoom(22f)
                .bearing(location.bearing)
                .build()
            val camUpd3 = CameraUpdateFactory.newCameraPosition(camPos)

            Log.d(TAG, "Setting camera position: $camPos")
            googleMap.animateCamera(camUpd3)
            isCameraSet = true
        }
    }


    private val sensorListener: SensorEventListener = object : SensorEventListener {
        override fun onSensorChanged(event: SensorEvent) {

            val x = event.values[0]
            val y = event.values[1]
            val z = event.values[2]
            lastAcceleration = currentAcceleration

            currentAcceleration = sqrt((x * x + y * y + z * z).toDouble()).toFloat()
            val delta: Float = currentAcceleration - lastAcceleration
            acceleration = acceleration * 0.9f + delta

            // Display a Toast message if acceleration value is over 12
            if (acceleration > 12 && !isShakeDetected && gameDataViewModel.isUserCreatingGame() == false ) {
                isShakeDetected = true

                if(gameDataViewModel.isUserAtCurrentRiddleLocation() == true){
                    toast(requireContext(), "Congratulations! You can now solve: Riddle #${gameDataViewModel.currentRiddleIndex}")
                    view?.findNavController()?.navigate(pt.ua.openairgame.R.id.action_currentGameFragment_to_solveRiddleFragment)
                }else{
                    isShakeDetected = false
                    toast(requireContext(), "There is no riddle in your location!")
                }
            }
        }
        override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {}
    }

    override fun onResume() {
        sensorManager?.registerListener(sensorListener, sensorManager!!.getDefaultSensor(
            Sensor .TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL
        )
        super.onResume()
    }

    override fun onPause() {
        sensorManager!!.unregisterListener(sensorListener)
        super.onPause()
    }
}
