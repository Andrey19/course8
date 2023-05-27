package ru.netology.nmedia.repository


import ru.netology.nmedia.dto.Post

interface PostRepository {
    fun getAll(): List<Post>
    fun likeById(id: Long): Post
    fun unLikeById(id: Long): Post
    fun save(post: Post)
    fun removeById(id: Long)
    fun getAllAsync(callback: GetAllCallback)
    fun saveAsync(post: Post, callback: SaveCallback)
    fun removeByIdAsync(id: Long, callback: RemoveCallback)
    fun likeByIdAsync(id: Long, callback: LikeCallback)
    fun unLikeByIdAsync(id: Long, callback: LikeCallback)

    interface LikeCallback {
        fun onSuccess(post: Post) {}
        fun onError(e: Exception) {}
    }
    interface RemoveCallback {
        fun onSuccess() {}
        fun onError(e: Exception) {}
    }
    interface SaveCallback {
        fun onSuccess() {}
        fun onError(e: Exception) {}
    }
    interface GetAllCallback {
        fun onSuccess(posts: List<Post>) {}
        fun onError(e: Exception) {}
    }
}
