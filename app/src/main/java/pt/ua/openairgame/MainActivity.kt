package pt.ua.openairgame

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import pt.ua.openairgame.model.GameDataViewModel

class MainActivity : AppCompatActivity() {
    private val TAG = "MainActivityty"
    private val gameDataViewModel: GameDataViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        checkPermissions()
        gameDataViewModel.setupFusedLocationProviderClient(this)
    }

    private fun checkPermissions(){
        if (isPermissionGranted()) {
            Log.d(TAG, "Permissions granted")
        } else{
            Log.d(TAG, "Requesting permissions.")
            requestPermission()
        }
    }

    private fun isPermissionGranted(): Boolean {
        return (ActivityCompat.checkSelfPermission(this,
            Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this,
            Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this,
            Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED)

    }

    private fun requestPermission() {
        if(!isPermissionGranted()) {
            ActivityCompat.requestPermissions(this,arrayOf(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.CAMERA), 1001)
        }
    }
}
