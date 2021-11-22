package ru.topchu.winfoxtestapp.presentation.auth.registration

import android.os.Bundle
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import ru.topchu.winfoxtestapp.R
import ru.topchu.winfoxtestapp.databinding.FragmentRegistrationBinding
import ru.topchu.winfoxtestapp.presentation.auth.AuthViewModel
import ru.topchu.winfoxtestapp.utils.clickListener
import ru.topchu.winfoxtestapp.utils.isValidEmail

@AndroidEntryPoint
class RegistrationFragment : Fragment() {

    private var _binding: FragmentRegistrationBinding? = null
    private val binding get() = _binding!!

    private val viewModel: RegistrationViewModel by viewModels()
    private val sharedViewModel: AuthViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRegistrationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.state.observe(viewLifecycleOwner) {
            if(it.isLoading) {
                binding.progressCircular.visibility = View.VISIBLE
            } else {
                binding.progressCircular.visibility = View.GONE
                binding.proceedRegister.isEnabled = true
                if(it.errorMessage != null) {
                    Toast.makeText(requireContext(), it.errorMessage, Toast.LENGTH_LONG).show()
                } else if(it.response != null) {
                    sharedViewModel.proceedAuth(it.response)
                }
            }
        }

        binding.showPassword.setOnClickListener(clickListener(binding.passwordInput))
        binding.showRepeatPassword.setOnClickListener(clickListener(binding.repeatPasswordInput))

        binding.proceedRegister.setOnClickListener {
            if(binding.emailInput.text.toString().isEmpty() ||
                binding.lastnameInput.text.toString().isEmpty() ||
                binding.firstnameInput.text.toString().isEmpty() ||
                binding.passwordInput.text.toString().isEmpty() ||
                binding.repeatPasswordInput.text.toString().isEmpty()) {
                Toast.makeText(requireContext(), "Введите все поля!", Toast.LENGTH_LONG).show()
            } else if(binding.passwordInput.text.toString() !=
                    binding.repeatPasswordInput.text.toString()) {
                Toast.makeText(requireContext(), "Пароли не совпадают!", Toast.LENGTH_LONG).show()
            } else if(!isValidEmail(binding.emailInput.text.toString())) {
                Toast.makeText(requireContext(), "Неккоректная эл. почта!", Toast.LENGTH_LONG).show()
            } else {
                binding.proceedRegister.isEnabled = false
                viewModel.proceedRegistration(
                    binding.emailInput.text.toString(),
                    binding.firstnameInput.text.toString(),
                    binding.lastnameInput.text.toString(),
                    binding.passwordInput.text.toString(),
                )
            }
        }

    }
}