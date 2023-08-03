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
import ru.netology.nmedia.databinding.FragmentLoginBinding
import ru.netology.nmedia.util.AndroidUtils
import ru.netology.nmedia.util.StringArg
import ru.netology.nmedia.viewmodel.LoginViewModel
import ru.netology.nmedia.viewmodel.PostViewModel

@AndroidEntryPoint
@ExperimentalCoroutinesApi
class LoginFragment : Fragment() {

    private val loginViewModel: LoginViewModel by activityViewModels()
    private val viewModel: PostViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentLoginBinding.inflate(inflater, container,
            false)

        binding.btnlogin.setOnClickListener {

            loginViewModel.usersLogin(binding.inputLogin.text.toString(),
                binding.inputPassword.text.toString())
            AndroidUtils.hideKeyboard(requireView())
        }

        loginViewModel.userLogin.observe(viewLifecycleOwner) {
            if (it) {
                viewModel.loadPosts()
                findNavController().navigateUp()
            } else {
                Snackbar.make(binding.root, R.string.auth_error,
                    Snackbar.LENGTH_INDEFINITE)
                    .setAction(R.string.ok) { }
                    .show()
            }
        }

        return binding.root
    }
}

