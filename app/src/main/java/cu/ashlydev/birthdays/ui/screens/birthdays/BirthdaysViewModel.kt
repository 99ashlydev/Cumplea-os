/*
 * Copyright (c) 2026+ Ashly Dev. Todos los derechos reservados.
 * Licencia: GPL-3.0 (ver archivo LICENSE en la raíz del proyecto).
 */
package cu.ashlydev.birthdays.ui.screens.birthdays

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cu.ashlydev.birthdays.data.repository.PersonRepository
import cu.ashlydev.birthdays.domain.model.Person
import cu.ashlydev.birthdays.domain.usecase.GetUpcomingBirthdaysUseCase
import cu.ashlydev.birthdays.domain.usecase.UpcomingBirthday
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

data class BirthdaysUiState(
    val items: List<UpcomingBirthday> = emptyList(),
    val query: String = "",
    val loading: Boolean = true
)

@HiltViewModel
class BirthdaysViewModel @Inject constructor(
    personRepository: PersonRepository,
    private val getUpcoming: GetUpcomingBirthdaysUseCase
) : ViewModel() {

    private val query = MutableStateFlow("")

    val uiState: StateFlow<BirthdaysUiState> =
        combine(personRepository.observeAll(), query) { persons, q ->
            val filtered = if (q.isBlank()) persons else persons.filter {
                it.nombre.contains(q, ignoreCase = true)
            }
            BirthdaysUiState(
                items = getUpcoming(filtered),
                query = q,
                loading = false
            )
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), BirthdaysUiState())

    fun onQueryChange(q: String) { query.value = q }
}