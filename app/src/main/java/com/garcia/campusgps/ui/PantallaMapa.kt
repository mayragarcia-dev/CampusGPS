package com.garcia.campusgps.ui

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.garcia.campusgps.model.TipoError
import com.garcia.campusgps.model.UbicacionUi
import com.garcia.campusgps.util.formatearDistancia
import com.garcia.campusgps.viewmodel.MapaViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaMapa(viewModel: MapaViewModel) {
    val context = LocalContext.current
    val alturaCompacta = LocalConfiguration.current.screenHeightDp < 620
    val estado by viewModel.estado.collectAsStateWithLifecycle()
    val lugares by viewModel.lugaresVisibles.collectAsStateWithLifecycle()
    val categoriaSeleccionada by viewModel.categoriaSeleccionada.collectAsStateWithLifecycle()
    val radioMetros by viewModel.radioMetros.collectAsStateWithLifecycle()
    val lugaresDentroDelRadio by viewModel.lugaresDentroDelRadio.collectAsStateWithLifecycle()
    val seleccionado by viewModel.seleccionado.collectAsStateWithLifecycle()
    val masCercano by viewModel.masCercano.collectAsStateWithLifecycle()
    val masCercanoVisible = masCercano?.let { cercano ->
        lugares.firstOrNull { it.lugar.id == cercano.id }
    }
    var solicitudReencuadre by remember { mutableIntStateOf(0) }
    val error = estado as? UbicacionUi.Error

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

    fun solicitarUbicacion() {
        val permisos = arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
        if (permisos.any { ContextCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED }) {
            viewModel.buscar()
        } else {
            permissionLauncher.launch(permisos)
        }
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
        val desplazamientoVertical = if (alturaCompacta) {
            Modifier.verticalScroll(rememberScrollState())
        } else Modifier
        Column(
            modifier = Modifier.fillMaxSize().padding(padding).then(desplazamientoVertical),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            if (error == null) {
                MapaUbicacion(
                    estado = estado,
                    lugares = lugares,
                    seleccionado = seleccionado,
                    masCercanoId = masCercanoVisible?.lugar?.id,
                    radioMetros = radioMetros,
                    solicitudReencuadre = solicitudReencuadre,
                    modifier = if (alturaCompacta) {
                        Modifier.fillMaxWidth().height(180.dp)
                    } else Modifier.weight(1f)
                )
            }

            Button(
                modifier = Modifier.padding(top = 12.dp),
                onClick = ::solicitarUbicacion,
                enabled = estado !is UbicacionUi.Buscando && error?.tipo != TipoError.SIN_PLAY_SERVICES
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
                is UbicacionUi.Error -> MensajeError(current, onReintentar = ::solicitarUbicacion)
            }
            if (error == null) {
                OutlinedButton(
                    enabled = masCercanoVisible != null,
                    onClick = { masCercanoVisible?.let { seleccionarLugar(it.lugar.id) } }
                ) {
                    Text("Ir al más cercano")
                }
                masCercanoVisible?.let { (lugar, metros) ->
                    if (metros != null) {
                        Text(
                            text = "Más cercano: ${lugar.nombre} (${formatearDistancia(metros)})",
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
                Row(
                    modifier = Modifier.fillMaxWidth()
                        .horizontalScroll(rememberScrollState())
                        .selectableGroup()
                        .padding(horizontal = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    viewModel.categorias.forEach { categoria ->
                        FilterChip(
                            selected = categoria == categoriaSeleccionada,
                            onClick = { viewModel.alternarCategoria(categoria) },
                            label = { Text(categoria) }
                        )
                    }
                }
                Row(
                    modifier = Modifier.fillMaxWidth()
                        .horizontalScroll(rememberScrollState())
                        .padding(horizontal = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Radio de cercanía", style = MaterialTheme.typography.labelMedium)
                    Row(
                        modifier = Modifier.selectableGroup(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        viewModel.radiosDisponibles.forEach { metros ->
                            FilterChip(
                                selected = metros == radioMetros,
                                onClick = { viewModel.seleccionarRadio(metros) },
                                label = { Text("$metros m") }
                            )
                        }
                    }
                }
                ListaLugares(
                    lugares = lugares,
                    lugaresDentroDelRadio = lugaresDentroDelRadio,
                    seleccionado = seleccionado,
                    onClick = ::seleccionarLugar,
                    modifier = Modifier.fillMaxWidth().height(220.dp)
                )
                Spacer(Modifier.height(12.dp))
            }
        }
    }
}

