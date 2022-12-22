package pt.ua.openairgame.creategame

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import pt.ua.openairgame.R
import pt.ua.openairgame.databinding.FragmentAddRiddleBinding

class AddRiddleFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = DataBindingUtil.inflate<FragmentAddRiddleBinding>(inflater, R.layout.fragment_add_riddle, container, false)

        binding.buttonSaveRiddle.setOnClickListener{ view : View ->
            view.findNavController().navigate(R.id.action_addRiddleFragment_to_createGameFragment)
        }

        return binding.root
    }
}