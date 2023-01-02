package pt.ua.openairgame.joingame

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.google.zxing.client.android.Intents.Scan.RESULT
import com.google.zxing.integration.android.IntentIntegrator
import org.json.JSONException
import org.json.JSONObject
import pt.ua.openairgame.R
import pt.ua.openairgame.databinding.FragmentJoinGameBinding


class JoinGameFragment : Fragment() {
    private lateinit var binding : FragmentJoinGameBinding
    private lateinit var qrScanIntegrator: IntentIntegrator

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate<FragmentJoinGameBinding>(inflater, R.layout.fragment_join_game, container, false)
        setupScanner()
        qrScanIntegrator.initiateScan()
        return binding.root
    }

    private fun setupScanner() {
        qrScanIntegrator = IntentIntegrator.forSupportFragment(this)
        qrScanIntegrator.setOrientationLocked(false)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        val result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
        if (result != null) {
            val qrResult = result.contents
            if (qrResult == null) {
                Toast.makeText(activity, R.string.result_not_found, Toast.LENGTH_LONG).show()
                view?.findNavController()?.popBackStack()
            } else {
                // toast the user
                Toast.makeText(activity, qrResult, Toast.LENGTH_LONG).show()
                // pop stack
                view?.findNavController()?.popBackStack()
                // TODO send request to join the user to the game
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }

}
