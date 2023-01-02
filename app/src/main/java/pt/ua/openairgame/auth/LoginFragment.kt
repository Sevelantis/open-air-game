package pt.ua.openairgame.auth

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.TextView.OnEditorActionListener
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import pt.ua.openairgame.R
import pt.ua.openairgame.databinding.FragmentLoginBinding
import pt.ua.openairgame.model.GameDataViewModel


class LoginFragment : Fragment() {
    private val TAG = "LoginFragment"
    private val gameDataViewModel: GameDataViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = DataBindingUtil.inflate<FragmentLoginBinding>(inflater, R.layout.fragment_login, container, false)

        binding.buttonSignIn.setOnClickListener{ view : View ->
            // TODO send login user request
            view.findNavController().navigate(R.id.action_loginFragment_to_menuFragment)
        }
        binding.buttonSignUp.setOnClickListener { view: View ->
            // TODO send register user request
        }
        binding.password.setOnEditorActionListener(OnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                // hide keyboard after confirming the password
                val imm: InputMethodManager = v.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(v.windowToken, 0)
                return@OnEditorActionListener true
            }
            false
        })
        return binding.root
    }
}
