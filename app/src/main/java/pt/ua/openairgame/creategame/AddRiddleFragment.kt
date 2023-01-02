package pt.ua.openairgame.creategame

import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.findNavController
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.Task
import pt.ua.openairgame.R
import pt.ua.openairgame.databinding.FragmentAddRiddleBinding
import pt.ua.openairgame.model.GameDataViewModel
import pt.ua.openairgame.model.Riddle
import java.io.File
import java.io.FileDescriptor
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*


class AddRiddleFragment : Fragment() {

    private val TAG = "AddRiddleFragment"
    private var LOCATION_REQUEST_CODE = 10001
    private var fusedLocationProviderClient: FusedLocationProviderClient? = null
    private var currLocation: Location? = null
    private val CAMERA_REQUEST = 1888
    private val MY_CAMERA_PERMISSION_CODE = 100
    private lateinit var currentPhotoPath: String
    private lateinit var photoURI: Uri
    private var bitmapPhotoHint: Bitmap? = null
    private lateinit var imageViewPhoto : ImageView

    private val gameDataViewModel: GameDataViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = DataBindingUtil.inflate<FragmentAddRiddleBinding>(
            inflater,
            R.layout.fragment_add_riddle,
            container,
            false
        )

        binding.buttonSaveRiddle.setOnClickListener { view: View ->
            val riddleText = binding.editTextRiddle.text.toString()
            val answer = binding.editTextAnswer.text.toString()

            if (riddleText != "" && answer != "") {
                val location = Location("point A")
                location.longitude = 0.0
                location.latitude = 0.0
                val riddle = Riddle(location, riddleText, answer, bitmapPhotoHint)

                gameDataViewModel.addRiddle(riddle)

                view.findNavController()
                    .navigate(R.id.action_addRiddleFragment_to_createGameFragment)
            }
        }

        imageViewPhoto = binding.imageViewPhotoHint
        binding.buttonAddHint.setOnClickListener {view : View ->
            dispatchTakePictureIntent()
        }

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireActivity())

        return binding.root
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == CAMERA_REQUEST && resultCode == Activity.RESULT_OK) {
            bitmapPhotoHint = uriToBitmap(photoURI)
            Log.d(TAG, "bitmap: ${bitmapPhotoHint.toString()}")
            Log.d(TAG, "photoUri: ${photoURI}")
            Log.d(TAG, "currentPhotoPath: ${currentPhotoPath}")
            imageViewPhoto.setImageBitmap(bitmapPhotoHint)
        }
    }

    private fun dispatchTakePictureIntent() {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            takePictureIntent.resolveActivity(requireContext().packageManager)?.also {
                val photoFile: File? = try {
                    createImageFile()
                } catch (ex: IOException) {
                    null
                }
                // Continue only if the File was successfully created
                photoFile?.also {
                    photoURI = FileProvider.getUriForFile(
                        requireContext(),
                        "com.example.android.fileprovider",
                        it
                    )
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                    startActivityForResult(takePictureIntent, CAMERA_REQUEST)
                }
            }
        }
    }

    @Throws(IOException::class)
    private fun createImageFile(): File {
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val storageDir: File = requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES)!!
        Log.d("AddRiddleFragmet", storageDir.toString())
        return File.createTempFile(
            "JPEG_${timeStamp}_", /* prefix */
            ".jpg", /* suffix */
            storageDir /* directory */
        ).apply {
            currentPhotoPath = absolutePath
        }
    }

    override fun onStart() {
        super.onStart()
        if (ContextCompat.checkSelfPermission(
                requireActivity(),
                ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            getLastLocation()
        } else {
            askLocationPermission()
        }
    }

    private fun uriToBitmap(selectedFileUri: Uri): Bitmap? {
        try {
            val parcelFileDescriptor = requireContext().contentResolver.openFileDescriptor(selectedFileUri, "r")
            val fileDescriptor: FileDescriptor = parcelFileDescriptor!!.fileDescriptor
            val image = BitmapFactory.decodeFileDescriptor(fileDescriptor)
            parcelFileDescriptor.close()
            return image
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return null
    }

    @SuppressLint("MissingPermission")
    private fun getLastLocation() {
        val locationTask: Task<Location> = fusedLocationProviderClient!!.lastLocation
        locationTask.addOnSuccessListener { location: Location? ->
            if (location != null) {
                currLocation = location
            } else {
                Log.d(TAG, "onSuccess: Location was null...")
            }
        }
        locationTask.addOnFailureListener { e ->
            Log.e(
                TAG,
                "onFailure: " + e.localizedMessage
            )
        }
    }

    private fun askLocationPermission() {
        if (ContextCompat.checkSelfPermission(
                requireActivity(),
                ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    requireActivity(),
                    ACCESS_FINE_LOCATION
                )
            ) {
                Log.d(TAG, "askLocationPermission: you should show an alert dialog...")
                ActivityCompat.requestPermissions(
                    requireActivity(),
                    arrayOf(ACCESS_FINE_LOCATION),
                    LOCATION_REQUEST_CODE
                )
            } else {
                ActivityCompat.requestPermissions(
                    requireActivity(),
                    arrayOf(ACCESS_FINE_LOCATION),
                    LOCATION_REQUEST_CODE
                )
            }
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String?>,
        grantResults: IntArray
    ) {
        if (requestCode == LOCATION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted
                getLastLocation()
            } else {
                //Permission not granted
            }
        }
    }
}