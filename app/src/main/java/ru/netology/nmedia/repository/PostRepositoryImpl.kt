package ru.netology.nmedia.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import ru.netology.nmedia.error.AppError
import ru.netology.nmedia.api.PostsApi
import ru.netology.nmedia.dao.PostDao
import ru.netology.nmedia.dto.Attachment
import ru.netology.nmedia.dto.AttachmentType
import ru.netology.nmedia.dto.Media
import ru.netology.nmedia.dto.MediaUpload
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.entity.PostEntity
import ru.netology.nmedia.entity.toDto
import ru.netology.nmedia.entity.toEntity
import ru.netology.nmedia.error.ApiError
import ru.netology.nmedia.error.NetworkError
import ru.netology.nmedia.error.UnknownError
import ru.netology.nmedia.model.TokenModel
import java.io.IOException


class PostRepositoryImpl(private val dao: PostDao) : PostRepository {
    override val data = dao.getAll()
        .map(List<PostEntity>::toDto)
        .flowOn(Dispatchers.Default)


    override suspend fun getAll() {
        try {
            val response = PostsApi.service.getAll()
            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }

            val body = response.body() ?: throw ApiError(response.code(), response.message())
            dao.insert(body.toEntity(true))
        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw UnknownError
        }
    }

    override fun getNewerCount(id: Long): Flow<Int> = flow {
        while (true) {
            delay(10_000L)
            val response = PostsApi.service.getNewer(id)
            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }

            val body = response.body() ?: throw ApiError(response.code(), response.message())
            if(body.isNotEmpty()) {
                dao.insert(body.toEntity(true))
                emit(body.size)
            }
        }
    }
        .catch { e -> throw AppError.from(e) }
        .flowOn(Dispatchers.Default)

    override suspend fun userLogin(login: String, password: String):
            TokenModel {
        try {
            val response = PostsApi.service.userLogin(login,password)
            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }

            return response.body() ?: throw ApiError(response.code(),
                response.message())

        } catch (e: Exception) {
            throw UnknownError
        }


    }
    override suspend fun userRegister(login: String, password: String,
                                      name: String): TokenModel {
        try {
            val response = PostsApi.service.userRegister(login, password,
                name)
            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }

            return response.body() ?: throw ApiError(response.code(),
                response.message())

        } catch (e: Exception) {
            throw UnknownError
        }


    }
    override suspend fun save(post: Post) {
        try {
            val response = PostsApi.service.save(post)
            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }

            val body = response.body() ?: throw ApiError(response.code(), response.message())
            dao.insert(PostEntity.fromDto(body, true))
        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw UnknownError
        }
    }

    override suspend fun saveWithAttachment(post: Post, upload: MediaUpload) {
        try {
            val media = upload(upload)

            val postWithAttachment = post.copy(attachment = Attachment(media.id,
                null, AttachmentType.IMAGE))
            save(postWithAttachment)
        } catch (e: AppError) {
            throw e
        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw UnknownError
        }
    }
    override suspend fun upload(upload: MediaUpload): Media {
        try {
            val media = MultipartBody.Part.createFormData(
                "file", upload.file.name, upload.file.asRequestBody()
            )

            val response = PostsApi.service.upload(media)
            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }

            return response.body() ?: throw ApiError(response.code(),
                response.message())
        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw UnknownError
        }
    }

    override suspend fun updateAll() {
        dao.updateAll()
    }

    override suspend fun likeById(id: Long) {
        dao.likeById(id)
        try {
            val response = PostsApi.service.likeById(id)
            if (!response.isSuccessful) {
                dao.likeById(id)
                throw ApiError(response.code(), response.message())
            }

            val body = response.body() ?: throw ApiError(response.code(), response.message())
            dao.insert(PostEntity.fromDto(body, true))
        } catch (e: IOException) {
            dao.likeById(id)
            throw NetworkError
        } catch (e: Exception) {
            dao.likeById(id)
            throw UnknownError
        }
    }

    override suspend fun unLikeById(id: Long) {
        dao.likeById(id)
        try {
            val response = PostsApi.service.dislikeById(id)
            if (!response.isSuccessful) {
                dao.likeById(id)
                throw ApiError(response.code(), response.message())
            }

            val body = response.body() ?: throw ApiError(response.code(), response.message())
            dao.insert(PostEntity.fromDto(body, true))
        } catch (e: IOException) {
            dao.likeById(id)
            throw NetworkError
        } catch (e: Exception) {
            dao.likeById(id)
            throw UnknownError
        }
    }

    override suspend fun removeById(id: Long) {
        val post = dao.getPostById(id).toDto()
        dao.removeById(id)
        try {
            val response = PostsApi.service.removeById(id)
            if (!response.isSuccessful) {
                dao.insert(PostEntity.fromDto(post,true))
                throw ApiError(response.code(), response.message())
            }
        } catch (e: IOException) {
            dao.insert(PostEntity.fromDto(post,true))
            throw NetworkError
        } catch (e: Exception) {
            dao.insert(PostEntity.fromDto(post,true))
            throw UnknownError
        }
    }

}

