package ru.topchu.winfoxtestapp.presentation

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.NavController
import androidx.navigation.NavOptions
import androidx.navigation.Navigation.findNavController
import dagger.hilt.android.AndroidEntryPoint
import ru.topchu.winfoxtestapp.R
import ru.topchu.winfoxtestapp.databinding.ActivityMainBinding
import ru.topchu.winfoxtestapp.presentation.auth.AuthFragmentDirections
import ru.topchu.winfoxtestapp.presentation.profile.ProfileFragment
import ru.topchu.winfoxtestapp.presentation.profile.ProfileFragmentDirections
import ru.topchu.winfoxtestapp.utils.SharedPref
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController

    @Inject
    lateinit var sharedPref: SharedPref

    @Inject
    lateinit var navOptions: NavOptions

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}