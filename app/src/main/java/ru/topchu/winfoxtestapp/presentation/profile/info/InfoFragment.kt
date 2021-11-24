package ru.topchu.winfoxtestapp.presentation.profile.info

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import ru.topchu.winfoxtestapp.data.remote.dto.UpdateProfileDto
import ru.topchu.winfoxtestapp.databinding.FragmentInfoBinding
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

        viewModel.state.observe(viewLifecycleOwner) {
            if(it.isLoading) {
                binding.progressCircular.visibility = View.VISIBLE
            } else {
                binding.progressCircular.visibility = View.GONE
                if(it.errorMessage != null) {
                    Toast.makeText(requireContext(), it.errorMessage, Toast.LENGTH_LONG).show()
                } else if(it.response != null) {
                    Toast.makeText(requireActivity().applicationContext, "Информация успешно обновлена!", Toast.LENGTH_LONG).show()
                    findNavController().navigate(InfoFragmentDirections.actionInfoFragmentToProfileFragment())
                }
            }
        }

        viewModel.prefs.observe(viewLifecycleOwner) {
            binding.interests.text = it.toString()
        }

        viewModel.userData.observe(viewLifecycleOwner) {
            if(it != null) {
                binding.progressCircular.visibility = View.GONE
                binding.form.visibility = View.VISIBLE
                if(firstLaunch) {
                    if(it.email != null){
                        binding.emailInput.text = it.email.toEditable()
                    }
                    if(it.firstname != null){
                        binding.firstnameInput.text = it.firstname.toEditable()
                    }
                    if(it.lastname != null){
                        binding.lastnameInput.text = it.lastname.toEditable()
                    }
                    if(it.middlename != null){
                        binding.middlenameInput.text = it.middlename.toEditable()
                    }
                    if(it.birth_place != null){
                        binding.birthplaceInput.text = it.birth_place.toEditable()
                    }
                    if(it.organization != null){
                        binding.organizationInput.text = it.organization.toEditable()
                    }
                    if(it.position != null){
                        binding.positionInput.text = it.position.toEditable()
                    }
                    if(it.birthdate != null){
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
            if(!isValidEmail(binding.emailInput.text.toString())){
                Toast.makeText(requireContext(), "Введите корректный эл. адрес", Toast.LENGTH_LONG).show()
            } else {
                val userUpdate = UpdateProfileDto()
                userUpdate.email = if(binding.emailInput.text.toString().isEmpty()) null else binding.emailInput.text.toString()
                userUpdate.firstname = if(binding.firstnameInput.text.toString().isEmpty()) null else binding.firstnameInput.text.toString()
                userUpdate.lastname = if(binding.lastnameInput.text.toString().isEmpty()) null else binding.lastnameInput.text.toString()
                userUpdate.middlename = if(binding.middlenameInput.text.toString().isEmpty()) null else binding.middlenameInput.text.toString()
                userUpdate.organization = if(binding.organizationInput.text.toString().isEmpty()) null else binding.organizationInput.text.toString()
                userUpdate.position = if(binding.positionInput.text.toString().isEmpty()) null else binding.positionInput.text.toString()
                userUpdate.birthdate = if(binding.birthdateInput.text.toString().isEmpty()) null else binding.birthdateInput.text.toString()
                userUpdate.birth_place = if(binding.birthplaceInput.text.toString().isEmpty()) null else binding.birthplaceInput.text.toString()
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
