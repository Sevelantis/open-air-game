package pt.ua.openairgame.creategame

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.FileProvider
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.findNavController
import pt.ua.openairgame.R
import pt.ua.openairgame.databinding.FragmentAddRiddleBinding
import pt.ua.openairgame.model.GameDataViewModel
import pt.ua.openairgame.model.Riddle
import pt.ua.openairgame.uriToBitmap
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*


class AddRiddleFragment : Fragment() {

    private val TAG = "AddRiddleFragment"
    private val CAMERA_REQUEST = 1888
    private lateinit var currentPhotoPath: String
    private lateinit var photoURI: Uri
    private var bitmapPhotoHint: Bitmap? = null
    private lateinit var imageViewPhoto : ImageView
    private lateinit var binding : FragmentAddRiddleBinding

    private val gameDataViewModel: GameDataViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate<FragmentAddRiddleBinding>(inflater,R.layout.fragment_add_riddle, container, false)
        imageViewPhoto = binding.imageViewPhotoHint

        binding.buttonSaveRiddle.setOnClickListener { view: View ->
            saveRiddle(view)
        }

        binding.buttonAddHint.setOnClickListener {view : View ->
            dispatchTakePictureIntent()
        }
        setupHideKeyboard()

        return binding.root
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        // this code runs whenever make photo intent activity exits
        if (requestCode == CAMERA_REQUEST && resultCode == Activity.RESULT_OK) {
            bitmapPhotoHint = uriToBitmap(requireContext(), photoURI)
            Log.d(TAG, "bitmap: ${bitmapPhotoHint.toString()}")
            Log.d(TAG, "photoUri: ${photoURI}")
            Log.d(TAG, "currentPhotoPath: ${currentPhotoPath}")
            imageViewPhoto.setImageBitmap(bitmapPhotoHint)
        }
    }

    override fun onStart() {
        super.onStart()
        gameDataViewModel.updateLocation()
        Log.d(TAG, "Riddle location: ${gameDataViewModel.location.value.toString()}")
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
        Log.d(TAG, storageDir.toString())
        return File.createTempFile(
            "JPEG_${timeStamp}_", /* prefix */
            ".jpg", /* suffix */
            storageDir /* directory */
        ).apply {
            currentPhotoPath = absolutePath
        }
    }

    private fun saveRiddle(view: View){
        val riddleText = binding.editTextRiddle.text.toString()
        val answer = binding.editTextAnswer.text.toString()

        if (riddleText != "" && answer != "") {
            gameDataViewModel.updateLocation()
            val location = gameDataViewModel.location.value
            Log.d(TAG, "Riddle location on save: $location")
            val riddle = location?.let { Riddle(it, riddleText, answer, bitmapPhotoHint) }

            if (riddle != null) {
                gameDataViewModel.addRiddle(riddle)
            }

            view.findNavController().navigate(R.id.action_addRiddleFragment_to_createGameFragment)
        }
    }

    private fun setupHideKeyboard(){
        binding.editTextAnswer.setOnEditorActionListener(TextView.OnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                // hide keyboard after confirming the riddle answer
                val imm: InputMethodManager =
                    v.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(v.windowToken, 0)
                return@OnEditorActionListener true
            }
            false
        })
    }

}
