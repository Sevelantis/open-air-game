package pt.ua.openairgame.model

import android.graphics.Bitmap
import android.location.Location

data class Riddle(var location: Location, var question: String, var answer: String, var bitmapPhotoHint: Bitmap?)