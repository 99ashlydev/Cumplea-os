/*
 * Copyright (c) 2026+ Ashly Dev. Todos los derechos reservados.
 * Licencia: GPL-3.0 (ver archivo LICENSE en la raíz del proyecto).
 */
package cu.ashlydev.birthdays.ui.screens.stats

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cu.ashlydev.birthdays.R
import cu.ashlydev.birthdays.data.local.entity.UserStatsEntity
import cu.ashlydev.birthdays.data.repository.AchievementRepository
import cu.ashlydev.birthdays.data.repository.GroupRepository
import cu.ashlydev.birthdays.data.repository.PersonRepository
import cu.ashlydev.birthdays.domain.model.AchievementKey
import cu.ashlydev.birthdays.domain.model.Group
import cu.ashlydev.birthdays.domain.model.Person
import cu.ashlydev.birthdays.domain.usecase.AchievementEvaluator
import cu.ashlydev.birthdays.ui.theme.ConfettiColors
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import java.time.LocalDate
import java.time.Period
import javax.inject.Inject

data class AchievementUi(
    val key: AchievementKey,
    val titulo: String,
    val descripcion: String,
    val emoji: String,
    val unlocked: Boolean
)

data class StatsUiState(
    val totalPersons: Int = 0,
    val thisMonth: Int = 0,
    val avgAge: Int = 0,
    val birthdayByMonth: List<Int> = List(12) { 0 },
    val groups: List<Group> = emptyList(),
    val groupCounts: Map<Long, Int> = emptyMap(),
    val achievements: List<AchievementUi> = emptyList(),
    val stats: UserStatsEntity = UserStatsEntity(),
    val top3: List<Person> = emptyList()
)

