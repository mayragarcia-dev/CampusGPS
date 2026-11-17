package com.garcia.campusgps.data

import com.garcia.campusgps.model.Lugar

interface LugaresRepository {
    fun obtenerLugares(): List<Lugar>
}

