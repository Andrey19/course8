package ru.netology.nmedia.viewmodel

import android.app.Application
import androidx.lifecycle.*
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.model.FeedModel
import ru.netology.nmedia.repository.*
import ru.netology.nmedia.util.SingleLiveEvent

private val empty = Post(
    id = 0,
    content = "",
    author = "",
    likedByMe = false,
    likes = 0,
    published = ""
)

class PostViewModel(application: Application) : AndroidViewModel(application) {
    // упрощённый вариант
    private val repository: PostRepository = PostRepositoryImpl()
    private val _data = MutableLiveData(FeedModel())
    val data: LiveData<FeedModel>
        get() = _data
    val edited = MutableLiveData(empty)
    private val _postCreated = SingleLiveEvent<Unit>()
    val postCreated: LiveData<Unit>
        get() = _postCreated

    init {
        loadPosts()
    }

    fun loadPosts() {
        _data.value = FeedModel(loading = true)
        repository.getAllAsync(object : PostRepository.GetAllCallback {
            override fun onSuccess(posts: List<Post>) {
                _data.postValue(FeedModel(posts = posts, empty = posts.isEmpty()))
            }

            override fun onError(e: Exception) {
                _data.postValue(FeedModel(error = true))
            }
        })
    }

    fun save() {
        repository.saveAsync(edited.value!!, object : PostRepository.SaveCallback {
            override fun onSuccess() {
                _postCreated.postValue(Unit)
                edited.value = empty
            }

            override fun onError(e: Exception) {
            }
        })
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

    fun likeById(id: Long) {
        val postLocal = _data.value?.posts?.find { it.id == id }
        val isLiked = postLocal?.likedByMe

        if (isLiked != null) {
            _data.postValue(FeedModel(posts = _data.value!!.posts.map {
                if (it.id != id) it else it.copy(
                    likedByMe = !isLiked,
                    likes = if (isLiked) postLocal.likes - 1 else postLocal.likes + 1
                )
            }))
            if (isLiked) {
                repository.unLikeByIdAsync(id, object : PostRepository.LikeCallback {
                    override fun onSuccess(post: Post) {
                        _data.postValue(FeedModel(posts = _data.value!!.posts.map {
                            if (it.id != id) it else it.copy(
                                likedByMe = post.likedByMe,
                                likes = post.likes
                            )
                        }))
                    }
                    override fun onError(e: Exception) {
                        _data.postValue(FeedModel(posts = _data.value!!.posts.map {
                            if (it.id != id) it else it.copy(
                                likedByMe = postLocal.likedByMe,
                                likes = postLocal.likes
                            )
                        }))
                    }
                })
            } else {
                repository.likeByIdAsync(id, object : PostRepository.LikeCallback {
                    override fun onSuccess(post: Post) {
                        _data.postValue(FeedModel(posts = _data.value!!.posts.map {
                            if (it.id != id) it else it.copy(
                                likedByMe = post.likedByMe,
                                likes = post.likes
                            )
                        }))
                    }

                    override fun onError(e: Exception) {
                        _data.postValue(FeedModel(posts = _data.value!!.posts.map {
                            if (it.id != id) it else it.copy(
                                likedByMe = postLocal.likedByMe,
                                likes = postLocal.likes
                            )
                        }))
                    }
                })
            }
        }
    }

    fun removeById(id: Long) {
        val old = _data.value?.posts.orEmpty()
        _data.postValue(
            _data.value?.copy(posts = _data.value?.posts.orEmpty()
                .filter { it.id != id }
            )
        )
        repository.removeByIdAsync(id, object : PostRepository.RemoveCallback {
            override fun onSuccess() {
            }

            override fun onError(e: Exception) {
                _data.postValue(_data.value?.copy(posts = old))
            }
        })
    }
}