@HiltViewModel
class StatsViewModel @Inject constructor(
    personRepository: PersonRepository,
    groupRepository: GroupRepository,
    achievementRepository: AchievementRepository,
    private val evaluator: AchievementEvaluator
) : ViewModel() {
    val uiState: StateFlow<StatsUiState> = combine(
        personRepository.observeAll(),
        groupRepository.observeAll(),
        achievementRepository.observeAchievements(),
        achievementRepository.observeStats()
    ) { persons, groups, achievements, stats ->
        val today = LocalDate.now()
        val active = persons.filter { it.activo }
        val avg = if (active.isEmpty()) 0 else active.map {
            Period.between(it.fechaNacimiento, today).years
        }.average().toInt()

        val byMonth = IntArray(12) { 0 }
        active.forEach { byMonth[it.fechaNacimiento.monthValue - 1]++ }

        val unlockedMap = achievements.associate { it.key to it.desbloqueado }

        StatsUiState(
            totalPersons = active.size,
            thisMonth = active.count { it.fechaNacimiento.monthValue == today.monthValue },
            avgAge = avg,
            birthdayByMonth = byMonth.toList(),
            groups = groups,
            groupCounts = active.groupBy { it.grupoId ?: -1L }.mapValues { it.value.size },
            achievements = AchievementKey.entries.map { key ->
                val (t, d, e) = evaluator.metadata(key)
                AchievementUi(key, t, d, e, unlockedMap[key] == true)
            },
            stats = stats ?: UserStatsEntity(),
            top3 = active.sortedBy {
                val next = it.fechaNacimiento.withYear(today.year)
                val n = if (next.isBefore(today)) next.plusYears(1) else next
                java.time.temporal.ChronoUnit.DAYS.between(today, n)
            }.take(3)
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), StatsUiState())
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatsScreen(viewModel: StatsViewModel = hiltViewModel()) {
    val state by viewModel.uiState.collectAsState()
    var selectedTab by remember { mutableIntStateOf(0) }

    Scaffold(topBar = { TopAppBar(title = { Text(stringResource(R.string.tab_stats)) }) }) { padding ->
        Column(Modifier.padding(padding).fillMaxSize()) {
            SingleChoiceSegmentedButtonRow(Modifier.fillMaxWidth().padding(horizontal = 16.dp)) {
                SegmentedButton(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    shape = SegmentedButtonDefaults.itemShape(index = 0, count = 2)
                ) { Text("Resumen") }
                SegmentedButton(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    shape = SegmentedButtonDefaults.itemShape(index = 1, count = 2)
                ) { Text("Logros") }
            }

            if (selectedTab == 0) SummaryTab(state) else AchievementsTab(state)
        }
    }
}

@Composable
private fun SummaryTab(state: StatsUiState) {
    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)) {
                Column(Modifier.padding(16.dp)) {
                    Text("🔥 Racha actual: ${state.stats.rachaActual}", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                    Text("Mejor racha: ${state.stats.mejorRacha}")
                    Text("Felicitaciones enviadas: ${state.stats.felicitacionesEnviadas}")
                }
            }
        }
        item {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                StatCard("Personas", state.totalPersons.toString(), Modifier.weight(1f))
                StatCard("Este mes", state.thisMonth.toString(), Modifier.weight(1f))
                StatCard("Edad media", state.avgAge.toString(), Modifier.weight(1f))
            }
        }
        item {
            Card {
                Column(Modifier.padding(16.dp)) {
                    Text("Cumpleaños por mes", fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(12.dp))
                    MonthBarChart(state.birthdayByMonth)
                }
            }
        }
        if (state.groups.isNotEmpty()) {
            item {
                Card {
                    Column(Modifier.padding(16.dp)) {
                        Text("Distribución por grupo", fontWeight = FontWeight.Bold)
                        Spacer(Modifier.height(12.dp))
                        state.groups.forEach { g ->
                            val count = state.groupCounts[g.id] ?: 0
                            Row(Modifier.fillMaxWidth().padding(vertical = 4.dp), verticalAlignment = Alignment.CenterVertically) {
                                Box(Modifier.size(12.dp).clip(CircleShape).background(Color(g.colorArgb)))
                                Spacer(Modifier.width(8.dp))
                                Text(g.nombre, modifier = Modifier.weight(1f))
                                Text(count.toString(), fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
        item {
            Card {
                Column(Modifier.padding(16.dp)) {
                    Text("Próximos cumples", fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(8.dp))
                    state.top3.forEach { p ->
                        Text("🎂 ${p.nombre} — ${p.fechaNacimiento.dayOfMonth}/${p.fechaNacimiento.monthValue}")
                    }
                }
            }
        }
    }
}

@Composable
private fun AchievementsTab(state: StatsUiState) {
    val unlocked = state.achievements.count { it.unlocked }
    Column(Modifier.padding(16.dp)) {
        Text(
            "$unlocked / ${state.achievements.size} desbloqueados",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )
        Spacer(Modifier.height(12.dp))
        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(state.achievements, key = { it.key.name }) { a ->
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = if (a.unlocked) MaterialTheme.colorScheme.primaryContainer
                        else MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Column(
                        Modifier.padding(8.dp).aspectRatio(0.85f),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(a.emoji, style = MaterialTheme.typography.headlineMedium)
                        Text(
                            a.titulo,
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            maxLines = 2
                        )
                        if (!a.unlocked) Text("🔒", style = MaterialTheme.typography.labelSmall)
                    }
                }
            }
        }
    }
}

@Composable
private fun StatCard(title: String, value: String, modifier: Modifier = Modifier) {
    Card(modifier) {
        Column(Modifier.padding(12.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(value, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
            Text(title, style = MaterialTheme.typography.labelSmall)
        }
    }
}

@Composable
private fun MonthBarChart(data: List<Int>) {
    val max = (data.maxOrNull() ?: 1).coerceAtLeast(1)
    val months = listOf("E", "F", "M", "A", "M", "J", "J", "A", "S", "O", "N", "D")
    Canvas(modifier = Modifier.fillMaxWidth().height(120.dp)) {
        val barWidth = size.width / (data.size * 2f)
        data.forEachIndexed { i, v ->
            val h = (v.toFloat() / max) * size.height
            val x = i * (size.width / data.size) + barWidth / 2
            drawRect(
                color = if (i == LocalDate.now().monthValue - 1) ConfettiColors[0] else Color.Gray,
                topLeft = Offset(x, size.height - h),
                size = Size(barWidth, h)
            )
        }
    }
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceAround) {
        months.forEach { Text(it, style = MaterialTheme.typography.labelSmall) }
    }
}