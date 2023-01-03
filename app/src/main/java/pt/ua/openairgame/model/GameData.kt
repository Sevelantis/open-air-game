package pt.ua.openairgame.model

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.Task
import java.time.Duration
import java.time.LocalDateTime

class GameData(val name: String, val description: String?) {
    var riddles: ArrayList<Riddle> = ArrayList()
    var user : User? = null
    var currentRiddleIndex : Int = 1
    var currentRiddle : Riddle? = null
    var score : Int = 500
    var startTime: LocalDateTime? = null
    var endTime: LocalDateTime? = null
    var steps: Int? = null
    var distance: Int? = null
    val calories: Int?
            get() = steps?.times(450)
    var isUserCreatingGame : Boolean = false
    var isGameOwner : Boolean = false
    var hasActiveGame : Boolean = false
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

    fun setUser(email : String){
        _gameData.value?.user = User(email)
    }

    fun setIsUserCreatingGame(value : Boolean){
        _gameData.value?.isUserCreatingGame = value
    }

    fun isUserCreatingGame() : Boolean? {
        return _gameData.value?.isUserCreatingGame
    }

    fun isGameOwner(): Boolean?{
        // TODO send request to check if current user is the game owner
        return _gameData.value?.isGameOwner
    }

    fun setIsGameOwner(value:Boolean){
        _gameData.value?.isGameOwner = value
    }

    fun hasActiveGame(): Boolean?{
        // TODO send request to check if current user has an active game
        return _gameData.value?.hasActiveGame
    }

    fun setHasActiveGame(value:Boolean){
        _gameData.value?.hasActiveGame = value
    }

    fun setScore(score : Int){
        _gameData.value?.score = score
    }

    fun nextRiddle(){
        _gameData.value?.currentRiddleIndex = _gameData.value?.currentRiddleIndex?.plus(1)!!
        _gameData.value?.riddles?.let { setCurrentRiddle(it[currentRiddleIndex!! - 1]) }
        Log.d(TAG, "(nextRiddle): currentRiddle: $currentRiddle")
    }

    private fun setCurrentRiddle(riddle: Riddle){
        _gameData.value?.currentRiddle = riddle
    }

    fun setupFirstRiddleAsCurrent(){
        setCurrentRiddle(_gameData.value?.riddles!![currentRiddleIndex!! - 1])
    }

    fun isLastRiddle() : Boolean{
        Log.d(TAG, "Is last riddle[${riddlesCounter == currentRiddleIndex}]? Riddles counter: $riddlesCounter, currentRiddleIndex: $currentRiddleIndex")
        return riddlesCounter == currentRiddleIndex
    }

    fun isUserAtCurrentRiddleLocation() : Boolean?{
        updateLocation()
        val radiusMeters = 5
        val location1 = location.value
        val location2 = currentRiddle!!.location
        if (location1 != null) {
            val distance = location1.distanceTo(location2)
            if(distance < radiusMeters){

                return true
            }
        }
        return false
    }

    fun getRiddlesTotalDistance() : Float{
        var dist : Float = 0.0f
        val riddles = getRiddles()!!
        for (i in 0 until riddles.size) {
            if(i+1 < riddles.size){
                val loc1 = riddles[i].location
                val loc2 = riddles[i+1].location
                dist += loc1.distanceTo(loc2)
            }
        }
        return dist
    }

    val gameData: LiveData<GameData>
        get() = _gameData

    val location: LiveData<Location>
        get() = _location

//    val gameTime: Duration
//        @RequiresApi(Build.VERSION_CODES.O)
//        get() {
//            val gd: GameData = _gameData.value ?: return Duration.ZERO
//            if (gd.endTime == null) return Duration.ZERO
//            return Duration.between(gd.startTime, gd.endTime)
//        }

    @SuppressLint("NullSafeMutableLiveData")
    fun setStartTime(t: LocalDateTime) {
        val gd: GameData? = _gameData.value
        if (gd != null) {
            gd.startTime = t
            _gameData.postValue(gd)
        }
    }

    @SuppressLint("NullSafeMutableLiveData")
    fun setEndTime(t: LocalDateTime) {
        val gd: GameData? = _gameData.value
        if (gd != null) {
            gd.endTime = t
            _gameData.postValue(gd)
        }
    }

    val riddlesCounter: Int
        get() = _gameData.value?.riddles!!.size

    val currentRiddleIndex : Int?
        get() = _gameData.value?.currentRiddleIndex

    val currentRiddle : Riddle?
        get() = _gameData.value?.currentRiddle

    val score : Int?
        get() = _gameData.value?.score

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
