package pt.ua.openairgame

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import java.io.FileDescriptor
import java.io.IOException

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
