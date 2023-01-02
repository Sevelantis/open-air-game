package pt.ua.openairgame.model

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.Task

class GameData(val name: String, val description: String?) {
    var riddles: ArrayList<Riddle> = ArrayList()
}

class GameDataViewModel : ViewModel() {
    private val TAG = "GameData"
    private var _gameData = MutableLiveData<GameData>()
    private var _location = MutableLiveData<Location>()
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient


    fun setGameData(gd: GameData) {
        _gameData.postValue(gd)
    }

    fun reset() {
        _gameData = MutableLiveData<GameData>()
    }

    val gameData: LiveData<GameData>
        get() = _gameData

    val location: LiveData<Location>
        get() = _location

    @SuppressLint("NullSafeMutableLiveData")
    fun addRiddle(riddle: Riddle) {
        val gd: GameData? = _gameData.value
        if (gd != null) {
            gd.riddles.add(riddle)
            _gameData.postValue(gd)
        }
    }

    @SuppressLint("NullSafeMutableLiveData")
    fun removeRiddle(riddle: Riddle) {
        val gd: GameData? = _gameData.value
        if (gd != null) {
            gd.riddles.remove(riddle)
            _gameData.postValue(gd)
        }
    }

    fun getRiddles(): ArrayList<Riddle>? {
        return _gameData.value?.riddles
    }

    @SuppressLint("MissingPermission")
    fun updateLocation() {
        val locationTask: Task<Location> = fusedLocationProviderClient.lastLocation
        locationTask.addOnSuccessListener { location: Location? ->
            if (location != null) {
                _location.value = location
            } else {
                Log.d(TAG, "location not OK: ${_location.value} ")
            }
        }
        locationTask.addOnFailureListener { e ->
            Log.e(TAG, "onFailure: " + e.localizedMessage)
        }
    }

    fun setupFusedLocationProviderClient(mContext : Context){
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(mContext)
    }
}
