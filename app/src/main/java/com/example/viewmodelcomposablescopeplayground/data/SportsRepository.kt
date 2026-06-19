package com.example.viewmodelcomposablescopeplayground.data

import android.util.Log
import com.example.viewmodelcomposablescopeplayground.data.model.LiveMatch
import com.example.viewmodelcomposablescopeplayground.data.model.MatchSchedule
import com.example.viewmodelcomposablescopeplayground.data.model.NewsPost
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class SportsRepository {

    // --- Notícias ---

    val newsFeed: List<NewsPost> = listOf(
        NewsPost("1", "BRASIL 3 x 0 CROÁCIA — Seleção arrasa e lidera o Grupo C na Copa 2026", "Copa 2026", "8 min atrás"),
        NewsPost("2", "Vinicius Jr. faz hat-trick e é eleito craque da rodada pelo 4° jogo seguido", "Copa 2026", "22 min atrás"),
        NewsPost("3", "ARGENTINA 2 x 1 MARROCOS — Messi decide nos acréscimos e classifica La Albiceleste", "Copa 2026", "45 min atrás"),
        NewsPost("4", "CONFIRMADO: Brasil x Argentina nas oitavas — o clássico que o mundo esperava", "Copa 2026", "1 h atrás"),
        NewsPost("5", "Mbappé brilha: FRANÇA 4 x 1 AUSTRÁLIA — Les Bleus vencem de goleada na estreia", "Copa 2026", "2 h atrás"),
        NewsPost("6", "SURPRESA! JAPÃO elimina PORTUGAL nos pênaltis e vai às quartas pela 1ª vez", "Copa 2026", "3 h atrás"),
        NewsPost("7", "MetLife Arena, Nova York: 94 mil torcedores — novo recorde de público em Copas", "Copa 2026", "4 h atrás"),
        NewsPost("8", "Tite fala sobre a final: 'Nosso grupo está mais unido do que em 2022'", "Copa 2026", "5 h atrás"),
        NewsPost("9", "ESPANHA x ALEMANHA confirmadas nas quartas — duelo de campeões mundiais", "Copa 2026", "6 h atrás"),
        NewsPost("10", "Rodrygo assume titularidade e responde com dois gols na vitória do Brasil", "Copa 2026", "7 h atrás"),
    )

    // --- Grade dos Jogos de Hoje ---

    val todayMatches: List<MatchSchedule> = listOf(
        MatchSchedule(
            matchId    = "wc_r16_bra_arg",
            homeTeam   = "Brasil",   homeFlag = "🇧🇷",
            awayTeam   = "Argentina", awayFlag = "🇦🇷",
            round      = "Oitavas de Final",
            venue      = "MetLife Arena · Nova York",
            kickoffBrt = "AO VIVO"
        ),
        MatchSchedule(
            matchId    = "wc_r16_fra_ger",
            homeTeam   = "França",   homeFlag = "🇫🇷",
            awayTeam   = "Alemanha", awayFlag = "🇩🇪",
            round      = "Oitavas de Final",
            venue      = "SoFi Stadium · Los Angeles",
            kickoffBrt = "AO VIVO"
        ),
        MatchSchedule(
            matchId    = "wc_r16_esp_eng",
            homeTeam   = "Espanha",   homeFlag = "🇪🇸",
            awayTeam   = "Inglaterra", awayFlag = "🇬🇧",
            round      = "Oitavas de Final",
            venue      = "AT&T Stadium · Dallas",
            kickoffBrt = "AO VIVO"
        ),
        MatchSchedule(
            matchId    = "wc_r16_por_jap",
            homeTeam   = "Portugal", homeFlag = "🇵🇹",
            awayTeam   = "Japão",    awayFlag = "🇯🇵",
            round      = "Oitavas de Final",
            venue      = "NRG Stadium · Houston",
            kickoffBrt = "AO VIVO"
        ),
    )

    // --- Cenários ao vivo ---
    //
    // Cada partida tem um estado inicial e eventos de gol por minuto.
    // Isso permite que cada Flow seja INDEPENDENTE com narrativa própria,
    // demonstrando por que cada LiveMatchWidget precisa de seu próprio
    // ciclo de vida — e não deve depender de um ViewModel Pai centralizado.

    private data class MatchScenario(
        val homeTeam: String,
        val awayTeam: String,
        val startMinute: Int,
        val startHomeScore: Int,
        val startAwayScore: Int,
        // minuto → (placar casa, placar fora) após esse segundo
        val events: Map<Int, Pair<Int, Int>>
    )

    private val matchScenarios: Map<String, MatchScenario> = mapOf(

        // Brasil 1-0 Argentina (67') → Argentina empata 1-1 (74') → Brasil vira 2-1 (89')
        "wc_r16_bra_arg" to MatchScenario(
            homeTeam = "Brasil", awayTeam = "Argentina",
            startMinute = 67, startHomeScore = 1, startAwayScore = 0,
            events = mapOf(73 to (1 to 1), 88 to (2 to 1))
        ),

        // França 1-0 Alemanha (22') → Alemanha empata 1-1 (31') → França vira 2-1 (57') → Alemanha 2-2 (79')
        "wc_r16_fra_ger" to MatchScenario(
            homeTeam = "França", awayTeam = "Alemanha",
            startMinute = 22, startHomeScore = 1, startAwayScore = 0,
            events = mapOf(31 to (1 to 1), 57 to (2 to 1), 79 to (2 to 2))
        ),

        // Espanha x Inglaterra 0-0 (46', início do 2° tempo) → Espanha 1-0 (64') → Inglaterra 1-1 (78')
        "wc_r16_esp_eng" to MatchScenario(
            homeTeam = "Espanha", awayTeam = "Inglaterra",
            startMinute = 46, startHomeScore = 0, startAwayScore = 0,
            events = mapOf(64 to (1 to 0), 78 to (1 to 1))
        ),

        // Portugal x Japão 1-1 (87') → congela no empate → prorrogação! Suspense total
        "wc_r16_por_jap" to MatchScenario(
            homeTeam = "Portugal", awayTeam = "Japão",
            startMinute = 87, startHomeScore = 1, startAwayScore = 1,
            events = emptyMap()
        ),
    )

    fun observeLiveMatch(matchId: String): Flow<LiveMatch> = flow {
        val scenario = matchScenarios[matchId] ?: return@flow
        var homeScore = scenario.startHomeScore
        var awayScore = scenario.startAwayScore
        var minute = scenario.startMinute

        while (true) {
            Log.d(
                "SportsRepository",
                "Conexão de fluxo ativa — ${scenario.homeTeam} x ${scenario.awayTeam}, " +
                        "minuto=$minute, placar=${homeScore}x${awayScore}"
            )
            emit(
                LiveMatch(
                    matchId    = matchId,
                    homeTeam   = scenario.homeTeam,
                    awayTeam   = scenario.awayTeam,
                    homeScore  = homeScore,
                    awayScore  = awayScore,
                    matchMinute = minute,
                    isLive     = true
                )
            )
            delay(1_000)
            // Aplica evento de gol deste minuto, se houver
            scenario.events[minute]?.let { (hs, as_) ->
                homeScore = hs
                awayScore = as_
            }
            if (minute < 90) minute++ else minute = 90 // congela no tempo regulamentar
        }
    }
}
