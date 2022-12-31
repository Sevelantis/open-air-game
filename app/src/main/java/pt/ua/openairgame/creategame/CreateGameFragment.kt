package pt.ua.openairgame.creategame

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.findNavController
import pt.ua.openairgame.R
import pt.ua.openairgame.databinding.FragmentCreateGameBinding
import pt.ua.openairgame.model.GameData
import pt.ua.openairgame.model.GameDataViewModel

class CreateGameFragment : Fragment() {


    private var _gameData: GameData? = null
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

        binding.viewmodel = gameDataViewModel
//        binding.lifecycleOwner = this

        if (gameDataViewModel.gameData.value != null) {
            if (gameDataViewModel.gameData.value!!.riddles.size > 0) {
                binding.buttonSaveGame.isEnabled = true
            }

            for (x in gameDataViewModel.gameData.value!!.riddles) {
                Log.d(x.index.toString(), "onCreateView: ${x.answer}")
            }
        }

        binding.buttonGameNextStep.setOnClickListener { view: View ->
            val name = binding.editTextGameName.text.toString()
            val desc = binding.editTextDescription.text.toString()

            if (name != "" && desc != "") {
                if (gameDataViewModel.gameData.value != null) {
                    if (gameDataViewModel.gameData.value!!.name == name
                        && gameDataViewModel.gameData.value!!.description == desc
                    ) {
                        view.findNavController()
                            .navigate(R.id.action_createGameFragment_to_addRiddleFragment)
                    }
                } else {
                    val gameData = GameData(name, desc)
                    if (_gameData != null) gameData.riddles = _gameData!!.riddles
                    gameDataViewModel.setGameData(gameData)

                    view.findNavController()
                        .navigate(R.id.action_createGameFragment_to_addRiddleFragment)
                }

            }
        }

        binding.buttonSaveGame.setOnClickListener { view: View ->
            gameDataViewModel.reset()
            view.findNavController()
                .navigate(R.id.menuFragment)
        }

        return binding.root
    }
}