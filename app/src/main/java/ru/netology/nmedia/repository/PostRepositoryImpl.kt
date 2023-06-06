package ru.netology.nmedia.repository

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import okhttp3.internal.EMPTY_REQUEST
import ru.netology.nmedia.api.PostsApi
import ru.netology.nmedia.dto.Post
import java.io.IOException
import java.util.concurrent.TimeUnit


class PostRepositoryImpl : PostRepository {
    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .build()
    private val gson = Gson()
    private val typeToken = object : TypeToken<List<Post>>() {}
    private val typePostToken = object : TypeToken<Post>() {}

    companion object {
        private const val BASE_URL = "http://10.0.2.2:9999"
        private val jsonType = "application/json".toMediaType()
    }


    override fun getAllAsync(callback: PostRepository.CommonCallback<List<Post>>) {
        PostsApi.retrofitService.getAll().enqueue(object : retrofit2.Callback<List<Post>> {
            override fun onResponse(call: retrofit2.Call<List<Post>>, response: retrofit2.Response<List<Post>>) {
                if (!response.isSuccessful) {
                    callback.onError(java.lang.RuntimeException(response.message()))
                    return
                }

                callback.onSuccess(response.body() ?: throw java.lang.RuntimeException("body is null"))
            }

            override fun onFailure(call: retrofit2.Call<List<Post>>, t: Throwable) {
                callback.onError(java.lang.RuntimeException(t.message))
            }
        })
    }



    override fun likeByIdAsync(id: Long, callback: PostRepository.CommonCallback<Post>) {
        PostsApi.retrofitService.likeById(id).enqueue(object : retrofit2.Callback<Post> {
            override fun onResponse(call: retrofit2.Call<Post>, response: retrofit2.Response<Post>) {
                if (!response.isSuccessful) {
                    callback.onError(java.lang.RuntimeException(response.message()))
                    return
                }

                callback.onSuccess(response.body() ?: throw java.lang.RuntimeException("body is null"))
            }

            override fun onFailure(call: retrofit2.Call<Post>, t: Throwable) {
                callback.onError(java.lang.RuntimeException(t.message))
            }
        })
    }

    override fun unLikeByIdAsync(id: Long, callback: PostRepository.CommonCallback<Post>) {
        PostsApi.retrofitService.dislikeById(id).enqueue(object : retrofit2.Callback<Post> {
            override fun onResponse(call: retrofit2.Call<Post>, response: retrofit2.Response<Post>) {
                if (!response.isSuccessful) {
                    callback.onError(java.lang.RuntimeException(response.message()))
                    return
                }

                callback.onSuccess(response.body() ?: throw java.lang.RuntimeException("body is null"))
            }

            override fun onFailure(call: retrofit2.Call<Post>, t: Throwable) {
                callback.onError(java.lang.RuntimeException(t.message))
            }
        })
    }


    override fun saveAsync(post: Post, callback: PostRepository.CommonCallback<Post>) {
        PostsApi.retrofitService.save(post).enqueue(object : retrofit2.Callback<Post> {
            override fun onResponse(call: retrofit2.Call<Post>, response: retrofit2.Response<Post>) {
                if (!response.isSuccessful) {
                    callback.onError(java.lang.RuntimeException(response.message()))
                    return
                }

                callback.onSuccess(response.body() ?: throw java.lang.RuntimeException("body is null"))
            }

            override fun onFailure(call: retrofit2.Call<Post>, t: Throwable) {
                callback.onError(java.lang.RuntimeException(t.message))
            }
        })
    }


    override fun removeByIdAsync(id: Long, callback: PostRepository.CommonCallback<Unit>) {
        PostsApi.retrofitService.removeById(id).enqueue(object : retrofit2.Callback<Unit> {
            override fun onResponse(call: retrofit2.Call<Unit>, response: retrofit2.Response<Unit>) {
                if (!response.isSuccessful) {
                    callback.onError(java.lang.RuntimeException(response.message()))
                    return
                }

                callback.onSuccess(response.body() ?: throw java.lang.RuntimeException("body is null"))
            }

            override fun onFailure(call: retrofit2.Call<Unit>, t: Throwable) {
                callback.onError(java.lang.RuntimeException(t.message))
            }
        })
    }
    
}
