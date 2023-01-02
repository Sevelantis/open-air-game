package pt.ua.openairgame.currentgame

import android.annotation.SuppressLint
import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import pt.ua.openairgame.databinding.FragmentCurrentGameBinding
import pt.ua.openairgame.model.GameData
import pt.ua.openairgame.model.GameDataViewModel
import java.util.*
import kotlin.math.sqrt


class CurrentGameFragment : Fragment(), OnMapReadyCallback {

    private lateinit var googleMap: GoogleMap
    private val TAG = "CurrentGameFragment"

    private var sensorManager: SensorManager? = null
    private var acceleration = 0f
    private var currentAcceleration = 0f
    private var lastAcceleration = 0f
    private var isMapSet: Boolean = false

    private val gameDataViewModel: GameDataViewModel by activityViewModels()
    private var gameData: GameData? = null
    private lateinit var binding: FragmentCurrentGameBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sensorManager = requireActivity().getSystemService(Context.SENSOR_SERVICE) as SensorManager

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

    override fun onMapReady(googleMap: GoogleMap) {
        this.googleMap = googleMap

        loadGameData()
        addRiddleMarkersToMap()
    }

    override fun onStart() {
        super.onStart()
        gameDataViewModel.location.observe(viewLifecycleOwner) {
            if (!isMapSet){
                setupMap()
                Log.d(TAG, "Setting map for location: $it")
            }else{
                Log.d(TAG, "Map was already set for location $it")
            }
        }
        gameDataViewModel.updateLocation()
    }

    private fun loadGameData() {
        // TODO request to get active game data
        gameData = gameDataViewModel.gameData.value
    }

    private fun addRiddleMarkersToMap(){
        if (gameData == null){
            Log.d(TAG, "addRiddleMarkersToMap() -> gameData has not been initialized")
            return
        }
        var cnt = 1
        for (riddle in gameData?.riddles!!) {
            val latLng = LatLng(riddle.location.latitude, riddle.location.longitude)
            googleMap.addMarker(MarkerOptions().position(latLng).title(cnt.toString()))
            Log.d(TAG, "marker[$cnt] added: $latLng")
            cnt += 1
        }
    }


    @SuppressLint("MissingPermission")
    private fun setupMap(){
        googleMap.mapType = GoogleMap.MAP_TYPE_SATELLITE
        googleMap.isMyLocationEnabled = true
        setupCameraPosition()
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
            isMapSet = true
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

            // Display a Toast message if
            // acceleration value is over 12
            if (acceleration > 12) {
                Toast.makeText(context, "Shake event detected", Toast.LENGTH_SHORT).show()
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
