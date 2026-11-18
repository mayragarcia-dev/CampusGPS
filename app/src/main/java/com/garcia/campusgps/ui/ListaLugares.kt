package com.garcia.campusgps.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.garcia.campusgps.model.LugarConDistancia
import com.garcia.campusgps.util.formatearDistancia

@Composable
fun ListaLugares(
    lugares: List<LugarConDistancia>,
    lugaresDentroDelRadio: Set<Int>,
    seleccionado: Int?,
    onClick: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val colores = MaterialTheme.colorScheme
    LazyColumn(
        modifier = modifier.selectableGroup(),
        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 2.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        items(lugares, key = { it.lugar.id }) { (lugar, metros) ->
            val estaSeleccionado = lugar.id == seleccionado
            Card(
                modifier = Modifier.fillMaxWidth().selectable(
                    selected = estaSeleccionado,
                    role = Role.Button,
                    onClick = { onClick(lugar.id) }
                ),
                colors = CardDefaults.cardColors(
                    containerColor = if (estaSeleccionado) colores.secondaryContainer else colores.surfaceVariant
                ),
                border = if (estaSeleccionado) BorderStroke(1.dp, colores.primary) else null
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 10.dp, vertical = 2.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(Modifier.size(7.dp).background(Color(0xFF267AC4), CircleShape))
                    Column(Modifier.weight(1f)) {
                        Text(lugar.nombre, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
                        Text(lugar.categoria, style = MaterialTheme.typography.bodySmall, color = colores.onSurfaceVariant)
                        if (lugar.id in lugaresDentroDelRadio) {
                            Text(
                                "Dentro del radio",
                                style = MaterialTheme.typography.labelSmall,
                                color = colores.primary
                            )
                        }
                    }
                    Text(
                        text = metros?.let(::formatearDistancia) ?: "Sin calcular",
                        style = MaterialTheme.typography.bodySmall,
                        color = if (metros == null) colores.onSurfaceVariant else colores.primary
                    )
                }
            }
        }
    }
}

