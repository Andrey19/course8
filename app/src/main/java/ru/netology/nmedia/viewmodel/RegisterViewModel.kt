package ru.netology.nmedia.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import ru.netology.nmedia.auth.AppAuth
import ru.netology.nmedia.db.AppDb
import ru.netology.nmedia.repository.PostRepository
import ru.netology.nmedia.repository.PostRepositoryImpl
import ru.netology.nmedia.util.SingleLiveEvent
import javax.inject.Inject

@ExperimentalCoroutinesApi
@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val repository: PostRepository,
    private val auth: AppAuth,
) : ViewModel() {


    private val _userRegister = SingleLiveEvent<Boolean>()

    val userRegister: LiveData<Boolean>
        get() = _userRegister


    fun usersRegister(login: String, password: String, name: String) {
        viewModelScope.launch {
            try {
                val response = repository.userRegister(login, password,
                    name)
                auth.setAuth(response.id,
                    response.token)
                _userRegister.value = true
            } catch (e: Exception) {
                _userRegister.value = false
            }
        }
    }


}
