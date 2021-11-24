package ru.topchu.winfoxtestapp.presentation.profile

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ru.topchu.winfoxtestapp.R
import ru.topchu.winfoxtestapp.data.local.AppDatabase
import ru.topchu.winfoxtestapp.databinding.FragmentProfileBinding
import ru.topchu.winfoxtestapp.di.ApplicationScope
import ru.topchu.winfoxtestapp.presentation.ImageActivity
import ru.topchu.winfoxtestapp.utils.SharedPref
import javax.inject.Inject

@AndroidEntryPoint
class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ProfileViewModel by viewModels()

    @Inject
    lateinit var sharedPref: SharedPref

    @Inject
    lateinit var database: AppDatabase

    @Inject
    @ApplicationScope
    lateinit var applicationScope: CoroutineScope

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        viewModel.update()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if(sharedPref.getUserId() == null) {
            findNavController().navigate(ProfileFragmentDirections.actionProfileFragmentToAuthFragment())
        }

        viewModel.userData.observe(viewLifecycleOwner) {
            if(it != null) {
                binding.test.text = it.toString()
            }
        }

        binding.toImage.setOnClickListener {
            startActivity(Intent(requireActivity(), ImageActivity::class.java))
        }

        binding.toInfo.setOnClickListener {
            findNavController().navigate(ProfileFragmentDirections.actionProfileFragmentToInfoFragment())
        }

        binding.logout.setOnClickListener {
            applicationScope.launch {
                database.userDao().deleteUser(sharedPref.getUserId()!!)
                sharedPref.wipeUserId()
                withContext(Dispatchers.Main){
                    findNavController().navigate(ProfileFragmentDirections.actionProfileFragmentToAuthFragment())
                }
            }
        }
    }

}
