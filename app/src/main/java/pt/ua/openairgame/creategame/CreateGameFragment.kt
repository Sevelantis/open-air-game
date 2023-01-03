package pt.ua.openairgame.creategame

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
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
    private lateinit var binding : FragmentCreateGameBinding
    private lateinit var riddleListAdapter : RiddleListAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate<FragmentCreateGameBinding>(inflater,R.layout.fragment_create_game, container, false)

        setupHideKeyboard()
        binding.viewModel = gameDataViewModel
        riddleListAdapter = RiddleListAdapter()
        binding.recyclerViewRiddleList.apply {
            adapter = riddleListAdapter
            layoutManager = LinearLayoutManager(context)
        }
        gameDataViewModel.gameData.observe(viewLifecycleOwner, Observer {
            it?.let {
                riddleListAdapter.data = it.riddles
            }
        })
        setupSwipeToDeleteCallback()
        setupGameSaveButton()
        binding.buttonGameNextStep.setOnClickListener { view: View ->
            nextStep(view)
        }
        binding.buttonGameSave.setOnClickListener { view: View ->
            saveGame(view)
        }
        binding.buttonGameShowMap.setOnClickListener { view : View ->
            view.findNavController().navigate(R.id.currentGameFragment)
        }
        return binding.root
    }

    private fun setupSwipeToDeleteCallback(){
        val swipeToDeleteCallback = object : SwipeToDeleteCallback(requireContext()){
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                if(direction == ItemTouchHelper.LEFT){
                    val position = viewHolder.adapterPosition
                    riddleListAdapter.removeAt(position)
                    Log.d(TAG, "riddles total: ${gameDataViewModel.getRiddles()?.size}")
                }
            }
        }
        val itemTouchHelper = ItemTouchHelper(swipeToDeleteCallback)
        itemTouchHelper.attachToRecyclerView(binding.recyclerViewRiddleList)
    }

    private fun setupHideKeyboard(){
        binding.editTextDescription.setOnEditorActionListener(TextView.OnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                // hide keyboard after confirming the  game description
                val imm: InputMethodManager = v.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(v.windowToken, 0)
                return@OnEditorActionListener true
            }
            false
        })
    }

    private fun setupGameSaveButton(){
        if (gameDataViewModel.gameData.value != null) {
            if (gameDataViewModel.gameData.value!!.riddles.size > 0) {
                binding.buttonGameSave.isEnabled = true
            }
        }
    }

    private fun nextStep(view: View){
        val name = binding.editTextGameName.text.toString()
        val desc = binding.editTextDescription.text.toString()

        if (name != "" && desc != "") {
            if (gameDataViewModel.gameData.value != null) {
                if (gameDataViewModel.gameData.value!!.name == name
                    && gameDataViewModel.gameData.value!!.description == desc
                ) {
                    view.findNavController().navigate(R.id.action_createGameFragment_to_addRiddleFragment)
                }
            } else {
                val gameData = GameData(name, desc)
                if (_gameData != null) {
                    gameData.riddles = _gameData!!.riddles
                }
                gameDataViewModel.setGameData(gameData)
                Log.d(TAG, "Saved game Riddles: ${gameDataViewModel.getRiddles()}")
                view.findNavController().navigate(R.id.action_createGameFragment_to_addRiddleFragment)
            }

        }
    }

    private fun saveGame(view: View){
        // TODO send request to create an active game, where the owner of the game is the current user
        gameDataViewModel.setIsUserCreatingGame(false)
        gameDataViewModel.setIsGameOwner(true)
        gameDataViewModel.setHasActiveGame(true)
        gameDataViewModel.setupFirstRiddleAsCurrent()
        view.findNavController().popBackStack()
    }

}
