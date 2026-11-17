package com.garcia.campusgps.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.garcia.campusgps.data.LugaresRepository
import com.garcia.campusgps.data.UbicacionRepository
import com.garcia.campusgps.model.LugarConDistancia
import com.garcia.campusgps.model.UbicacionUi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MapaViewModel(
    private val ubicacionRepo: UbicacionRepository,
    lugaresRepo: LugaresRepository
) : ViewModel() {
    private val _estado = MutableStateFlow<UbicacionUi>(UbicacionUi.Inicial)
    val estado = _estado.asStateFlow()

    private val _lugares = MutableStateFlow(
        lugaresRepo.obtenerLugares().map { LugarConDistancia(it, null) }
    )
    val lugares = _lugares.asStateFlow()

    fun buscar() {
        _estado.value = UbicacionUi.Buscando
        viewModelScope.launch {
            ubicacionRepo.obtenerPosicion()
                .onSuccess { posicion -> _estado.value = UbicacionUi.Lista(posicion) }
                .onFailure { error -> _estado.value = UbicacionUi.Error(mensajeDe(error)) }
        }
    }

    fun permisoDenegado() {
        _estado.value = UbicacionUi.Error("Sin permiso de ubicación no puedo mostrar dónde estás")
    }

    private fun mensajeDe(error: Throwable): String = when (error) {
        is SecurityException -> "Falta el permiso de ubicación"
        else -> error.message ?: "No se pudo obtener la ubicación"
    }
}

