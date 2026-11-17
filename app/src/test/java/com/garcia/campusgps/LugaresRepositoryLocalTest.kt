package com.garcia.campusgps

import com.garcia.campusgps.data.LugaresRepositoryLocal
import org.junit.Assert.assertEquals
import org.junit.Test

class LugaresRepositoryLocalTest {
    @Test
    fun devuelveCincoLugaresConIdsUnicos() {
        val lugares = LugaresRepositoryLocal().obtenerLugares()

        assertEquals(5, lugares.size)
        assertEquals(lugares.size, lugares.map { it.id }.toSet().size)
    }
}
