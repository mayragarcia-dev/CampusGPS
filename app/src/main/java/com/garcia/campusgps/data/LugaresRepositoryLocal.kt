package com.garcia.campusgps.data

import com.garcia.campusgps.model.Lugar

class LugaresRepositoryLocal : LugaresRepository {
    // Puntos de demostración dentro del campus Tecsup Lima.
    override fun obtenerLugares(): List<Lugar> = listOf(
        Lugar(1, "Biblioteca Central", "Estudio", -12.04375, -76.95318),
        Lugar(2, "Cafetería Campus", "Comida", -12.04418, -76.95253),
        Lugar(3, "Laboratorio de Cómputo", "Estudio", -12.04338, -76.95237),
        Lugar(4, "Auditorio Principal", "Eventos", -12.04482, -76.95298),
        Lugar(5, "Estacionamiento Norte", "Servicios", -12.04278, -76.95286)
    )
}

