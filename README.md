## Sistema de Gestión de Stock y Ventas

Aplicación de escritorio desarrollada en Java para la administración de productos y ventas en pequeños comercios.

Tecnologías
- Java
- JavaFX
- SQLite
- Maven

Funcionalidades
- Gestión de productos y categorías
- Sistema de ventas con carrito
- Historial de ventas
- Control de stock
- Exportación a Excel y PDF
- Ticket de venta
- Backup automático

## Descripción

Este proyecto fue desarrollado como parte de mi aprendizaje en programación y desarrollo de software. El objetivo fue construir un sistema completo de gestión para pequeños comercios.

## Arquitectura del proyecto

El proyecto está organizado en capas para separar responsabilidades y facilitar el mantenimiento del sistema:

- **model** → contiene las entidades principales del sistema, como productos, categorías y ventas.
- **dao** → contiene la lógica de acceso a datos y la comunicación con la base SQLite.
- **app** → contiene la interfaz gráfica y la lógica principal de la aplicación.

Esta estructura permite mantener el código más ordenado, reutilizable y escalable.

## Cómo ejecutar el proyecto

1. Clonar el repositorio
2. Abrir el proyecto en NetBeans o IntelliJ
3. Ejecutar la aplicación

La base de datos SQLite se inicializa automáticamente usando el archivo `sqlite_init.sql`.

## Capturas del sistema

### Pantalla principal
![Pantalla principal](images/principal.png)

### Gestión de productos
![Productos](images/productos.png)

### Gestión de categorias
![Historial](images/categorias.png)

### Registro de ventas
![Ventas](images/ventas.png)

### Historial de ventas
![Historial](images/historial.png)

## Autor

Felipe Saavedra
