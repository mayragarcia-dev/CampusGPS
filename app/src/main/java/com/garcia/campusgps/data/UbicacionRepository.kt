package com.garcia.campusgps.data

import com.garcia.campusgps.model.Posicion

interface UbicacionRepository {
    suspend fun obtenerPosicion(): Result<Posicion>
}

