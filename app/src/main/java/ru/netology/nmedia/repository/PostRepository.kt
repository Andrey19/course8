package ru.netology.nmedia.repository



import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import ru.netology.nmedia.dto.Media
import ru.netology.nmedia.dto.MediaUpload
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.model.PhotoModel
import ru.netology.nmedia.model.TokenModel

interface PostRepository {

    val data: Flow<PagingData<Post>>
    fun getNewerCount(id: Long): Flow<Int>
    suspend fun updateAll()
    suspend fun getAll()
    suspend fun save(post: Post)
    suspend fun upload(upload: MediaUpload): Media
    suspend fun userLogin(login: String, password: String): TokenModel

    suspend fun userRegister(login: String, password: String, name: String):
            TokenModel

    suspend fun saveWithAttachment(post: Post, upload: MediaUpload)
    suspend fun removeById(id: Long)
    suspend fun likeById(id: Long)
    suspend fun unLikeById(id: Long)
}

