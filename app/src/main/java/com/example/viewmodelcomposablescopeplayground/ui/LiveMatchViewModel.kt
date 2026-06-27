package com.example.viewmodelcomposablescopeplayground.ui

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.viewmodelcomposablescopeplayground.data.SportsRepository
import com.example.viewmodelcomposablescopeplayground.data.model.LiveMatch
import com.example.viewmodelcomposablescopeplayground.data.model.MatchSchedule
import com.example.viewmodelcomposablescopeplayground.data.model.NewsPost
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

private const val TAG = "LiveMatchViewModel"

class LiveMatchViewModel(
    private val matchId: String,
    private val repository: SportsRepository
) : ViewModel() {

    private val _liveScore = MutableStateFlow<LiveMatch?>(null)
    val liveScore = _liveScore.asStateFlow()

    init {
        Log.d(TAG, "Init")
        viewModelScope.launch {
            repository.observeLiveMatch(matchId).collect { match ->
                _liveScore.value = match
            }
        }
    }

    override fun onCleared() {
        Log.d(TAG, "onCleared")
    }
}
