package ru.netology.nmedia.model

import ru.netology.nmedia.dto.Post

data class FeedModel(
    val posts: List<Post> = emptyList(),
    val empty: Boolean = false,
)

data class FeedModelState (
    val loading: Boolean = false,
    val error: ErrorType = ErrorType.NONE,
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

