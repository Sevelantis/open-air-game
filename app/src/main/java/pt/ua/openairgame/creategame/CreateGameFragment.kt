package pt.ua.openairgame.creategame

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import pt.ua.openairgame.R
import pt.ua.openairgame.databinding.FragmentCreateGameBinding

class CreateGameFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = DataBindingUtil.inflate<FragmentCreateGameBinding>(inflater, R.layout.fragment_create_game, container, false)

        binding.buttonGameNextStep.setOnClickListener{ view : View ->
            view.findNavController().navigate(R.id.action_createGameFragment_to_addRiddleFragment)
        }

        return binding.root
    }
}