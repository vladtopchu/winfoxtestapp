package ru.topchu.winfoxtestapp.presentation.auth.login

import android.os.Bundle
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import ru.topchu.winfoxtestapp.R
import ru.topchu.winfoxtestapp.databinding.FragmentLoginBinding
import ru.topchu.winfoxtestapp.databinding.FragmentRegistrationBinding
import ru.topchu.winfoxtestapp.presentation.auth.AuthViewModel
import ru.topchu.winfoxtestapp.utils.clickListener
import ru.topchu.winfoxtestapp.utils.isValidEmail
import timber.log.Timber
import kotlin.properties.Delegates

@AndroidEntryPoint
class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    private val viewModel: LoginViewModel by viewModels()
    private val sharedViewModel: AuthViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.hint.setOnClickListener {
            if(binding.hintText.alpha == 0f) {
                binding.hintText.animate().alpha(1f).translationX(0f).setDuration(500).start()
            } else {
                binding.hintText.animate().alpha(0f).translationX(-binding.hintText.width.toFloat()).setDuration(500).start()
            }
        }

        viewModel.state.observe(viewLifecycleOwner) {
            if(it.isLoading) {
                binding.progressCircular.visibility = View.VISIBLE
            } else {
                binding.progressCircular.visibility = View.GONE
                binding.proceedLogin.isEnabled = true
                if(it.errorMessage != null) {
                    Toast.makeText(requireContext(), it.errorMessage, Toast.LENGTH_LONG).show()
                } else if(it.response != null) {
                    sharedViewModel.proceedAuth(it.response)
                }
            }
        }

        binding.showPassword.setOnClickListener(clickListener(binding.passwordInput))

        binding.proceedLogin.setOnClickListener {
            if(binding.emailInput.text.toString().isEmpty() || binding.passwordInput.text.toString().isEmpty()){
                Toast.makeText(requireContext(), "?????????????????? ?????? ????????!", Toast.LENGTH_LONG).show()
            } else if(!isValidEmail(binding.emailInput.text.toString())) {
                Toast.makeText(requireContext(), "?????????????? ???????????????????? ????. ??????????", Toast.LENGTH_LONG).show()
            } else {
                viewModel.proceedLogin(binding.emailInput.text.toString(), binding.passwordInput.text.toString())
                binding.proceedLogin.isEnabled = false
            }
        }
    }
}