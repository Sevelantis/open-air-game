package pt.ua.openairgame.gamestats

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.findNavController
import pt.ua.openairgame.R
import pt.ua.openairgame.databinding.FragmentGameStatsBinding
import pt.ua.openairgame.model.GameDataViewModel
import java.time.LocalDateTime

class GameStatsFragment : Fragment() {
    private lateinit var binding: FragmentGameStatsBinding
    private val gameDataViewModel: GameDataViewModel by activityViewModels()

    @SuppressLint("NewApi")
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate<FragmentGameStatsBinding>(
            inflater,
            R.layout.fragment_game_stats,
            container,
            false
        )

        val gameData = gameDataViewModel.gameData.value

        if (gameData != null) {
            if (gameData.endTime == null) {
                gameDataViewModel.setEndTime(LocalDateTime.now())
            }

//            val gameTime = gameDataViewModel.gameTime

            val dist = gameDataViewModel.getRiddlesTotalDistance()
            val steps = (dist * 1.31).toInt()
            val calories = steps / 20
            val scoreText = "Score: ${gameData.score}"
//            val timeText = "Time: ${gameTime.toHours()}h ${((gameTime.seconds % (60 * 60)) / 60).toInt()}m ${(gameTime.seconds % 60).toInt()}s"
            val riddlesText = "Finished riddles: ${gameData.riddles.size}"
            val stepsText = "Steps: ${steps}"
            val distanceText = "Distance: ${dist} [m]"
            val caloriesText = "Calories burned: ${calories} kcal"

//            binding.tvStatGameTime.text = timeText
            binding.tvStatPointsEarned.text = scoreText
            binding.tvStatRiddlesFinished.text = riddlesText
            binding.tvStatSteps.text = stepsText
            binding.tvStatCalories.text = caloriesText
            binding.tvDistance.text = distanceText
        }

        binding.buttonCloseStats.setOnClickListener { view : View ->
            finishGame(view)
        }

        return binding.root
    }

    private fun finishGame(view : View){
        gameDataViewModel.reset()
        gameDataViewModel.setHasActiveGame(false)
        gameDataViewModel.setIsGameOwner(false)
        view.findNavController().popBackStack(R.id.createGameFragment, true)
    }

}