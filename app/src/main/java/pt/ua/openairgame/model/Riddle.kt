package pt.ua.openairgame.model

import android.location.Location

data class Riddle(var index: Int, var location: Location, var question: String, var answer: String)