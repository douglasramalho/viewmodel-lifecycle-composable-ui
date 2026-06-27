package com.example.viewmodelcomposablescopeplayground.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import androidx.lifecycle.viewmodel.compose.rememberViewModelStoreOwner
import androidx.lifecycle.viewmodel.compose.rememberViewModelStoreProvider
import com.example.viewmodelcomposablescopeplayground.data.model.LiveMatch
import com.example.viewmodelcomposablescopeplayground.data.model.MatchSchedule
import com.example.viewmodelcomposablescopeplayground.data.model.NewsPost
import com.example.viewmodelcomposablescopeplayground.ui.theme.ViewModelComposableScopePlaygroundTheme
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

// ---------------------------------------------------------------------------
// Ponto de entrada — injeta o único ViewModel via Koin e coleta 4 StateFlows.
// PONTO DE REFATORAÇÃO: após o tutorial, trackedMatchIds e liveScores saem
// daqui e ficam encapsulados em cada LiveMatchViewModel scopado ao widget.
// ---------------------------------------------------------------------------
@Composable
fun MainSportsScreen() {
    val viewModel: FeedParentViewModel = koinViewModel()

    val newsFeed by viewModel.newsFeed.collectAsStateWithLifecycle()
    val todayMatches by viewModel.todayMatches.collectAsStateWithLifecycle()
    val trackedMatchIds by viewModel.trackedMatchIds.collectAsStateWithLifecycle()

    MainSportsScreenContent(
        newsFeed = newsFeed,
        todayMatches = todayMatches,
        trackedMatchIds = trackedMatchIds,
        onToggleMatch = viewModel::toggleMatchTracking
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MainSportsScreenContent(
    newsFeed: List<NewsPost>,
    todayMatches: List<MatchSchedule>,
    trackedMatchIds: List<String>,
    onToggleMatch: (String) -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(text = "ArenaInfo", style = MaterialTheme.typography.titleLarge)
                        Text(
                            text = "Copa do Mundo 2026",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { innerPadding ->
        val storeProvider = rememberViewModelStoreProvider()
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(0.dp)
        ) {

            // ── Seção: Jogos de Hoje ────────────────────────────────────────
            item(key = "header_today") {
                SectionHeader(
                    title = "Jogos de Hoje",
                    badge = "${todayMatches.count { it.kickoffBrt == "AO VIVO" }} ao vivo",
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
                )
            }
            item(key = "today_matches_row") {
                TodayMatchesRow(
                    matches = todayMatches,
                    trackedMatchIds = trackedMatchIds,
                    onToggleMatch = onToggleMatch
                )
            }

            // ── Seção: Acompanhando Ao Vivo (condicional) ──────────────────
            //
            // PONTO DE REFATORAÇÃO: este bloco itera sobre trackedMatchIds para
            // renderizar cada LiveMatchWidget — e passa o placar coletado pelo
            // ViewModel Pai. Após o refactoring, cada widget receberá apenas
            // o matchId e buscará seu próprio estado via LiveMatchViewModel.

            item(key = "header_live") {
                if (trackedMatchIds.isNotEmpty()) {
                    SectionHeader(
                        title = "Acompanhando Ao Vivo",
                        badge = "${trackedMatchIds.size}",
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
                    )
                }
            }

            items(trackedMatchIds, key = { "live_widget_$it" }) { matchId ->
                val scopedOwner = rememberViewModelStoreOwner(
                    provider = storeProvider,
                    key = "live_widget_$matchId"
                )
                CompositionLocalProvider(LocalViewModelStoreOwner provides scopedOwner) {
                    val liveMatchViewModel = koinViewModel<LiveMatchViewModel> {
                        parametersOf(matchId)
                    }

                    val liveScore by liveMatchViewModel.liveScore.collectAsStateWithLifecycle()

                    LiveMatchWidget(
                        match = liveScore,
                        modifier = Modifier
                            .animateItem()
                            .padding(horizontal = 16.dp)
                            .padding(bottom = 8.dp)
                    )
                }
            }

            // ── Divider ─────────────────────────────────────────────────────
            item(key = "divider") {
                HorizontalDivider(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                    color = MaterialTheme.colorScheme.outlineVariant
                )
            }

            // ── Seção: Últimas Notícias ─────────────────────────────────────
            item(key = "header_news") {
                SectionHeader(
                    title = "Últimas Notícias",
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
                )
            }

            items(newsFeed, key = { "news_${it.id}" }) { post ->
                NewsPostCard(
                    post = post,
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .padding(bottom = 10.dp)
                )
            }
        }
    }
}

// ---------------------------------------------------------------------------
// Seção horizontal de jogos — LazyRow com MatchScheduleCard por partida
// ---------------------------------------------------------------------------
@Composable
private fun TodayMatchesRow(
    matches: List<MatchSchedule>,
    trackedMatchIds: List<String>,
    onToggleMatch: (String) -> Unit
) {
    LazyRow(
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(matches, key = { it.matchId }) { match ->
            MatchScheduleCard(
                match = match,
                isTracking = match.matchId in trackedMatchIds,
                onToggle = { onToggleMatch(match.matchId) }
            )
        }
    }
}

@Composable
private fun MatchScheduleCard(
    match: MatchSchedule,
    isTracking: Boolean,
    onToggle: () -> Unit
) {
    val containerColor = if (isTracking)
        MaterialTheme.colorScheme.primaryContainer
    else
        MaterialTheme.colorScheme.surfaceVariant

    Card(
        modifier = Modifier.width(176.dp),
        colors = CardDefaults.cardColors(containerColor = containerColor),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (isTracking) 6.dp else 2.dp
        )
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            // Round label
            Text(
                text = match.round,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.secondary,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(2.dp))

            // Times
            Text(
                text = "${match.homeFlag}  ${match.homeTeam}",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "vs",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = "${match.awayFlag}  ${match.awayTeam}",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(2.dp))

            // Estádio
            Text(
                text = match.venue,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            // Status / horário
            val isLiveNow = match.kickoffBrt == "AO VIVO"
            Text(
                text = if (isLiveNow) "● ${match.kickoffBrt}" else match.kickoffBrt,
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                color = if (isLiveNow) MaterialTheme.colorScheme.error
                else MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(4.dp))

            // Botão de acompanhar
            if (isTracking) {
                Button(
                    onClick = onToggle,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    ),
                    contentPadding = PaddingValues(vertical = 6.dp)
                ) {
                    Text("✓ Ao Vivo", style = MaterialTheme.typography.labelMedium)
                }
            } else {
                OutlinedButton(
                    onClick = onToggle,
                    modifier = Modifier.fillMaxWidth(),
                    contentPadding = PaddingValues(vertical = 6.dp)
                ) {
                    Text("Acompanhar", style = MaterialTheme.typography.labelMedium)
                }
            }
        }
    }
}

