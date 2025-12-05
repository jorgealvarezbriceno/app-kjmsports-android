# Documentación Técnica: KJMSports App (Android)

## 1. Descripción del Proyecto

KJMSports es una aplicación de comercio electrónico para Android, desarrollada como un proyecto académico. La aplicación permite a los usuarios navegar por un catálogo de artículos deportivos, gestionar un carrito de compras y realizar un proceso de checkout simulado. Además, cuenta con un panel de administración protegido para gestionar los productos y usuarios del sistema.

La aplicación está diseñada siguiendo patrones de arquitectura modernos de Android, con una clara separación de responsabilidades entre la interfaz de usuario, la lógica de negocio y la comunicación con la API.

---

## 2. Características Principales

### 2.1. Funcionalidades para el Cliente

- **Autenticación de Usuarios:** Pantalla de Login para acceder a la aplicación.
- **Navegación Intuitiva:** Menú lateral deslizable para acceder a todas las secciones.
- **Pantalla de Inicio:** Muestra un resumen de categorías, ofertas y banners promocionales.
- **Catálogo de Productos:** 
    - Visualización de productos por categorías.
    - Lista general de todos los productos.
- **Carrito de Compras:** 
    - Añadir productos al carrito desde las diferentes listas.
    - Visualizar y modificar las cantidades de los productos en el carrito.
    - Cálculo del total de la compra.
- **Proceso de Checkout:** Flujo de pago simulado que culmina con la creación de una "boleta" en el sistema.

### 2.2. Funcionalidades para el Administrador

- **Control de Acceso:** El panel de administración solo es visible y accesible para usuarios con el rol "admin".
- **Dashboard de Administración:** Menú central para acceder a la gestión de productos y usuarios.
- **CRUD de Productos:**
    - **Crear:** Añadir nuevos productos a través de un formulario completo.
    - **Leer:** Visualizar la lista de todos los productos existentes.
    - **Actualizar:** Editar la información de cualquier producto (nombre, precio, stock, etc.).
    - **Eliminar:** Borrar productos de la base de datos con un diálogo de confirmación.
- **CRUD de Usuarios:**
    - **Crear:** Registrar nuevos usuarios (clientes o administradores).
    - **Leer:** Visualizar la lista de todos los usuarios registrados.
    - **Actualizar:** Modificar la información de cualquier usuario.
    - **Eliminar:** Borrar usuarios del sistema.

---

## 3. Arquitectura y Tecnologías Utilizadas

- **Lenguaje de Programación:** 100% **Kotlin**.
- **Interfaz de Usuario:** **Jetpack Compose**, el framework moderno de UI declarativa de Android.
- **Arquitectura:** MVVM (Model-View-ViewModel).
    - **View:** Las pantallas (`Composable functions`).
    - **ViewModel:** Clases que gestionan la lógica de la UI y los estados (`ProductViewModel`, `UserViewModel`, etc.).
    - **Model:** Las clases de datos y la capa de comunicación con la API.
- **Gestión de Estado:** **StateFlow** para una comunicación reactiva y eficiente entre los ViewModels y la UI.
- **Navegación:** **Jetpack Navigation for Compose** para gestionar el flujo entre pantallas.
- **Comunicación con API REST:**
    - **Retrofit:** Para definir las peticiones HTTP a la API de Spring Boot.
    - **Gson:** Para la serialización/deserialización de objetos JSON.
- **Carga de Imágenes:** **Coil (Coil-Compose)** para cargar y cachear imágenes desde URLs de forma asíncrona.

---

## 4. Puesta en Marcha del Proyecto

Para compilar y ejecutar la aplicación, se deben seguir los siguientes pasos:

1.  **Clonar el Repositorio:** `git clone https://github.com/jorgealvarezbriceno/app-kjmsports-android.git`
2.  **Abrir en Android Studio:** Abrir el proyecto en la última versión estable de Android Studio.
3.  **Ejecutar la API Backend:** Esta aplicación cliente requiere que la API de Spring Boot (KJM-API) se esté ejecutando localmente.
4.  **Configurar la Dirección IP:**
    - Verificar la dirección IP de la máquina donde se ejecuta la API (ej: `192.168.1.100`).
    - Actualizar la constante `BASE_URL` en el archivo `app/src/main/java/com/example/kjm_android/api/ApiService.kt` para que apunte a dicha IP.
5.  **Ejecutar la Aplicación:** Construir y ejecutar la aplicación en un emulador o en un dispositivo físico conectado a la misma red WiFi que la API.
