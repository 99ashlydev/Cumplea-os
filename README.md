# Cumpleaños 🎂

<!--
 Copyright (c) 2026+ Ashly Dev. Todos los derechos reservados.
 Licencia: GPL-3.0 (ver archivo LICENSE en la raíz del proyecto).
-->

App Android para gestionar cumpleaños y recibir avisos festivos el día indicado.
Escrita en **Kotlin + Jetpack Compose + Material 3**.

## ✨ Funciones

- Lista de cumpleaños ordenada por próximo en llegar, con cuenta regresiva.
- Vista **Hoy** (cumples de hoy + próximos 7 días).
- Vista **Calendario** mensual con marcadores.
- **Estadísticas** con racha, logros y gráficos.
- **Ajustes** con hora, tono y aviso anticipado configurables.
- Notificación **full-screen festiva** el día del cumpleaños.
- Recuperación de alarmas perdidas tras reiniciar el dispositivo.
- Botón **"Felicitación por WhatsApp"** con mensaje prellenado.
- Widget de pantalla de inicio + Quick Settings Tile.
- Backup global (export/import JSON) + Auto Backup de Android.
- Importación puntual desde contactos locales.
- 100% offline, sin anuncios, sin tracking.

## 🛠️ Stack

| Capa | Tecnología |
|---|---|
| Lenguaje | Kotlin 2.0.21 |
| UI | Jetpack Compose + Material 3 |
| Arquitectura | MVVM + Clean (ligero) |
| Persistencia | Room + DataStore |
| DI | Hilt |
| Navegación | Navigation Compose |
| Fechas | java.time (API 28+, sin desugaring) |
| Build | AGP 8.7.3 + Gradle 8.9 + KSP |
| CI | GitHub Actions (Linux, JDK 17) |

## 📦 Requisitos

- Android 9 (API 28) o superior.
- Android Studio Ladybug o superior (para desarrollo local).

## 🚀 Compilar

### Local

```bash
git clone https://github.com/<tu-usuario>/<tu-repo>.git
cd <tu-repo>
# Genera el wrapper si no está
gradle wrapper --gradle-version 8.9
./gradlew assembleDebug