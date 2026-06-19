package com.example.viewmodelcomposablescopeplayground.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.viewmodelcomposablescopeplayground.data.model.LiveMatch
import com.example.viewmodelcomposablescopeplayground.ui.theme.ViewModelComposableScopePlaygroundTheme

// modifier é exposto para permitir animateItem() da LazyColumn no site de chamada
@Composable
fun LiveMatchWidget(match: LiveMatch?, modifier: Modifier = Modifier) {
    AnimatedVisibility(
        visible = match != null,
        modifier = modifier,
        enter = fadeIn(),
        exit = fadeOut()
    ) {
        match?.let {
            LiveMatchCard(it)
        }
    }
}

@Composable
private fun LiveMatchCard(match: LiveMatch) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "● AO VIVO",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    ),
                    modifier = Modifier
                        .background(Color(0xFFD32F2F), RoundedCornerShape(4.dp))
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                )
                Text(
                    text = "Oitavas · Copa 2026",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                )
                Text(
                    text = "${match.matchMinute}'",
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = match.homeTeam,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                Text(
                    text = "${match.homeScore}  ×  ${match.awayScore}",
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 30.sp
                    ),
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = match.awayTeam,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
        }
    }
}

// --- Previews ---

@Preview(showBackground = true, name = "Brasil 2×1 Argentina — 89'")
@Composable
private fun LiveMatchWidgetBraArgPreview() {
    ViewModelComposableScopePlaygroundTheme {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            LiveMatchWidget(
                match = LiveMatch("wc_r16_bra_arg", "Brasil", "Argentina", 2, 1, 89, true)
            )
            LiveMatchWidget(
                match = LiveMatch("wc_r16_fra_ger", "França", "Alemanha", 2, 2, 79, true)
            )
        }
    }
}

@Preview(showBackground = true, name = "Widget Oculto (null)")
@Composable
private fun LiveMatchWidgetHiddenPreview() {
    ViewModelComposableScopePlaygroundTheme {
        Column(modifier = Modifier.padding(16.dp)) {
            LiveMatchWidget(match = null)
        }
    }
}
