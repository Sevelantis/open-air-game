package pt.ua.openairgame.menu

import android.graphics.Bitmap
import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.google.zxing.BarcodeFormat
import com.google.zxing.qrcode.QRCodeWriter
import android.widget.ImageView
import android.widget.Toast
import pt.ua.openairgame.databinding.FragmentMenuBinding


class MenuFragment : Fragment() {

    private lateinit var binding : FragmentMenuBinding


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate<FragmentMenuBinding>(inflater, pt.ua.openairgame.R.layout.fragment_menu, container, false)

        setupButtonsVisibility()

        binding.buttonGameCreate.setOnClickListener{ view : View ->
            view.findNavController().navigate(pt.ua.openairgame.R.id.action_menuFragment_to_createGameFragment)
        }
        binding.buttonGameCurrent.setOnClickListener{ view : View ->
            view.findNavController().navigate(pt.ua.openairgame.R.id.action_menuFragment_to_currentGameFragment)
        }
        binding.buttonGameJoin.setOnClickListener{ view : View ->
            view.findNavController().navigate(pt.ua.openairgame.R.id.action_menuFragment_to_joinGameFragment)
        }
        binding.buttonShowQr.setOnClickListener{ view : View ->
            showQr()
        }
        binding.buttonGameEnd.setOnClickListener{ view: View ->
            // TODO send request to finish the game
        }

        return binding.root
    }

    private fun setupButtonsVisibility(){
        if(isOwner()){
            binding.buttonGameEnd.visibility = View.VISIBLE
        }
        if(isActiveGame()){
            binding.buttonGameCurrent.visibility = View.VISIBLE
            binding.buttonShowQr.visibility = View.VISIBLE
        }
    }

    private fun isOwner(): Boolean{
        return true
    }

    private fun isActiveGame(): Boolean{
        return true
    }

    private fun getQrContent() : String{
        return "Slim Shady"
    }

    private fun showQr(){
        val bitmap = generateQrBitmap(getQrContent())
        val toast = Toast(context)
        val imageViewQr = ImageView(context)
        imageViewQr.setImageBitmap(bitmap)
        toast.view = imageViewQr
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.duration = Toast.LENGTH_LONG
        toast.show()
    }

    private fun generateQrBitmap(content : String): Bitmap? {
        val writer = QRCodeWriter()
        val bitMatrix = writer.encode(content, BarcodeFormat.QR_CODE, 768, 768)
        val width = bitMatrix.width
        val height = bitMatrix.height
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565)
        for (x in 0 until width) {
            for (y in 0 until height) {
                bitmap.setPixel(x, y, if (bitMatrix.get(x, y)) Color.BLACK else Color.WHITE)
            }
        }
        return bitmap
    }

}