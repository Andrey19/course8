package ru.netology.nmedia.model

import ru.netology.nmedia.dto.Post

data class PostCreatedModel(
    val post: Post? = null,
    val error: ErrorType = ErrorType.NONE,
)


