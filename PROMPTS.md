# Prompts y revisión técnica

Las mejoras de esta rama se trabajaron con Codex. El registro atribuye cada propuesta a la herramienta usada durante esta sesión.

| Prompt que escribí | Qué respondió la IA | Qué cambié o descarté | Por qué |
| --- | --- | --- | --- |
| «Genera el código para implementar un filtro por categorías usando Chips (Estudio, Comida, Eventos, Servicios) en la app CampusGPS. Al seleccionar un chip, se deben mostrar solo esos lugares correspondientes tanto en el mapa como en la lista. Realiza los cambios modificando únicamente las capas ViewModel y View, asegurándote de no romper la arquitectura MVVM + Repository» | Codex propuso guardar la categoría seleccionada con `StateFlow` y publicar una lista filtrada que alimenta el mapa y las tarjetas. | Se agregó la opción de tocar de nuevo el chip activo para ver todos los lugares. Se descartó `FlowRow` al detectar una incompatibilidad en tiempo de ejecución y se usó una fila desplazable. | Un solo estado mantiene el mapa y la lista sincronizados. El botón **Ir al más cercano** debe apuntar a un lugar visible. |
| «realiza lo que falta por fas», con el radio de cercanía elegido de la guía | Codex propuso un selector de 100, 200 y 500 m y un círculo centrado en la ubicación del usuario. | El ViewModel reutiliza las distancias ya calculadas y publica los IDs dentro del radio. La lista conserva su orden y añade una etiqueta a los lugares incluidos. | Cambiar el radio solo compara distancias existentes. La vista dibuja el círculo y muestra el estado que recibe. |
| «realiza lo que falta por fas», con el punto azul nativo elegido de la guía | Codex propuso activar la capa de ubicación y el botón nativo de Maps cuando Android confirma el permiso. | La vista vuelve a comprobar el permiso al regresar de Ajustes y conserva el marcador rojo de la ubicación consultada. | Android puede cambiar el permiso fuera de la app. La comprobación corresponde a la vista y el ViewModel sigue dependiendo de la interfaz del repositorio. |

## Verificación en el emulador

- **Filtro por categoría.** Al alternar entre **Servicios** y **Comida**, el mapa y la lista muestran los lugares de la categoría seleccionada.
- **Radio.** Desde el punto base, Estacionamiento Norte está a 121 m. A 100 m queda fuera del círculo y a 200 o 500 m aparece con la etiqueta **Dentro del radio**.
- **Punto azul.** El botón nativo de Maps aparece al conceder el permiso y el punto azul coincide con la ubicación simulada.
