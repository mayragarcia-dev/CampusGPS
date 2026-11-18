package com.garcia.campusgps.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapType
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.rememberUpdatedMarkerState
import com.garcia.campusgps.model.LugarConDistancia
import com.garcia.campusgps.model.UbicacionUi

private val centroCampusTecsup = LatLng(-12.04387, -76.95281)

@Composable
fun MapaUbicacion(
    estado: UbicacionUi,
    lugares: List<LugarConDistancia>,
    seleccionado: Int?,
    modifier: Modifier = Modifier
) {
    val camara = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(centroCampusTecsup, 17f)
    }
    val posicionActual = (estado as? UbicacionUi.Lista)?.posicion
    val puntoActual = posicionActual?.let { LatLng(it.latitud, it.longitud) } ?: centroCampusTecsup
    val marcadorActual = rememberUpdatedMarkerState(position = puntoActual)
    var mapaCargado by remember { mutableStateOf(false) }

    LaunchedEffect(posicionActual, mapaCargado, seleccionado) {
        if (mapaCargado && posicionActual != null && seleccionado == null) {
            camara.animate(
                CameraUpdateFactory.newLatLngZoom(puntoActual, 16f),
                durationMs = 1_000
            )
            marcadorActual.showInfoWindow()
        }
    }

    GoogleMap(
        modifier = modifier,
        cameraPositionState = camara,
        onMapLoaded = { mapaCargado = true },
        properties = MapProperties(mapType = MapType.NORMAL),
        uiSettings = MapUiSettings(
            compassEnabled = false,
            mapToolbarEnabled = false,
            myLocationButtonEnabled = false,
            zoomControlsEnabled = false
        )
    ) {
        val iconoLugar = remember {
            BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)
        }
        lugares.forEach { (lugar) ->
            key(lugar.id) {
                val marcador = rememberUpdatedMarkerState(LatLng(lugar.latitud, lugar.longitud))
                Marker(
                    state = marcador,
                    title = lugar.nombre,
                    snippet = lugar.categoria,
                    icon = iconoLugar
                )
                LaunchedEffect(seleccionado, mapaCargado) {
                    if (mapaCargado && seleccionado == lugar.id) {
                        camara.animate(
                            CameraUpdateFactory.newLatLngZoom(marcador.position, 17f),
                            durationMs = 750
                        )
                        marcador.showInfoWindow()
                    }
                }
            }
        }

        if (posicionActual != null) {
            Marker(
                state = marcadorActual,
                title = "Estás aquí"
            )
        }
    }
}

