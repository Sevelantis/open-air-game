package pt.ua.openairgame

import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.location.Location
import android.net.Uri
import android.util.Log
import android.view.Display
import android.widget.Toast
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import java.io.FileDescriptor
import java.io.IOException
import kotlin.math.acos
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random


fun uriToBitmap(mContext: Context, selectedFileUri: Uri): Bitmap? {
    try {
        val parcelFileDescriptor = mContext.contentResolver.openFileDescriptor(selectedFileUri, "r")
        val fileDescriptor: FileDescriptor = parcelFileDescriptor!!.fileDescriptor
        val image = BitmapFactory.decodeFileDescriptor(fileDescriptor)
        parcelFileDescriptor.close()
        return image
    } catch (e: IOException) {
        e.printStackTrace()
    }
    return null
}

fun resizeBitmapIcon(bitmap: Bitmap, activity : Activity): BitmapDescriptor {
    val display: Display = activity.windowManager.defaultDisplay
    val width: Int = display.width / 12
    val height: Int = display.height / 12
    return BitmapDescriptorFactory.fromBitmap(Bitmap.createScaledBitmap(bitmap, width, height, false))
}

fun resizeBitmap(bitmap: Bitmap, activity : Activity): Bitmap? {
    val display: Display = activity.windowManager.defaultDisplay
    val width: Int = display.width / 5
    val height: Int = display.height / 5
    return Bitmap.createScaledBitmap(bitmap, width, height, false)
}

fun getNiceRandomMarkerIcon(): BitmapDescriptor {
    val markerColor : Float = when(Random.nextInt(1, 10)){
        1-> BitmapDescriptorFactory.HUE_MAGENTA
        2-> BitmapDescriptorFactory.HUE_AZURE
        3-> BitmapDescriptorFactory.HUE_RED
        4-> BitmapDescriptorFactory.HUE_BLUE
        5-> BitmapDescriptorFactory.HUE_ORANGE
        6-> BitmapDescriptorFactory.HUE_ROSE
        7-> BitmapDescriptorFactory.HUE_VIOLET
        8-> BitmapDescriptorFactory.HUE_YELLOW
        9-> BitmapDescriptorFactory.HUE_GREEN
        else -> BitmapDescriptorFactory.HUE_RED
    }

    return BitmapDescriptorFactory.defaultMarker(markerColor)
}

fun toast(mContext: Context, toastText : String){
    Toast.makeText(mContext, toastText, Toast.LENGTH_SHORT).show()
}

fun distanceLocation(location1: Location, location2: Location): Double {
    val theta = location1.longitude - location2.longitude
    var dist = (sin(degToRad(location1.latitude))
            * sin(degToRad(location2.latitude))
            * cos(degToRad(location1.latitude))
            * cos(degToRad(location2.latitude))
            * cos(degToRad(theta)))
    dist = acos(dist)
    dist = radToDeg(dist)
    dist *= 60.0 * 1.1515
//    var distStr = "$dist Km."
    if (dist < 0) {
        dist *= 1000
        Log.d("distanceLocation", "$dist Meter")
    }
    return dist
}

private fun radToDeg(rad: Double): Double {
    return rad * 180 / Math.PI
}

private fun degToRad(deg: Double): Double {
    return deg * Math.PI / 180.0
}