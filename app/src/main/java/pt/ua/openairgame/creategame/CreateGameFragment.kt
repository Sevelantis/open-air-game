package pt.ua.openairgame.creategame

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import pt.ua.openairgame.R
import pt.ua.openairgame.creategame.swipetodelete.SwipeToDeleteCallback
import pt.ua.openairgame.databinding.FragmentCreateGameBinding
import pt.ua.openairgame.model.GameData
import pt.ua.openairgame.model.GameDataViewModel

class CreateGameFragment : Fragment() {
    private val TAG = "CreateGameFragment"
    private var _gameData: GameData? = null
    private val gameDataViewModel: GameDataViewModel by activityViewModels()
    private lateinit var  binding : FragmentCreateGameBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate<FragmentCreateGameBinding>(inflater,R.layout.fragment_create_game, container, false)

        binding.viewModel = gameDataViewModel
        val riddleListAdapter = RiddleListAdapter()
        binding.recyclerViewRiddleList.apply {
            adapter = riddleListAdapter
            layoutManager = LinearLayoutManager(context)
        }

        val swipeToDeleteCallback = object : SwipeToDeleteCallback(requireContext()){
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                riddleListAdapter.removeAt(position)
                Log.d(TAG, "riddles total: ${gameDataViewModel.getRiddles()?.size}")
            }
        }
        val itemTouchHelper = ItemTouchHelper(swipeToDeleteCallback)
        itemTouchHelper.attachToRecyclerView(binding.recyclerViewRiddleList)

        gameDataViewModel.gameData.observe(viewLifecycleOwner, Observer {
            it?.let {
                riddleListAdapter.data = it.riddles
            }
        })

//        binding.lifecycleOwner = this

        setupGameSaveButton()

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

        binding.buttonGameSave.setOnClickListener { view: View ->
            gameDataViewModel.reset()
            view.findNavController().navigate(R.id.menuFragment)
        }

        return binding.root
    }

    private fun setupGameSaveButton(){
        if (gameDataViewModel.gameData.value != null) {
            if (gameDataViewModel.gameData.value!!.riddles.size > 0) {
                binding.buttonGameSave.isEnabled = true
            }
        }
    }
}