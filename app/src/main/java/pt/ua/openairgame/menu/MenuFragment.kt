package pt.ua.openairgame.menu

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import pt.ua.openairgame.R
import pt.ua.openairgame.databinding.FragmentMenuBinding

class MenuFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = DataBindingUtil.inflate<FragmentMenuBinding>(inflater, R.layout.fragment_menu, container, false)

        binding.buttonGameCreate.setOnClickListener{ view : View ->
            view.findNavController().navigate(R.id.action_menuFragment_to_createGameFragment)
        }
        binding.buttonGameCurrent.setOnClickListener{ view : View ->
            view.findNavController().navigate(R.id.action_menuFragment_to_currentGameFragment)
        }
        binding.buttonGameJoin.setOnClickListener{ view : View ->
            view.findNavController().navigate(R.id.action_menuFragment_to_joinGameFragment)
        }

        return binding.root
    }

}