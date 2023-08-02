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
class LoginViewModel @Inject constructor(
    private val repository: PostRepository,
    private val auth: AppAuth,
) : ViewModel() {

    private val _userLogin = SingleLiveEvent<Boolean>()

    val userLogin: LiveData<Boolean>
        get() = _userLogin


    fun usersLogin(login: String, password: String) {
        viewModelScope.launch {
            try {
                val response = repository.userLogin(login, password)
                auth.setAuth(response.id,
                    response.token)
                _userLogin.value = true
            } catch (e: Exception) {
                _userLogin.value = false
            }
        }
    }


}
