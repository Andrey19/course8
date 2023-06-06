package ru.netology.nmedia.repository


import ru.netology.nmedia.dto.Post

interface PostRepository {
    fun getAllAsync(callback: CommonCallback<List<Post>>)
    fun saveAsync(post: Post, callback: CommonCallback<Post>)
    fun removeByIdAsync(id: Long, callback: CommonCallback<Unit>)
    fun likeByIdAsync(id: Long, callback: CommonCallback<Post>)
    fun unLikeByIdAsync(id: Long, callback: CommonCallback<Post>)

    interface CommonCallback<T> {
        fun onSuccess(t :T) {}
        fun onError(e: Exception) {}
    }

}
