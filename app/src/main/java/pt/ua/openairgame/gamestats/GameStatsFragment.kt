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
            gameDataViewModel.setStartTime(LocalDateTime.now().plusMinutes(-23).plusHours(-2))

            if (gameData.endTime == null) {
                gameDataViewModel.setEndTime(LocalDateTime.now())
            }

            val gameTime = gameDataViewModel.gameTime

            val scoreText = "Score: ${gameData.score}"
            val timeText = "Time: ${gameTime.toHoursPart()}h ${gameTime.toMinutesPart()}m ${gameTime.toSecondsPart()}s"
            val riddlesText = "Finished riddles: ${gameData.riddles.size}"
            val stepsText = "Steps: ${gameData.steps}"
            val distanceText = "Distance: ${gameData.distance?.div(1000)}km ${gameData.distance?.mod(1000)}m: "
            val caloriesText = "Calories burned: ${gameData.calories} kcal"

            binding.tvStatGameTime.text = timeText
            binding.tvStatPointsEarned.text = scoreText
            binding.tvStatRiddlesFinished.text = riddlesText
            binding.tvStatSteps.text = stepsText
            binding.tvStatCalories.text = caloriesText
            binding.tvDistance.text = distanceText
        }

        binding.buttonCloseStats.setOnClickListener { view : View ->
            view.findNavController().navigate(R.id.menuFragment)
        }

        return binding.root
    }

}