# Prompts y revisión técnica

Las mejoras de esta rama se trabajaron con Gemini. Los prompts de abajo amplían las solicitudes de la conversación con el comportamiento esperado y las pruebas del emulador.

## Prompts técnicos solicitados

### 1. Filtro por categorías
Revisa el filtro por categorías de CampusGPS. Los chips Estudio, Comida,
Eventos y Servicios deben filtrar tanto los marcadores del mapa como las
tarjetas de la lista. Al tocar otra vez el chip activo, deben reaparecer
los cinco lugares. Conserva el marcador del usuario y el orden por
distancia. «Ir al más cercano» debe funcionar con los lugares visibles.

Revisa solo ViewModel y View. Comprueba que el ViewModel publique una
misma lista filtrada para el mapa y las tarjetas. Explica qué funciona,
qué corregirías y cómo probarlo en el emulador con Estudio y Comida.

### 2. Radio de cercanía
Revisa el selector de 100, 200 y 500 metros de CampusGPS. El valor inicial
debe ser 100 m. Cuando haya una ubicación disponible, el mapa debe
dibujar un círculo centrado en ella y la lista debe mostrar la etiqueta
«Dentro del radio» para los lugares cuya distancia sea menor o igual al
radio seleccionado. Los demás lugares deben seguir en la lista.

Revisa solo ViewModel y View. Comprueba que el ViewModel reutilice las
distancias calculadas y que la vista dibuje el círculo y las etiquetas
a partir del estado recibido.

Propón una prueba con la ubicación -12.04387, -76.95281. Estacionamiento
Norte está a 121 m, así que la etiqueta debe aparecer a 200 y 500 m.
Indica qué corregirías y por qué.

### 3. Punto azul nativo de Google Maps
Revisa cómo CampusGPS muestra el punto azul y el botón nativo de
ubicación de Google Maps. Comprueba que se activen con permiso de
ubicación precisa o aproximada y que la app vuelva a comprobar el
permiso al regresar de Ajustes. Conserva el marcador rojo «Estás aquí»
y el manejo de errores existente.

Revisa solo la capa View. Comprueba que PantallaMapa pase el estado
del permiso a MapaUbicacion para configurar isMyLocationEnabled y
myLocationButtonEnabled. Explica cómo probar los casos de permiso
concedido, denegado y revocado. Justifica cualquier cambio de código
que propongas.

## Decisiones de implementación

| Prompt documentado | Qué respondió la IA | Qué cambié o descarté | Por qué |
| --- | --- | --- | --- |
| [Filtro por categoría](#1-filtro-por-categorías) | Gemini propuso guardar la categoría con `StateFlow` y publicar una lista filtrada que alimenta el mapa y las tarjetas. | Se agregó la opción de volver a tocar el chip activo para ver todos los lugares. Se descartó `FlowRow` por una incompatibilidad en tiempo de ejecución y se usó una fila desplazable. | Un solo estado mantiene sincronizados el mapa, la lista y el botón **Ir al más cercano**. |
| [Radio de cercanía](#2-radio-de-cercanía) | Gemini propuso un selector de tres radios y un círculo centrado en la ubicación del usuario. | El ViewModel publica los IDs que están dentro del radio. La lista mantiene el orden y añade una etiqueta a las tarjetas que correspondan. | Al cambiar el radio basta con comparar las distancias existentes. La vista dibuja el círculo y muestra el estado que recibe. |
| [Punto azul nativo](#3-punto-azul-nativo-de-google-maps) | Gemini propuso activar la capa de ubicación y el botón de Maps con el permiso concedido. | La vista vuelve a comprobar el permiso al regresar de Ajustes y conserva el marcador rojo de la ubicación consultada. | Android puede cambiar el permiso fuera de la app. La comprobación corresponde a la vista y el ViewModel sigue dependiendo de la interfaz del repositorio. |

## Verificación en el emulador

- **Filtro por categoría.** Al alternar entre **Servicios** y **Comida**, el mapa y la lista muestran los lugares de la categoría seleccionada.
- **Radio.** Desde el punto base, Estacionamiento Norte está a 121 m. A 100 m queda fuera del círculo y a 200 o 500 m aparece con la etiqueta **Dentro del radio**.
- **Punto azul.** El botón nativo de Maps aparece al conceder el permiso y el punto azul coincide con la ubicación simulada.
