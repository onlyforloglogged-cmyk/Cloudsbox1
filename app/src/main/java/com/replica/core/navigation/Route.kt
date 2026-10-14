package com.replica.core.navigation

import kotlinx.serialization.Serializable

sealed interface Route {
    @Serializable
    data object Feed : Route

    @Serializable
    data class Search(val initialQuery: String? = null) : Route

    @Serializable
    data class Details(
        val mediaId: String,
        val providerId: String
    ) : Route

    @Serializable
    data class Player(
        val mediaId: String,
        val episodeId: String,
        val streamUrl: String,
        val resumePositionMs: Long = 0L
    ) : Route

    @Serializable
    data object Downloads : Route

    @Serializable
    data object Extensions : Route
}
