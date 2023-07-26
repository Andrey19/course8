package ru.netology.nmedia.viewmodel

import android.app.Application
import android.net.Uri
import androidx.lifecycle.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import ru.netology.nmedia.auth.AppAuth
import ru.netology.nmedia.db.AppDb
import ru.netology.nmedia.dto.MediaUpload
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.model.ErrorType
import ru.netology.nmedia.model.FeedModel
import ru.netology.nmedia.model.FeedModelState
import ru.netology.nmedia.model.PhotoModel
import ru.netology.nmedia.repository.*
import ru.netology.nmedia.util.SingleLiveEvent
import java.io.File

private val empty = Post(
    id = 0,
    authorId = 0,
    content = "",
    author = "",
    authorAvatar = "",
    likedByMe = false,
    likes = 0,
    published = "",
    attachment = null,
)

private val noPhoto = PhotoModel()

@ExperimentalCoroutinesApi
class PostViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: PostRepository =
        PostRepositoryImpl(AppDb.getInstance(context = application).postDao())


    val data: LiveData<FeedModel> = AppAuth.getInstance()
        .authStateFlow
        .flatMapLatest { (myId, _) ->
            repository.data
                .map { posts ->
                    FeedModel(
                        posts.map { it.copy(ownedByMe = it.authorId == myId) },
                        posts.isEmpty()
                    )
                }
        }.asLiveData(Dispatchers.Default)

    private val _dataState = MutableLiveData<FeedModelState>()
    val dataState: LiveData<FeedModelState>
        get() = _dataState

    private val _photo = MutableLiveData(noPhoto)
    val photo: LiveData<PhotoModel>
        get() = _photo


    private val edited = MutableLiveData(empty)
    private val _postCreated = SingleLiveEvent<Unit>()
    val postCreated: LiveData<Unit>
        get() = _postCreated

    private val _addedPost = SingleLiveEvent<Unit>()

    val addedPost: LiveData<Unit>
        get() = _addedPost


    init {
        loadPosts()
    }

    fun changePhoto(uri: Uri?, file: File?) {
        _photo.value = PhotoModel(uri, file)
    }


    fun loadPosts() = viewModelScope.launch {
        try {
            _dataState.value = FeedModelState(loading = true)
            repository.getAll()
            _dataState.value = FeedModelState()
        } catch (e: Exception) {
            _dataState.value = FeedModelState(error = ErrorType.LOAD)
        }
    }

    val newerCount: LiveData<Int> = data.switchMap {
        repository.getNewerCount(it.posts.firstOrNull()?.id ?: 0L)
            .catch { e -> e.printStackTrace() }
            .asLiveData(Dispatchers.Default)
    }

    fun updateAll() = viewModelScope.launch {
        try {
            repository.updateAll()
        } catch (e: Exception) {
            println("Update error")
        }
    }


    fun refreshPosts() = viewModelScope.launch {
        try {
            _dataState.value = FeedModelState(refreshing = true)
            repository.getAll()
            _dataState.value = FeedModelState()
        } catch (e: Exception) {
            _dataState.value = FeedModelState(error = ErrorType.LOAD)
        }
    }

    fun save() {
        edited.value?.let {
            _postCreated.value = Unit
            viewModelScope.launch {
                try {
                    when(_photo.value) {
                        noPhoto -> repository.save(it.copy(attachment = null))
                        else -> {
                            if (_photo.value?.file != null) {
                                _photo.value?.file?.let { file ->
                                    repository.saveWithAttachment(it, MediaUpload(file))
                                }
                            } else {
                                repository.save(it)
                            }
                        }
                    }
                    if(it.id == 0L) {
                        _addedPost.value = Unit
                    }
                    _dataState.value = FeedModelState()
                } catch (e: Exception) {
                    _dataState.value = FeedModelState(error = ErrorType.SAVE)
                }
            }
        }
        edited.value = empty
        _photo.value = noPhoto
    }



    fun edit(post: Post) {
        edited.value = post
    }


    fun changeContent(content: String) {
        val text = content.trim()
        if (edited.value?.content == text) {
            return
        }
        edited.value = edited.value?.copy(content = text)
    }

    fun likeById(post: Post) {

        val isLiked = post.likedByMe


        if (isLiked) {
            viewModelScope.launch {
                try {
                    repository.unLikeById(post.id)
                    _dataState.value = FeedModelState()
                } catch (e: Exception) {
                    _dataState.value = FeedModelState(error = ErrorType.DISLIKE)
                }
            }
        } else {
            viewModelScope.launch {
                try {
                    repository.likeById(post.id)
                    _dataState.value = FeedModelState()
                } catch (e: Exception) {
                    _dataState.value = FeedModelState(error = ErrorType.LIKE)
                }
            }
        }
    }

    fun removeById(id: Long) {
        viewModelScope.launch {
            try {
                repository.removeById(id)
                _dataState.value = FeedModelState()
            } catch (e: Exception) {
                _dataState.value = FeedModelState(error = ErrorType.REMOVE)
            }
        }
    }

    fun clearPhoto(){
        _photo.value = null
    }
    fun setPhoto(photoModel: PhotoModel) {
        _photo.value = photoModel
    }
}
