package ru.netology.nmedia.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import ru.netology.nmedia.auth.AppAuth
import ru.netology.nmedia.db.AppDb
import ru.netology.nmedia.repository.PostRepository
import ru.netology.nmedia.repository.PostRepositoryImpl
import ru.netology.nmedia.util.SingleLiveEvent

@ExperimentalCoroutinesApi
class RegisterViewModel(application: Application) :
    AndroidViewModel(application) {
    private val repository: PostRepository =
        PostRepositoryImpl(AppDb.getInstance(context =
        application).postDao())

    private val _userRegister = SingleLiveEvent<Boolean>()

    val userRegister: LiveData<Boolean>
        get() = _userRegister


    fun usersRegister(login: String, password: String, name: String) {
        viewModelScope.launch {
            try {
                val response = repository.userRegister(login, password,
                    name)
                AppAuth.getInstance().setAuth(response.id,
                    response.token)
                _userRegister.value = true
            } catch (e: Exception) {
                _userRegister.value = false
            }
        }
    }


}
