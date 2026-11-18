# Prompts y revisión técnica

Las mejoras de esta rama se trabajaron con Codex. Los prompts de abajo amplían las solicitudes de la conversación con el comportamiento esperado y las pruebas del emulador.

## Prompts técnicos

### 1. Filtro por categoría

```text
Agrega a CampusGPS cuatro chips de Material 3: Estudio, Comida, Eventos y Servicios.
Al seleccionar uno, muestra solo los lugares de esa categoría en el mapa y en la lista.
Si vuelvo a tocar el chip activo, muestra los cinco lugares. Mantén el marcador de mi
ubicación y el orden por distancia. Si una tarjeta seleccionada queda oculta, limpia su
selección. El botón «Ir al más cercano» debe usar los lugares visibles.

Modifica solo ViewModel y View. Guarda la categoría y deriva una única lista visible en
el ViewModel. La vista debe usar ese mismo estado para los marcadores y las tarjetas.

Prueba que Estudio muestre Biblioteca Central y Laboratorio de Cómputo, que Comida
muestre solo Cafetería Campus y que al desactivar el filtro vuelvan los cinco lugares.
```

### 2. Radio de cercanía

```text
Agrega un selector de radio de 100, 200 y 500 metros a CampusGPS. Usa 100 m como valor
inicial. En cuanto la app obtenga la ubicación, dibuja un círculo centrado en ella y
añade la etiqueta «Dentro del radio» a cada lugar cuya distancia sea menor o igual al
radio elegido. Conserva el orden por distancia, el filtro por categoría y las tarjetas
de los lugares que queden fuera.

Modifica solo ViewModel y View. El ViewModel debe reutilizar las distancias calculadas
y publicar qué lugares están dentro. La vista debe dibujar el círculo y mostrar las
etiquetas a partir de ese estado. Antes de obtener la ubicación, omite ambos elementos.

En el emulador, fija la ubicación en -12.04387, -76.95281. Estacionamiento Norte queda
a 121 m. Comprueba que la etiqueta esté ausente a 100 m y aparezca al elegir 200 o
500 m. Comprueba también que cambie el tamaño del círculo.
```

### 3. Punto azul nativo

```text
Agrega el punto azul y el botón de ubicación nativo de Google Maps a CampusGPS. Activa
ambos cuando Android conceda el permiso de ubicación precisa o aproximada. Vuelve a
comprobar el permiso al regresar de Ajustes y desactiva la capa si el usuario lo revoca.
Conserva el marcador rojo «Estás aquí», el radio de cercanía y el flujo de errores.

Modifica solo la capa View. Maneja el permiso en PantallaMapa y pasa su estado a
MapaUbicacion para configurar isMyLocationEnabled y myLocationButtonEnabled.

Abre la app sin permiso y comprueba que el botón nativo esté oculto. Concede el permiso
y verifica que aparezcan el botón y el punto azul en la ubicación simulada.
```

## Decisiones de implementación

| Prompt documentado | Qué respondió la IA | Qué cambié o descarté | Por qué |
| --- | --- | --- | --- |
| [Filtro por categoría](#1-filtro-por-categoría) | Codex propuso guardar la categoría con `StateFlow` y publicar una lista filtrada que alimenta el mapa y las tarjetas. | Se agregó la opción de volver a tocar el chip activo para ver todos los lugares. Se descartó `FlowRow` por una incompatibilidad en tiempo de ejecución y se usó una fila desplazable. | Un solo estado mantiene sincronizados el mapa, la lista y el botón **Ir al más cercano**. |
| [Radio de cercanía](#2-radio-de-cercanía) | Codex propuso un selector de tres radios y un círculo centrado en la ubicación del usuario. | El ViewModel publica los IDs que están dentro del radio. La lista mantiene el orden y añade una etiqueta a las tarjetas que correspondan. | Al cambiar el radio basta con comparar las distancias existentes. La vista dibuja el círculo y muestra el estado que recibe. |
| [Punto azul nativo](#3-punto-azul-nativo) | Codex propuso activar la capa de ubicación y el botón de Maps con el permiso concedido. | La vista vuelve a comprobar el permiso al regresar de Ajustes y conserva el marcador rojo de la ubicación consultada. | Android puede cambiar el permiso fuera de la app. La comprobación corresponde a la vista y el ViewModel sigue dependiendo de la interfaz del repositorio. |

## Verificación en el emulador

- **Filtro por categoría.** Al alternar entre **Servicios** y **Comida**, el mapa y la lista muestran los lugares de la categoría seleccionada.
- **Radio.** Desde el punto base, Estacionamiento Norte está a 121 m. A 100 m queda fuera del círculo y a 200 o 500 m aparece con la etiqueta **Dentro del radio**.
- **Punto azul.** El botón nativo de Maps aparece al conceder el permiso y el punto azul coincide con la ubicación simulada.
