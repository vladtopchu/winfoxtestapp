package ru.topchu.winfoxtestapp.presentation.profile.info

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.DialogInterface
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.DatePicker
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.MutableLiveData
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ru.topchu.winfoxtestapp.data.local.AppDatabase
import ru.topchu.winfoxtestapp.data.remote.dto.UpdateProfileDto
import ru.topchu.winfoxtestapp.databinding.FragmentInfoBinding
import ru.topchu.winfoxtestapp.databinding.FragmentProfileBinding
import ru.topchu.winfoxtestapp.di.ApplicationScope
import ru.topchu.winfoxtestapp.utils.*
import ru.topchu.winfoxtestapp.utils.Constants.INTERESTS
import timber.log.Timber
import javax.inject.Inject
import kotlin.properties.Delegates

@AndroidEntryPoint
class InfoFragment : Fragment() {

    private var _binding: FragmentInfoBinding? = null
    private val binding get() = _binding!!

    private val viewModel: InfoViewModel by viewModels()

    lateinit var datePickerDialog: DatePickerDialog
    lateinit var interestsPickerDialog: AlertDialog

    private var firstLaunch by Delegates.notNull<Boolean>()

    @Inject
    lateinit var sharedPref: SharedPref

    @Inject
    lateinit var database: AppDatabase

    @Inject
    @ApplicationScope
    lateinit var applicationScope: CoroutineScope

    override fun onResume() {
        super.onResume()
        firstLaunch = true
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentInfoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initDatePicker()
        initInterestsPicker(null)

        viewModel.prefs.observe(viewLifecycleOwner) {
            Timber.d(it.toString())
        }

        viewModel.state.observe(viewLifecycleOwner) {
            if(it.isLoading) {
                binding.progressCircular.visibility = View.VISIBLE
            } else {
                binding.progressCircular.visibility = View.GONE
                binding.proceedUpdate.isEnabled = true
                if(it.errorMessage != null) {
                    Toast.makeText(requireContext(), it.errorMessage, Toast.LENGTH_LONG).show()
                } else if(it.response != null) {
                    Toast.makeText(requireContext(), "SUCCESS", Toast.LENGTH_LONG).show()
                }
            }
        }

        viewModel.userData.observe(viewLifecycleOwner) {
            if(it != null) {
                Timber.d("USERDATA")
                Timber.d(it.toString())
                binding.progressCircular.visibility = View.GONE
                binding.form.visibility = View.VISIBLE
                if(firstLaunch) {
                    if(it.email != null){
                        Timber.d("email is not null", it.email)
                        binding.emailInput.text = it.email.toEditable()
                    }
                    if(it.firstname != null){
                        Timber.d("firstname is not null", it.firstname)
                        binding.firstnameInput.text = it.firstname.toEditable()
                    }
                    if(it.lastname != null){
                        Timber.d("lastname is not null", it.lastname)
                        binding.lastnameInput.text = it.lastname.toEditable()
                    }
                    if(it.middlename != null){
                        Timber.d("midllename is not null", it.middlename)
                        binding.middlenameInput.text = it.middlename.toEditable()
                    }
                    if(it.birth_place != null){
                        Timber.d("birthplace is not null", it.middlename)
                        binding.birthplaceInput.text = it.birth_place.toEditable()
                    }
                    if(it.organization != null){
                        Timber.d("organization is not null", it.middlename)
                        binding.organizationInput.text = it.organization.toEditable()
                    }
                    if(it.position != null){
                        Timber.d("position is not null", it.middlename)
                        binding.positionInput.text = it.position.toEditable()
                    }
                    if(it.birthdate != null){
                        Timber.d("birthdate is not null", it.birthdate)
                        val arr = fromDateString(it.birthdate)
                        Timber.d(arr.toString())
                        datePickerDialog.datePicker.updateDate(arr[2], arr[1], arr[0])
                        binding.birthdateInput.text = makeDateString(arr[0], arr[1], arr[2])
                    }
                    if(it.preferences.isNotEmpty()) {
                       initInterestsPicker(INTERESTS.map { el -> it.preferences.contains(el) }.toBooleanArray())
                    }
                    firstLaunch = false
                }
            } else {
                binding.form.visibility = View.GONE
                binding.progressCircular.visibility = View.VISIBLE
            }
        }
        binding.proceedUpdate.setOnClickListener {
            Timber.d("ProceedUpdate")
            if(!isValidEmail(binding.emailInput.text.toString())){
                Toast.makeText(requireContext(), "Введите корректный эл. адрес", Toast.LENGTH_LONG).show()
            } else {
                val userUpdate = UpdateProfileDto()
                userUpdate.email = binding.emailInput.text.toString()
                if(binding.firstnameInput != null) {
                    userUpdate.firstname = binding.firstnameInput.text.toString()
                }
                if(binding.lastnameInput != null) {
                    userUpdate.lastname = binding.lastnameInput.text.toString()
                }
                if(binding.middlenameInput != null) {
                    userUpdate.middlename = binding.middlenameInput.text.toString()
                }
                if(binding.organizationInput != null) {
                    userUpdate.organization = binding.organizationInput.text.toString()
                }
                if(binding.positionInput != null) {
                    userUpdate.position = binding.positionInput.text.toString()
                }
                if(binding.birthdateInput != null) {
                    userUpdate.birthdate = binding.birthdateInput.text.toString()
                }
                if(binding.birthplaceInput != null) {
                    userUpdate.birth_place = binding.birthplaceInput.text.toString()
                }
                userUpdate.preferences = viewModel.prefs.value!!
                viewModel.proceedUpdate(userUpdate)
            }
        }

        binding.birthdateInput.setOnClickListener {
            datePickerDialog.show()
        }
        binding.interestsInput.setOnClickListener {
            interestsPickerDialog.show()
        }
    }

    private fun initInterestsPicker(booleanArray: BooleanArray?) {
        val builder = AlertDialog.Builder(context).apply {
            setTitle("Выберите минимум один интерес")
            setPositiveButton("OK", null)
            setMultiChoiceItems(INTERESTS, booleanArray) { _, which, isChecked ->
                if(isChecked){
                    viewModel.addToPrefs(INTERESTS[which])
                } else {
                    viewModel.removeFromPrefs(INTERESTS[which])
                }
            }
        }
        interestsPickerDialog = builder.create()
    }

    private fun initDatePicker() {
        val listener = DatePickerDialog.OnDateSetListener { _, year, month, day ->
            Timber.d(year.toString(), month.toString(), day.toString())
            binding.birthdateInput.text = makeDateString(day, month, year)
        }
        val today = getToday()
        binding.birthdateInput.text = makeDateString(today[0], today[1], today[2])
        datePickerDialog = DatePickerDialog(requireContext(), listener, today[2], today[1], today[0])
    }
}
