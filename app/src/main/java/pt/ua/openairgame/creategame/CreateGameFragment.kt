package pt.ua.openairgame.creategame

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.findNavController
import pt.ua.openairgame.R
import pt.ua.openairgame.databinding.FragmentCreateGameBinding
import pt.ua.openairgame.model.GameDataViewModel

class CreateGameFragment : Fragment() {

//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setFragmentResultListener("requestKey") { requestKey, bundle ->
//            val result: Riddle = bundle.getParcelable<Riddle>("bundleKey")
//
//            Log.d("Tag", "onCreate: ${result!!.answer}, ${result!!.location.longitude}")
//            // Do something with the result
//        }
//    }

    private val gameDataViewModel: GameDataViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = DataBindingUtil.inflate<FragmentCreateGameBinding>(
            inflater,
            R.layout.fragment_create_game,
            container,
            false
        )

//        Log.d("TAG", "onCreateView: ${gameDataViewModel.name()}")

        binding.buttonGameNextStep.setOnClickListener { view: View ->
            val name = binding.editTextGameName.text.toString()
            val desc = binding.editTextDescription.text.toString()

            if (name != "" && desc != "") {
                view.findNavController().navigate(R.id.action_createGameFragment_to_addRiddleFragment)
            }
        }

        return binding.root
    }
}