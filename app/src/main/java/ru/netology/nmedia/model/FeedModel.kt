package ru.netology.nmedia.model

import ru.netology.nmedia.dto.Post

data class FeedModel(
    val posts: List<Post> = emptyList(),
    val loading: Boolean = false,
    val error: ErrorType = ErrorType.NONE,
    val empty: Boolean = false,
    val refreshing: Boolean = false,
)

enum class ErrorType{
    NONE,
    LOAD,
    LIKE,
    DISLIKE,
    SAVE,
    REMOVE
}
