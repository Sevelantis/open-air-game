package pt.ua.openairgame

import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.view.Display
import android.view.Gravity
import android.widget.ImageView
import android.widget.Toast
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import java.io.FileDescriptor
import java.io.IOException
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

fun resizeBitmap(bitmap: Bitmap, activity : Activity, scale:Double=5.0): Bitmap? {
    val display: Display = activity.windowManager.defaultDisplay
    val width = display.width / scale
    val height = display.height / scale
    return Bitmap.createScaledBitmap(bitmap, width.toInt(), height.toInt(), false)
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

fun toast(mContext: Context, toastText : String, length : Int){
    Toast.makeText(mContext, toastText, length).show()
}

fun toastBitmap(mContext: Context, bitmap : Bitmap){
    val toast = Toast(mContext)
    val imageViewQr = ImageView(mContext)
    imageViewQr.setImageBitmap(bitmap)
    toast.view = imageViewQr
    toast.setGravity(Gravity.CENTER, 0, 0);
    toast.duration = Toast.LENGTH_LONG
    toast.show()
}
