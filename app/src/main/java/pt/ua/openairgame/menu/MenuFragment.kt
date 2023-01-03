package pt.ua.openairgame.menu

import android.graphics.Bitmap
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.google.zxing.BarcodeFormat
import com.google.zxing.qrcode.QRCodeWriter
import androidx.fragment.app.activityViewModels
import pt.ua.openairgame.databinding.FragmentMenuBinding
import pt.ua.openairgame.model.GameDataViewModel
import pt.ua.openairgame.toastBitmap


class MenuFragment : Fragment() {

    private lateinit var binding : FragmentMenuBinding
    private val gameDataViewModel: GameDataViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate<FragmentMenuBinding>(inflater, pt.ua.openairgame.R.layout.fragment_menu, container, false)

        setupButtonsVisibility()
        binding.buttonGameCreate.setOnClickListener{ view : View ->
            gameDataViewModel.reset()
            gameDataViewModel.setIsUserCreatingGame(true)
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
            // TODO send request to finish the game: set active to false, deregister all affected users from the game
        }
        binding.buttonSwitchRole.setOnClickListener { view : View ->
            switchRole()
        }

        return binding.root
    }

    private fun switchRole(){
        var btnText = ""
        if(gameDataViewModel.isGameOwner() == true){
            gameDataViewModel.setGameOwner(false)
            btnText = "Become Owner"
        }else{
            gameDataViewModel.setGameOwner(true)
            btnText = "Become Player"
        }
        binding.buttonSwitchRole.text = btnText
    }

    private fun setupButtonsVisibility(){
        if(gameDataViewModel.isGameOwner() == true){
            binding.buttonGameEnd.visibility = View.VISIBLE
            binding.buttonSwitchRole.visibility = View.VISIBLE
        }
        if(gameDataViewModel.hasActiveGame() == true){
            binding.buttonGameCurrent.visibility = View.VISIBLE
            binding.buttonShowQr.visibility = View.VISIBLE
            binding.buttonGameCreate.visibility = View.INVISIBLE
        }
    }

    private fun getQrContent() : String{
        // TODO send request to obtain game ID
        return "Slim Shady"
    }

    private fun showQr(){
        generateQrBitmap(getQrContent())?.let { toastBitmap(requireContext(), it) }
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