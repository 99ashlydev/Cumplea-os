/*
 * Copyright (c) 2026+ Ashly Dev. Todos los derechos reservados.
 * Licencia: GPL-3.0 (ver archivo LICENSE en la raíz del proyecto).
 */
package cu.ashlydev.birthdays.ui.alarm

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cu.ashlydev.birthdays.data.repository.PersonRepository
import cu.ashlydev.birthdays.ui.theme.ConfettiColors
import cu.ashlydev.birthdays.ui.theme.CumpleanosTheme
import cu.ashlydev.birthdays.whatsapp.WhatsAppHelper
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class AlarmActivity : ComponentActivity() {

    @Inject lateinit var personRepository: PersonRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setShowWhenLocked(true)
        setTurnScreenOn(true)
        setContent {
            CumpleanosTheme {
                AlarmContent(this)
            }
        }
    }

    companion object {
        const val EXTRA_PERSON_ID = "extra_person_id"
        const val EXTRA_PERSON_NAME = "extra_person_name"
        const val EXTRA_PERSON_AGE = "extra_person_age"
        const val EXTRA_DISMISS_ID = "extra_dismiss_id"
        const val ACTION_DISMISS = "cu.ashlydev.birthdays.ACTION_DISMISS"
    }
}

@Composable
private fun AlarmContent(activity: AlarmActivity) {
    val context = LocalContext.current
    val personId = activity.intent.getLongExtra(AlarmActivity.EXTRA_PERSON_ID, -1L)
    val name = activity.intent.getStringExtra(AlarmActivity.EXTRA_PERSON_NAME) ?: "🎉"
    val age = activity.intent.getIntExtra(AlarmActivity.EXTRA_PERSON_AGE, 0)

    var person by remember { mutableStateOf<cu.ashlydev.birthdays.domain.model.Person?>(null) }
    LaunchedEffect(personId) {
        if (personId > 0) person = activity.personRepository.getById(personId)
    }

    val gradient = remember {
        Brush.verticalGradient(ConfettiColors.shuffled())
    }

    var confettiTick by remember { mutableStateOf(0) }
    LaunchedEffect(Unit) {
        while (true) { delay(800); confettiTick++ }
    }

    Box(
        modifier = Modifier.fillMaxSize().background(gradient),
        contentAlignment = Alignment.Center
    ) {
        ConfettiOverlay(tick = confettiTick)

        Column(
            modifier = Modifier.fillMaxWidth().padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text("🎂", fontSize = 72.sp)
            Spacer(Modifier.height(16.dp))
            Text(
                "¡Feliz cumpleaños!",
                style = MaterialTheme.typography.headlineLarge,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
            Text(
                name,
                style = MaterialTheme.typography.headlineMedium,
                color = Color.White,
                textAlign = TextAlign.Center
            )
            if (age > 0) {
                Text(
                    "Cumple $age años",
                    style = MaterialTheme.typography.titleLarge,
                    color = Color.White
                )
            }
            Spacer(Modifier.height(32.dp))
            person?.let { p ->
                Button(
                    onClick = { WhatsAppHelper.openWhatsApp(context, p) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.White, contentColor = Color.Black)
                ) { Text("Felicitación por WhatsApp") }
                Spacer(Modifier.height(12.dp))
            }
            OutlinedButton(
                onClick = { activity.finish() },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White)
            ) { Text("Descartar") }
        }
    }
}

@Composable
private fun ConfettiOverlay(tick: Int) {
    val animatedProgress by animateFloatAsState(
        targetValue = tick.toFloat(),
        animationSpec = tween(800),
        label = "confetti"
    )
    androidx.compose.foundation.Canvas(Modifier.fillMaxSize()) {
        val w = size.width
        val h = size.height
        val colors = ConfettiColors
        repeat(30) { i ->
            val baseX = (i * 137) % w.toInt()
            val baseY = ((i * 73 + (animatedProgress * 50).toInt()) % h.toInt())
            drawCircle(
                color = colors[i % colors.size].copy(alpha = 0.7f),
                radius = 8f + (i % 5) * 2f,
                center = Offset(baseX.toFloat(), baseY.toFloat())
            )
        }
    }
}