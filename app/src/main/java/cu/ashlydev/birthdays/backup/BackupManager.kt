/*
 * Copyright (c) 2026+ Ashly Dev. Todos los derechos reservados.
 * Licencia: GPL-3.0 (ver archivo LICENSE en la raíz del proyecto).
 */
package cu.ashlydev.birthdays.backup

import android.content.Context
import android.net.Uri
import cu.ashlydev.birthdays.data.repository.GroupRepository
import cu.ashlydev.birthdays.data.repository.PersonRepository
import cu.ashlydev.birthdays.domain.model.Group
import cu.ashlydev.birthdays.domain.model.Person
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.InputStream
import java.io.OutputStream
import java.time.LocalDate
import javax.inject.Inject
import javax.inject.Singleton

@Serializable
data class PersonBackup(
    val nombre: String,
    val fechaNacimiento: String,
    val telefono: String? = null,
    val notas: String? = null,
    val fotoUri: String? = null,
    val grupoNombre: String? = null,
    val avisoAnticipadoDias: Int? = null,
    val horaNotificacionMinutos: Int? = null
)

@Serializable
data class GroupBackup(val nombre: String, val colorArgb: Int)

@Serializable
data class BackupPayload(
    val version: Int = 1,
    val exportedAt: String,
    val groups: List<GroupBackup>,
    val persons: List<PersonBackup>
)

@Singleton
class BackupManager @Inject constructor(
    @ApplicationContext private val context: Context,
    private val personRepository: PersonRepository,
    private val groupRepository: GroupRepository
) {
    private val json = Json { prettyPrint = true; ignoreUnknownKeys = true }

    suspend fun exportTo(uri: Uri): Result<Unit> = runCatching {
        val persons = personRepository.getAllOnce()
        val groups = groupRepository.getAllOnce()
        val payload = BackupPayload(
            exportedAt = LocalDate.now().toString(),
            groups = groups.map { GroupBackup(it.nombre, it.colorArgb) },
            persons = persons.map { p ->
                PersonBackup(
                    nombre = p.nombre,
                    fechaNacimiento = p.fechaNacimiento.toString(),
                    telefono = p.telefono,
                    notas = p.notas,
                    fotoUri = p.fotoUri,
                    grupoNombre = groups.firstOrNull { it.id == p.grupoId }?.nombre,
                    avisoAnticipadoDias = p.avisoAnticipadoDias,
                    horaNotificacionMinutos = p.horaNotificacionMinutos
                )
            }
        )
        context.contentResolver.openOutputStream(uri)?.use { out: OutputStream ->
            out.write(json.encodeToString(payload).toByteArray())
        } ?: error("No se pudo abrir el archivo de salida")
    }

    suspend fun importFrom(uri: Uri): Result<Int> = runCatching {
        val text = context.contentResolver.openInputStream(uri)?.use { inp: InputStream ->
            inp.bufferedReader().readText()
        } ?: error("No se pudo leer el archivo")
        val payload = json.decodeFromString<BackupPayload>(text)

        val existingGroups = groupRepository.getAllOnce().toMutableList()
        val groupIdByName = existingGroups.associateBy { it.nombre }.toMutableMap()

        payload.groups.forEach { g ->
            if (!groupIdByName.containsKey(g.nombre)) {
                val id = groupRepository.upsert(Group(nombre = g.nombre, colorArgb = g.colorArgb))
                groupIdByName[g.nombre] = Group(id = id, nombre = g.nombre, colorArgb = g.colorArgb)
            }
        }

        var imported = 0
        payload.persons.forEach { p ->
            val grupoId = p.grupoNombre?.let { groupIdByName[it]?.id }
            personRepository.upsert(
                Person(
                    nombre = p.nombre,
                    fechaNacimiento = LocalDate.parse(p.fechaNacimiento),
                    telefono = p.telefono,
                    notas = p.notas,
                    fotoUri = p.fotoUri,
                    grupoId = grupoId,
                    avisoAnticipadoDias = p.avisoAnticipadoDias,
                    horaNotificacionMinutos = p.horaNotificacionMinutos
                )
            )
            imported++
        }
        imported
    }
}