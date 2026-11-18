package com.garcia.campusgps.ui

import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.garcia.campusgps.model.TipoError
import com.garcia.campusgps.model.UbicacionUi

@Composable
fun MensajeError(error: UbicacionUi.Error, onReintentar: () -> Unit) {
    val context = LocalContext.current
    val colores = MaterialTheme.colorScheme

    Column(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = colores.errorContainer,
            shape = MaterialTheme.shapes.medium
        ) {
            Text(
                text = error.mensaje,
                modifier = Modifier.padding(16.dp),
                color = colores.onErrorContainer,
                textAlign = TextAlign.Center
            )
        }
        when (error.tipo) {
            TipoError.SIN_PERMISO -> OutlinedButton(onClick = {
                val ajustes = Intent(
                    Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                    Uri.fromParts("package", context.packageName, null)
                )
                context.startActivity(ajustes)
            }) { Text("Abrir ajustes") }
            TipoError.GPS_APAGADO, TipoError.OTRO -> OutlinedButton(onClick = onReintentar) {
                Text("Reintentar")
            }
            TipoError.SIN_PLAY_SERVICES -> Unit
        }
    }
}

