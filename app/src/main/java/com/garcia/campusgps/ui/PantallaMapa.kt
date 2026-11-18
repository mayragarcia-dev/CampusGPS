package com.garcia.campusgps.ui

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.garcia.campusgps.model.UbicacionUi
import com.garcia.campusgps.util.formatearDistancia
import com.garcia.campusgps.viewmodel.MapaViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaMapa(viewModel: MapaViewModel) {
    val context = LocalContext.current
    val estado by viewModel.estado.collectAsStateWithLifecycle()
    val lugares by viewModel.lugares.collectAsStateWithLifecycle()
    val seleccionado by viewModel.seleccionado.collectAsStateWithLifecycle()
    val masCercano by viewModel.masCercano.collectAsStateWithLifecycle()
    var solicitudReencuadre by remember { mutableIntStateOf(0) }

    fun seleccionarLugar(id: Int) {
        if (id == seleccionado) solicitudReencuadre++
        viewModel.seleccionar(id)
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { resultados ->
        if (resultados.values.any { it }) viewModel.buscar()
        else viewModel.permisoDenegado()
    }

    Scaffold(topBar = {
        TopAppBar(
            title = { Text("CampusGPS") },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.primary,
                titleContentColor = MaterialTheme.colorScheme.onPrimary
            )
        )
    }) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            MapaUbicacion(
                estado = estado,
                lugares = lugares,
                seleccionado = seleccionado,
                masCercanoId = masCercano?.id,
                solicitudReencuadre = solicitudReencuadre,
                modifier = Modifier.weight(1f)
            )

            Button(
                modifier = Modifier.padding(top = 12.dp),
                onClick = {
                    val tienePermisoPreciso = ContextCompat.checkSelfPermission(
                        context,
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ) == PackageManager.PERMISSION_GRANTED
                    val tienePermisoAproximado = ContextCompat.checkSelfPermission(
                        context,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    ) == PackageManager.PERMISSION_GRANTED

                    if (tienePermisoPreciso || tienePermisoAproximado) {
                        viewModel.buscar()
                    } else {
                        permissionLauncher.launch(
                            arrayOf(
                                Manifest.permission.ACCESS_FINE_LOCATION,
                                Manifest.permission.ACCESS_COARSE_LOCATION
                            )
                        )
                    }
                },
                enabled = estado !is UbicacionUi.Buscando
            ) {
                Text(if (estado is UbicacionUi.Buscando) "Buscando…" else "Obtener mi ubicación")
            }

            Spacer(Modifier.height(8.dp))

            when (val current = estado) {
                UbicacionUi.Inicial -> Text("Pulsa el botón para obtener tu ubicación")
                UbicacionUi.Buscando -> CircularProgressIndicator(modifier = Modifier.padding(8.dp))
                is UbicacionUi.Lista -> Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Latitud: ${current.posicion.latitud}")
                    Text("Longitud: ${current.posicion.longitud}")
                }
                is UbicacionUi.Error -> Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = MaterialTheme.colorScheme.errorContainer,
                    shape = MaterialTheme.shapes.medium
                ) {
                    Text(
                        text = current.mensaje,
                        modifier = Modifier.padding(16.dp),
                        color = MaterialTheme.colorScheme.onErrorContainer
                    )
                }
            }
            OutlinedButton(
                enabled = masCercano != null,
                onClick = { masCercano?.let { seleccionarLugar(it.id) } }
            ) {
                Text("Ir al más cercano")
            }
            masCercano?.let { lugar ->
                lugares.firstOrNull { it.lugar.id == lugar.id }?.metros?.let { metros ->
                    Text(
                        text = "Más cercano: ${lugar.nombre} (${formatearDistancia(metros)})",
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
            ListaLugares(
                lugares = lugares,
                seleccionado = seleccionado,
                onClick = ::seleccionarLugar,
                modifier = Modifier.fillMaxWidth().height(220.dp)
            )
            Spacer(Modifier.height(12.dp))
        }
    }
}

