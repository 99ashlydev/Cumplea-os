/*
 * Copyright (c) 2026+ Ashly Dev. Todos los derechos reservados.
 * Licencia: GPL-3.0 (ver archivo LICENSE en la raíz del proyecto).
 */
package cu.ashlydev.birthdays.data.local

import androidx.room.TypeConverter
import cu.ashlydev.birthdays.domain.model.AchievementKey
import java.time.LocalDate

class Converters {

    @TypeConverter
    fun localDateToString(value: LocalDate?): String? = value?.toString()

    @TypeConverter
    fun stringToLocalDate(value: String?): LocalDate? = value?.let(LocalDate::parse)

    @TypeConverter
    fun achievementKeyToString(value: AchievementKey): String = value.name

    @TypeConverter
    fun stringToAchievementKey(value: String): AchievementKey = AchievementKey.valueOf(value)
}