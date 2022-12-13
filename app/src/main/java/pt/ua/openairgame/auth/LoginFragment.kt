package pt.ua.openairgame.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import pt.ua.openairgame.R
import pt.ua.openairgame.databinding.FragmentLoginBinding

class LoginFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = DataBindingUtil.inflate<FragmentLoginBinding>(inflater, R.layout.fragment_menu, container, false)

        binding.signInButton.setOnClickListener{ view : View ->
            view.findNavController().navigate(R.id.action_loginFragment_to_menuFragment)
        }
        binding.signUpButton.setOnClickListener{ view : View ->
            TODO("Go to SignUpFragment")
        }

        return binding.root
    }

}