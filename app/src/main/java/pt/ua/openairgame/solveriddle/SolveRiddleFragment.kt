package pt.ua.openairgame.solveriddle

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import pt.ua.openairgame.R
import pt.ua.openairgame.databinding.FragmentSolveRiddleBinding
import pt.ua.openairgame.model.GameDataViewModel
import pt.ua.openairgame.toast

class SolveRiddleFragment : Fragment() {

    private lateinit var binding : FragmentSolveRiddleBinding
    private val gameDataViewModel: GameDataViewModel by activityViewModels()
    private var retries : Int = 3
    private var wrongAnswerPenalty : Int = 20
    private var hintPenalty : Int = 75

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate<FragmentSolveRiddleBinding>(inflater, R.layout.fragment_solve_riddle, container, false)

        updateView()
        binding.buttonCheckAnswer.setOnClickListener{ view : View ->
            if (retries == 0){
                // do penalty
                gameDataViewModel.setScore(gameDataViewModel.score!! - wrongAnswerPenalty)
                toast(requireContext(), "Wrong answer! Score: ${gameDataViewModel.score}Moving to the next!")
                // go back to map
            }else{

            }
            retries -= 1
        }

        binding.buttonHint.setOnClickListener{ view : View ->
            photoHint()
        }

        return binding.root
    }

    private fun isAnswerCorrect(): Boolean{
        return true
    }

    private fun wrongAnswer(){

    }

    private fun correctAnswer(){

    }

    private fun photoHint(){
        // present photo
        // do penalty
        gameDataViewModel.setScore(gameDataViewModel.score!! - hintPenalty)
        toast(requireContext(), "You lose $hintPenalty points.")
        // update ui
        binding.textViewScore.text = "Score: ${gameDataViewModel.score.toString()}"
    }

    private fun updateView(){
        binding.tvAddRiddleTitle.text = "Riddle #${gameDataViewModel.currentRiddleIndex}"
        binding.textViewQuestion.text = gameDataViewModel.currentRiddle!!.question
        binding.textViewScore.text = "Score: ${gameDataViewModel.score.toString()}"
        binding.textViewRetries.text = "Retries: $retries"
        binding.textViewHintPenalty.text = "Hint penalty: $hintPenalty points"
    }

}