package pt.ua.openairgame.model

import android.annotation.SuppressLint
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class GameData(val name: String, val description: String?) {
    var riddles: ArrayList<Riddle> = ArrayList()
}

class GameDataViewModel : ViewModel() {
    private var _gameData = MutableLiveData<GameData>()

    fun setGameData(gd: GameData) {
        _gameData.postValue(gd)
    }

    fun reset() {
        _gameData = MutableLiveData<GameData>()
    }

    val gameData: LiveData<GameData>
        get() = _gameData

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
}