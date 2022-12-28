package pt.ua.openairgame.model

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class GameData(val name: String, val description: String?) {
    var riddles: ArrayList<Riddle> = ArrayList()
}

class GameDataViewModel : ViewModel() {
    private val mGameData = MutableLiveData<GameData>()

    fun addRiddle(riddle: Riddle) {
        mGameData.value!!.riddles.add(riddle)
    }

    fun removeRiddle(riddle: Riddle) {
        mGameData.value!!.riddles.remove(riddle)
    }

    fun getRiddles(): ArrayList<Riddle> {
        return mGameData.value!!.riddles
    }
}