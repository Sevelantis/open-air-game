package pt.ua.openairgame.solveriddle

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.findNavController
import pt.ua.openairgame.R
import pt.ua.openairgame.databinding.FragmentSolveRiddleBinding
import pt.ua.openairgame.model.GameDataViewModel
import pt.ua.openairgame.model.Riddle
import pt.ua.openairgame.toast
import pt.ua.openairgame.toastBitmap

class SolveRiddleFragment : Fragment() {

    private val TAG = "SolveRiddleFragment"
    private lateinit var binding : FragmentSolveRiddleBinding
    private val gameDataViewModel: GameDataViewModel by activityViewModels()
    private var retries : Int = 3
    private var wrongAnswerPenalty : Int = 20
    private var hintPenalty : Int = 75
    private var goodAnswerReward: Int = 100
    private lateinit var currentRiddle : Riddle

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate<FragmentSolveRiddleBinding>(inflater, R.layout.fragment_solve_riddle, container, false)

        if(gameDataViewModel.currentRiddle != null){
            currentRiddle = gameDataViewModel.currentRiddle!!
        }else{
            Log.d(TAG, "Riddle was null...")
        }
        updateView()

        binding.buttonCheckAnswer.setOnClickListener{ view : View ->
            retries -= 1
            if(isAnswerCorrect()){
                gameDataViewModel.setScore(gameDataViewModel.score!! + goodAnswerReward)
                toast(requireContext(), "Good answer!. You earned $goodAnswerReward points!", Toast.LENGTH_LONG)
            }else{
                gameDataViewModel.setScore(gameDataViewModel.score!! - wrongAnswerPenalty)
                toast(requireContext(), "Wrong answer! You lose $wrongAnswerPenalty points!", Toast.LENGTH_LONG)
            }
            if (retries == 0){
                view.findNavController().popBackStack()
            }
            updateView()
        }

        binding.buttonHint.setOnClickListener{ view : View ->
            photoHint()
        }

        return binding.root
    }

    private fun isAnswerCorrect(): Boolean{
        val userAnswer :String = binding.editTextAnswer.text.toString()
        val goodAnswer :String= currentRiddle.answer
        Log.d(TAG, "user answer: $userAnswer, good answer: $goodAnswer")
        return userAnswer.equals(goodAnswer, ignoreCase = true)
    }

    private fun photoHint(){
        gameDataViewModel.currentRiddle?.bitmapPhotoHint?.let { toastBitmap(requireContext(), it) }
        gameDataViewModel.setScore(gameDataViewModel.score!! - hintPenalty)
        toast(requireContext(), "You lose $hintPenalty points.", Toast.LENGTH_SHORT)
        updateView()
    }

    private fun updateView(){
        binding.tvAddRiddleTitle.text = "Riddle #${gameDataViewModel.currentRiddleIndex}, Score: ${gameDataViewModel.score.toString()}"
        binding.textViewQuestion.text = currentRiddle.question
        binding.textViewRetries.text = "Retries: $retries"
        binding.textViewHintPenalty.text = "Hint penalty: $hintPenalty points"
    }

}