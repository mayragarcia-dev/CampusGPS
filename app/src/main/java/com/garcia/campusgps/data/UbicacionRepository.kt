package com.garcia.campusgps.data

import com.garcia.campusgps.model.Posicion

interface UbicacionRepository {
    fun playServicesDisponible(): Boolean
    suspend fun obtenerPosicion(): Result<Posicion>
}

class UbicacionNoDisponibleException : Exception()

