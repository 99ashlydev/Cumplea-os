/*
 * Copyright (c) 2026+ Ashly Dev. Todos los derechos reservados.
 * Licencia: GPL-3.0 (ver archivo LICENSE en la raíz del proyecto).
 */
package cu.ashlydev.birthdays.data.mapper

import cu.ashlydev.birthdays.data.local.entity.GroupEntity
import cu.ashlydev.birthdays.data.local.entity.PersonEntity
import cu.ashlydev.birthdays.domain.model.Group
import cu.ashlydev.birthdays.domain.model.Person

fun PersonEntity.toDomain(): Person = Person(
    id = id,
    nombre = nombre,
    fechaNacimiento = fechaNacimiento,
    telefono = telefono,
    notas = notas,
    fotoUri = fotoUri,
    grupoId = grupoId,
    avisoAnticipadoDias = avisoAnticipadoDias,
    horaNotificacionMinutos = horaNotificacionMinutos,
    activo = activo
)

fun Person.toEntity(): PersonEntity = PersonEntity(
    id = id,
    nombre = nombre,
    fechaNacimiento = fechaNacimiento,
    telefono = telefono,
    notas = notas,
    fotoUri = fotoUri,
    grupoId = grupoId,
    avisoAnticipadoDias = avisoAnticipadoDias,
    horaNotificacionMinutos = horaNotificacionMinutos,
    activo = activo
)

fun GroupEntity.toDomain(): Group = Group(
    id = id,
    nombre = nombre,
    colorArgb = colorArgb
)

fun Group.toEntity(): GroupEntity = GroupEntity(
    id = id,
    nombre = nombre,
    colorArgb = colorArgb
)