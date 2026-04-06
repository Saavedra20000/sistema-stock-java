PRAGMA foreign_keys = ON;

CREATE TABLE IF NOT EXISTS categorias (
    id_categoria INTEGER PRIMARY KEY AUTOINCREMENT,
    nombre TEXT NOT NULL UNIQUE,
    descripcion TEXT,
    activo INTEGER NOT NULL DEFAULT 1
);

CREATE TABLE IF NOT EXISTS usuarios (
    id_usuario INTEGER PRIMARY KEY AUTOINCREMENT,
    nombre_usuario TEXT NOT NULL UNIQUE,
    clave TEXT NOT NULL,
    rol TEXT NOT NULL,
    activo INTEGER NOT NULL DEFAULT 1
);

CREATE TABLE IF NOT EXISTS productos (
    id_producto INTEGER PRIMARY KEY AUTOINCREMENT,
    codigo TEXT NOT NULL UNIQUE,
    nombre TEXT NOT NULL,
    descripcion TEXT,
    precio_compra REAL NOT NULL,
    precio_venta REAL NOT NULL,
    stock INTEGER NOT NULL,
    stock_minimo INTEGER NOT NULL,
    id_categoria INTEGER NOT NULL,
    activo INTEGER NOT NULL DEFAULT 1,
    codigo_barras TEXT,
    FOREIGN KEY (id_categoria) REFERENCES categorias(id_categoria)
);

CREATE TABLE IF NOT EXISTS ventas (
    id_venta INTEGER PRIMARY KEY AUTOINCREMENT,
    fecha TEXT NOT NULL,
    subtotal_sin_iva REAL NOT NULL,
    iva REAL NOT NULL,
    total_con_iva REAL NOT NULL,
    metodo_pago TEXT,
    cuotas INTEGER,
    descuento REAL NOT NULL DEFAULT 0,
    recargo REAL NOT NULL DEFAULT 0
);

 CREATE TABLE IF NOT EXISTS detalle_venta (
    id_detalle INTEGER PRIMARY KEY AUTOINCREMENT,
    id_venta INTEGER NOT NULL,
    id_producto INTEGER NOT NULL,
    cantidad INTEGER NOT NULL,
    precio_unitario_sin_iva REAL NOT NULL,
    subtotal_linea REAL NOT NULL,
    FOREIGN KEY (id_venta) REFERENCES ventas(id_venta),
    FOREIGN KEY (id_producto) REFERENCES productos(id_producto)
);

CREATE TABLE IF NOT EXISTS configuracion_negocio(
id INTEGER PRIMARY KEY AUTOINCREMENT,

nombre_empresa TEXT,
direccion TEXT,
telefono TEXT,
cuit TEXT,
mensaje_ticket TEXT
);