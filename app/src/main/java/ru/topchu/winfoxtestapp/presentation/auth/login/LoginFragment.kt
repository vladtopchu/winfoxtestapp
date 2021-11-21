package ru.topchu.winfoxtestapp.presentation.auth.login

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import ru.topchu.winfoxtestapp.R
import ru.topchu.winfoxtestapp.databinding.FragmentLoginBinding
import ru.topchu.winfoxtestapp.databinding.FragmentRegistrationBinding

class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }
}