// ---------------------------------------------------------------------------
// Componentes utilitários
// ---------------------------------------------------------------------------

@Composable
private fun SectionHeader(
    title: String,
    modifier: Modifier = Modifier,
    badge: String? = null
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = title, style = MaterialTheme.typography.titleMedium)
        if (badge != null) {
            Surface(
                color = MaterialTheme.colorScheme.secondaryContainer,
                shape = MaterialTheme.shapes.small
            ) {
                Text(
                    text = badge,
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSecondaryContainer,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                )
            }
        }
    }
}

@Composable
private fun NewsPostCard(post: NewsPost, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                AssistChip(
                    onClick = {},
                    label = {
                        Text(post.category, style = MaterialTheme.typography.labelSmall)
                    }
                )
                Text(
                    text = post.timeAgo,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Text(text = post.title, style = MaterialTheme.typography.bodyLarge)
        }
    }
}

// ---------------------------------------------------------------------------
// Previews
// ---------------------------------------------------------------------------

private val previewMatches = listOf(
    MatchSchedule(
        "wc_r16_bra_arg", "Brasil", "Argentina", "🇧🇷", "🇦🇷",
        "Oitavas de Final", "MetLife Arena · Nova York", "AO VIVO"
    ),
    MatchSchedule(
        "wc_r16_fra_ger", "França", "Alemanha", "🇫🇷", "🇩🇪",
        "Oitavas de Final", "SoFi Stadium · Los Angeles", "AO VIVO"
    ),
    MatchSchedule(
        "wc_r16_esp_eng", "Espanha", "Inglaterra", "🇪🇸", "🇬🇧",
        "Oitavas de Final", "AT&T Stadium · Dallas", "AO VIVO"
    ),
    MatchSchedule(
        "wc_r16_por_jap", "Portugal", "Japão", "🇵🇹", "🇯🇵",
        "Oitavas de Final", "NRG Stadium · Houston", "AO VIVO"
    ),
)

private val previewNews = listOf(
    NewsPost(
        "1",
        "BRASIL 3 x 0 CROÁCIA — Seleção arrasa e lidera o Grupo C",
        "Copa 2026",
        "8 min atrás"
    ),
    NewsPost(
        "2",
        "Vinicius Jr. faz hat-trick e é eleito craque da rodada",
        "Copa 2026",
        "22 min atrás"
    ),
    NewsPost(
        "3",
        "ARGENTINA 2 x 1 MARROCOS — Messi decide nos acréscimos",
        "Copa 2026",
        "45 min atrás"
    ),
)

private val previewTracked = listOf("wc_r16_bra_arg", "wc_r16_fra_ger")

private val previewLiveScores = mapOf(
    "wc_r16_bra_arg" to LiveMatch("wc_r16_bra_arg", "Brasil", "Argentina", 2, 1, 89, true),
    "wc_r16_fra_ger" to LiveMatch("wc_r16_fra_ger", "França", "Alemanha", 2, 2, 79, true),
)

@Preview(showBackground = true, name = "Dois jogos ao vivo + feed")
@Composable
private fun MainScreenTwoLivePreview() {
    ViewModelComposableScopePlaygroundTheme {
        MainSportsScreenContent(
            newsFeed = previewNews,
            todayMatches = previewMatches,
            trackedMatchIds = previewTracked,
            onToggleMatch = {}
        )
    }
}

@Preview(showBackground = true, name = "Nenhum jogo acompanhado")
@Composable
private fun MainScreenNoLivePreview() {
    ViewModelComposableScopePlaygroundTheme {
        MainSportsScreenContent(
            newsFeed = previewNews,
            todayMatches = previewMatches,
            trackedMatchIds = emptyList(),
            onToggleMatch = {}
        )
    }
}
