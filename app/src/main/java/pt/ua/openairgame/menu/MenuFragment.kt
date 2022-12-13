package pt.ua.openairgame.menu

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import pt.ua.openairgame.R
import pt.ua.openairgame.databinding.FragmentMenuBinding

class MenuFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = DataBindingUtil.inflate<FragmentMenuBinding>(inflater, R.layout.fragment_menu, container, false)

        binding.createGameButton.setOnClickListener{ view : View ->
            TODO("Go to CreateGameFragment")
        }
        binding.currentGameButton.setOnClickListener{ view : View ->
            TODO("Go to CurrentGameFragment")
        }
        binding.joinGameButton.setOnClickListener{ view : View ->
            TODO("Go to JoinGameFragment")
        }

        return binding.root
    }

}