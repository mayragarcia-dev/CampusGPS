package com.garcia.campusgps

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class PaqueteAplicacionTest {
    @Test
    fun usaElPaqueteCampusGps() {
        val contexto = InstrumentationRegistry.getInstrumentation().targetContext

        assertEquals("com.garcia.campusgps", contexto.packageName)
    }
}
