package com.example.viewmodelcomposablescopeplayground.data.model

data class MatchSchedule(
    val matchId: String,
    val homeTeam: String,
    val awayTeam: String,
    val homeFlag: String,
    val awayFlag: String,
    val round: String,
    val venue: String,
    val kickoffBrt: String
)
