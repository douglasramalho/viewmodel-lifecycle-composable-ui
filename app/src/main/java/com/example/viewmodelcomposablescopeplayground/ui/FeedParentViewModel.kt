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

private const val TAG = "FeedParentViewModel"

class FeedParentViewModel(
    private val repository: SportsRepository
) : ViewModel() {

    private val _newsFeed = MutableStateFlow<List<NewsPost>>(emptyList())
    val newsFeed: StateFlow<List<NewsPost>> = _newsFeed.asStateFlow()

    private val _todayMatches = MutableStateFlow<List<MatchSchedule>>(emptyList())
    val todayMatches: StateFlow<List<MatchSchedule>> = _todayMatches.asStateFlow()

    init {
        _newsFeed.value = repository.newsFeed
        _todayMatches.value = repository.todayMatches
    }

    // =========================================================================
    // PONTO DE REFATORAÇÃO — Android Lifecycle 2.11
    //
    // O bloco abaixo representa o PROBLEMA desta arquitetura centralizada:
    // o ViewModel Pai precisa gerenciar o ciclo de vida de N partidas ao vivo
    // simultaneamente — um Job por partida, um Map de placares, um Set de IDs.
    //
    // Cada novo jogo acompanhado aumenta a responsabilidade deste ViewModel.
    // A solução (próxima etapa do tutorial) é extrair tudo isso para um
    // LiveMatchViewModel scopado a cada instância de LiveMatchWidget usando
    // a nova API viewModel() do Android Lifecycle 2.11.
    // =========================================================================

    // IDs das partidas sendo acompanhadas, em ordem de ativação
    private val _trackedMatchIds = MutableStateFlow<List<String>>(emptyList())
    val trackedMatchIds: StateFlow<List<String>> = _trackedMatchIds.asStateFlow()

    // Placar ao vivo de cada partida ativa, indexado por matchId
    private val _liveScores = MutableStateFlow<Map<String, LiveMatch>>(emptyMap())
    val liveScores: StateFlow<Map<String, LiveMatch>> = _liveScores.asStateFlow()

    // Um Job por partida — todos vivem no escopo do ViewModel PAI, não do widget
    private val liveMatchJobs = mutableMapOf<String, Job>()

    fun toggleMatchTracking(matchId: String) {
        if (matchId in _trackedMatchIds.value) {
            stopTracking(matchId)
        }
        else startTracking(matchId)
    }

    private fun startTracking(matchId: String) {
        if (liveMatchJobs.containsKey(matchId)) {
            return
        }

        Log.d(TAG, "Iniciando coleta — matchId=$matchId (Jobs ativos após: ${liveMatchJobs.size + 1})")

        _trackedMatchIds.update {
            it.plus(matchId)
        }

        liveMatchJobs[matchId] = viewModelScope.launch {
            repository.observeLiveMatch(matchId).collect { match ->
                _liveScores.update { current -> current + (matchId to match) }
            }
        }
    }

    private fun stopTracking(matchId: String) {
        Log.d(TAG, "Cancelando coleta — matchId=$matchId (Jobs restantes após: ${liveMatchJobs.size - 1})")
        liveMatchJobs[matchId]?.cancel()
        liveMatchJobs.remove(matchId)
        _trackedMatchIds.update { list -> list.filterNot { it == matchId } }
        _liveScores.update { it - matchId }
    }

    override fun onCleared() {
        super.onCleared()
        Log.d(TAG, "onCleared — cancelando todos os ${liveMatchJobs.size} Jobs ativos")
        liveMatchJobs.values.forEach { it.cancel() }
        liveMatchJobs.clear()
    }
}
