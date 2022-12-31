package pt.ua.openairgame.solveriddle

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import pt.ua.openairgame.R
import pt.ua.openairgame.databinding.FragmentSolveRiddleBinding

class SolveRiddleFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = DataBindingUtil.inflate<FragmentSolveRiddleBinding>(inflater, R.layout.fragment_solve_riddle, container, false)

        return binding.root
    }
}