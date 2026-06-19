package com.example.viewmodelcomposablescopeplayground.data.model

data class LiveMatch(
    val matchId: String,
    val homeTeam: String,
    val awayTeam: String,
    val homeScore: Int,
    val awayScore: Int,
    val matchMinute: Int,
    val isLive: Boolean
)
