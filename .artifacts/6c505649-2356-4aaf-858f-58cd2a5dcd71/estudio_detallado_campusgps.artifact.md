# Documentación Detallada de Estudio - CampusGPS

Esta guía exhaustiva recopila todos los temas teóricos y prácticos abordados durante el desarrollo y análisis de **CampusGPS**. Está diseñada como material de estudio personal para comprender a fondo cada aspecto técnico de la aplicación.

---

## 1. Arquitectura MVVM (Model - View - ViewModel)

La arquitectura MVVM separa el diseño de la interfaz de usuario de la lógica de negocio y el acceso a datos.

- **Model (Capa de Datos):**
  - Contiene los modelos de datos (`Lugar`, `Posicion`, `LugarConDistancia`, `UbicacionUi`).
  - Define interfaces y repositorios (`LugaresRepository`, `UbicacionRepository`).
  - Utiliza `FusedLocationProviderClient` para la geolocalización y repositorios locales para los puntos de interés (Campus Tecsup).
- **ViewModel (`MapaViewModel`):**
  - Mantiene el estado de la aplicación mediante `StateFlow` reactivos (`_estado`, `_lugares`, `seleccionado`, `radioMetros`).
  - Procesa la lógica de negocio: cálculo de distancias usando la fórmula de Haversine (`Distancia.kt`), filtrado por categorías y control de radios de cercanía.
- **View (Capa de Vista):**
  - Compuesta por funciones Composable de Jetpack Compose (`PantallaMapa`, `MapaUbicacion`, `ListaLugares`, `MensajeError`).
  - Observa los `StateFlow` del ViewModel mediante `collectAsStateWithLifecycle()` y reacciona instantáneamente ante cualquier cambio de estado.

---

## 2. Implementación de Jetpack Compose y Material 3

La interfaz está construida siguiendo los estándares modernos de Android:

- **Material 3 (`material3`):**
  - Se configura con `MaterialTheme(colorScheme = lightColorScheme())` en `MainActivity.kt`.
  - Utiliza componentes oficiales de M3: `Scaffold`, `TopAppBar`, `Card`, `Button`, `OutlinedButton`, `FilterChip` y `CircularProgressIndicator`.
  - Tipografía estandarizada mediante `MaterialTheme.typography`.
- **Diseño Adaptativo:**
  - Control de altura compacta (`LocalConfiguration.current.screenHeightDp < 620`) para ajustar el mapa y la lista en pantallas pequeñas o modo horizontal.

---

## 3. Integración y Uso de Google Maps API (`maps-compose`)

La aplicación integra mapas interactivos utilizando la librería declarativa `maps-compose`:

- **Credenciales y Configuración:**
  - La clave de API de Google Maps se almacena de forma segura en `local.properties` con la clave `MAPS_API_KEY`.
  - Se inyecta en el `AndroidManifest.xml` mediante `meta-data` con `com.google.android.geo.API_KEY`.
- **El Composable `GoogleMap`:**
  - **`cameraPositionState`**: Controla la posición inicial, el nivel de zoom y las animaciones fluidas con `CameraUpdateFactory.newLatLngZoom()`.
  - **`MapProperties`**: Permite activar capas nativas como el punto azul de ubicación (`isMyLocationEnabled`).
  - **`MapUiSettings`**: Controla los controles de UI (zoom, botón de mi ubicación, brújula).
- **Elementos Geográficos Hijos:**
  - **`Marker`**: Muestra los puntos de interés de Tecsup y la posición del usuario, con iconos personalizados (`BitmapDescriptorFactory`) y globos informativos (`showInfoWindow()`).
  - **`Circle`**: Dibuja el radio de cercanía (100m, 200m, 500m) alrededor del usuario con colores translúcidos derivados del tema.

---

## 4. Localización e Idioma del Mapa (Español vs. Inglés)

- **¿Por qué el mapa aparecía en inglés por defecto?**
  - El SDK nativo de Google Maps no lee el archivo `strings.xml` de la app, sino que consulta directamente la configuración regional (`Locale.getDefault()`) del sistema operativo del dispositivo o emulador.
- **Cómo solucionarlo:**
  - **Método de sistema:** Cambiar el idioma del emulador/dispositivo a Español en *Ajustes > Sistema > Idiomas*.
  - **Método por código:** Forzar el `Locale` en `MainActivity.kt` mediante `resources.configuration.setLocale(locale)`.

---

## 5. Ubicación y GPS en el Emulador

- **Comportamiento del Emulador:**
  - El emulador simula una ubicación virtual (por defecto en Mountain View, California, u otra fija) y no el GPS físico de la PC.
- **Cómo simular tu ubicación o la de Tecsup:**
  - Usando el panel **Extended Controls (`...`) -> Location** en Android Studio para introducir coordenadas de latitud y longitud personalizadas (por ejemplo, el campus de Tecsup en Lima: `-12.04387, -76.95281`).
  - Mediante comandos ADB: `adb emu geo fix <longitud> <latitud>`.

---

## 6. Gestión de Permisos de Ubicación

- **Permisos requeridos:** `ACCESS_FINE_LOCATION` y `ACCESS_COARSE_LOCATION`.
- **Flujo de permisos:**
  - Solicitud en tiempo real mediante `rememberLauncherForActivityResult(ActivityResultContracts.RequestMultiplePermissions())`.
  - Verificación del ciclo de vida con `LifecycleEventEffect(Lifecycle.Event.ON_RESUME)` para detectar si el usuario concedió o revocó el permiso desde los Ajustes del sistema.
  - Manejo de estados de error específicos (`TipoError.SIN_PERMISO`, `TipoError.GPS_APAGADO`, `TipoError.SIN_PLAY_SERVICES`).
