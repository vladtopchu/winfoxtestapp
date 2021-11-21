package ru.topchu.winfoxtestapp.presentation.auth

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import ru.topchu.winfoxtestapp.R
import ru.topchu.winfoxtestapp.databinding.FragmentAuthBinding
import ru.topchu.winfoxtestapp.databinding.FragmentRegistrationBinding
import ru.topchu.winfoxtestapp.presentation.auth.login.LoginFragment
import ru.topchu.winfoxtestapp.presentation.auth.registration.RegistrationFragment
import timber.log.Timber

@AndroidEntryPoint
class AuthFragment : Fragment() {

    private var _binding: FragmentAuthBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewPagerAdapter: AuthViewPagerAdapter

    private val viewModel: AuthViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAuthBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewPagerAdapter = AuthViewPagerAdapter(requireActivity())
        viewPagerAdapter.fragments = listOf(LoginFragment(), RegistrationFragment())
        binding.viewPager.adapter = viewPagerAdapter

        viewModel.status.observe(viewLifecycleOwner) {
            Timber.d(it.toString())
            if(it) {
                viewModel.wipeStatus()
                findNavController().navigate(AuthFragmentDirections.actionAuthFragmentToProfileFragment())
            }
        }

    }
}