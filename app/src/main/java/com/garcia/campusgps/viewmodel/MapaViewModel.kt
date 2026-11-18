package com.garcia.campusgps.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.garcia.campusgps.data.LugaresRepository
import com.garcia.campusgps.data.UbicacionNoDisponibleException
import com.garcia.campusgps.data.UbicacionRepository
import com.garcia.campusgps.model.Lugar
import com.garcia.campusgps.model.LugarConDistancia
import com.garcia.campusgps.model.Posicion
import com.garcia.campusgps.model.TipoError
import com.garcia.campusgps.model.UbicacionUi
import com.garcia.campusgps.util.distanciaMetros
import com.garcia.campusgps.util.formatearDistancia
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

private const val ETIQUETA_DISTANCIAS = "CampusGPSDistancias"
private val errorSinPermiso = UbicacionUi.Error(
    "Sin permiso de ubicación no puedo mostrar dónde estás",
    TipoError.SIN_PERMISO
)
private val errorSinPlayServices = UbicacionUi.Error(
    "Este dispositivo no tiene Google Play Services.",
    TipoError.SIN_PLAY_SERVICES
)

class MapaViewModel(
    private val ubicacionRepo: UbicacionRepository,
    lugaresRepo: LugaresRepository
) : ViewModel() {
    private val _estado = MutableStateFlow<UbicacionUi>(
        if (ubicacionRepo.playServicesDisponible()) UbicacionUi.Inicial else errorSinPlayServices
    )
    val estado = _estado.asStateFlow()

    private val lugaresIniciales = lugaresRepo.obtenerLugares().map { LugarConDistancia(it, null) }
    private val _lugares = MutableStateFlow(lugaresIniciales)
    val lugares = _lugares.asStateFlow()
    val masCercano: StateFlow<Lugar?> = lugares
        .map { lista -> lista.firstOrNull { it.metros != null }?.lugar }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)

    private val _seleccionado = MutableStateFlow<Int?>(null)
    val seleccionado = _seleccionado.asStateFlow()

    fun seleccionar(id: Int?) {
        _seleccionado.value = id
    }

    private fun actualizarDistancias(pos: Posicion) {
        val ordenados = _lugares.value
            .map { (lugar) -> LugarConDistancia(lugar, distanciaMetros(pos, lugar)) }
            .sortedBy { it.metros }
        _lugares.value = ordenados

        if (Log.isLoggable(ETIQUETA_DISTANCIAS, Log.DEBUG)) {
            val resumen = ordenados.joinToString { (lugar, metros) ->
                "${lugar.nombre}: ${formatearDistancia(checkNotNull(metros))}"
            }
            Log.d(ETIQUETA_DISTANCIAS, resumen)
        }
    }

    fun buscar() {
        if (!ubicacionRepo.playServicesDisponible()) {
            mostrarError(errorSinPlayServices)
            return
        }
        _estado.value = UbicacionUi.Buscando
        seleccionar(null)
        viewModelScope.launch {
            ubicacionRepo.obtenerPosicion()
                .onSuccess { posicion ->
                    actualizarDistancias(posicion)
                    _estado.value = UbicacionUi.Lista(posicion)
                }
                .onFailure { error -> mostrarError(errorDe(error)) }
        }
    }

    fun permisoDenegado() {
        mostrarError(errorSinPermiso)
    }

    private fun mostrarError(error: UbicacionUi.Error) {
        _estado.value = error
        _lugares.value = lugaresIniciales
        seleccionar(null)
    }

    private fun errorDe(error: Throwable): UbicacionUi.Error = when (error) {
        is SecurityException -> errorSinPermiso
        is UbicacionNoDisponibleException -> UbicacionUi.Error(
            "No se pudo obtener la ubicación. Activa el GPS e intenta de nuevo.",
            TipoError.GPS_APAGADO
        )
        else -> UbicacionUi.Error("No se pudo obtener la ubicación. Inténtalo de nuevo.", TipoError.OTRO)
    }
}

