package ru.netology.nmedia.activity

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import ru.netology.nmedia.R
import ru.netology.nmedia.databinding.FragmentRegisterBinding
import ru.netology.nmedia.util.AndroidUtils
import ru.netology.nmedia.util.StringArg
import ru.netology.nmedia.viewmodel.PostViewModel
import ru.netology.nmedia.viewmodel.RegisterViewModel
@AndroidEntryPoint
@ExperimentalCoroutinesApi
class RegisterFragment : Fragment() {



    private val registerViewModel: RegisterViewModel by
    activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentRegisterBinding.inflate(inflater,
            container, false)

        binding.btnRegister.setOnClickListener {

            registerViewModel.usersRegister(binding.inputLogin.text.toString(),
                binding.inputPassword.text.toString(),
                binding.inputUsername.text.toString())
            AndroidUtils.hideKeyboard(requireView())
        }

        registerViewModel.userRegister.observe(viewLifecycleOwner) {
            if (it) {
                findNavController().navigateUp()
            } else {
                Snackbar.make(binding.root, R.string.register_error,
                    Snackbar.LENGTH_INDEFINITE)
                    .setAction(R.string.ok) { }
                    .show()
            }
        }


        return binding.root
    }
}
