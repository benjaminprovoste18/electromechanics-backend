-- Modelo de referencia. Las tablas las crea Hibernate al levantar cada microservicio (ddl-auto: update).
-- Este script sirve para revisar el modelo o crearlo a mano en SQL Developer conectados como EMTERRENO.

CREATE TABLE sucursal (
    id_sucursal     VARCHAR2(40)  PRIMARY KEY,
    nombre_cliente  VARCHAR2(80)  NOT NULL,
    direccion       VARCHAR2(120) NOT NULL,
    latitud         FLOAT         NOT NULL,
    longitud        FLOAT         NOT NULL,
    actualizado_en  NUMBER(19)    NOT NULL
);

CREATE TABLE orden_trabajo (
    id_servicio             VARCHAR2(40)   PRIMARY KEY,
    id_tecnico              VARCHAR2(40)   NOT NULL,
    id_sucursal             VARCHAR2(40)   NOT NULL,
    fecha_programada        NUMBER(19)     NOT NULL,
    estado                  VARCHAR2(20)   NOT NULL CHECK (estado IN ('PENDIENTE', 'EN_PROCESO', 'FINALIZADO')),
    descripcion             VARCHAR2(500)  NOT NULL,
    actividades_requeridas  VARCHAR2(1000) NOT NULL,
    prioridad               VARCHAR2(20)   NOT NULL CHECK (prioridad IN ('NORMAL', 'URGENTE')),
    foto_inicial            VARCHAR2(300),
    foto_final              VARCHAR2(300),
    hora_inicio             NUMBER(19),
    hora_termino            NUMBER(19),
    aprobada                NUMBER(1)      DEFAULT 0 NOT NULL,
    creada_en               NUMBER(19)     NOT NULL,
    actualizado_en          NUMBER(19)     NOT NULL
);

CREATE TABLE registro_actividad (
    id_actividad    VARCHAR2(40)  PRIMARY KEY,
    id_servicio     VARCHAR2(40)  NOT NULL,
    detalle         VARCHAR2(500) NOT NULL,
    repuestos       VARCHAR2(300),
    estado_equipo   VARCHAR2(30)  NOT NULL,
    hora_registro   NUMBER(19)    NOT NULL,
    comentarios     VARCHAR2(500),
    actualizado_en  NUMBER(19)    NOT NULL,
    CONSTRAINT fk_actividad_orden FOREIGN KEY (id_servicio) REFERENCES orden_trabajo (id_servicio)
);

CREATE TABLE registro_jornada (
    id_registro          VARCHAR2(40) PRIMARY KEY,
    id_empleado          VARCHAR2(40) NOT NULL,
    tipo_marcacion       VARCHAR2(10) NOT NULL CHECK (tipo_marcacion IN ('ENTRADA', 'SALIDA')),
    fecha_hora           NUMBER(19)   NOT NULL,
    latitud              FLOAT        NOT NULL,
    longitud             FLOAT        NOT NULL,
    precision_metros     FLOAT        NOT NULL,
    foto_respaldo        VARCHAR2(300),
    id_sucursal_cercana  VARCHAR2(40),
    distancia_metros     FLOAT,
    dentro_de_radio      NUMBER(1)    DEFAULT 0 NOT NULL,
    ubicacion_simulada   NUMBER(1)    DEFAULT 0 NOT NULL,
    aprobado             NUMBER(1)    DEFAULT 0 NOT NULL,
    actualizado_en       NUMBER(19)   NOT NULL
);

CREATE INDEX idx_orden_tecnico ON orden_trabajo (id_tecnico);
CREATE INDEX idx_actividad_servicio ON registro_actividad (id_servicio);
CREATE INDEX idx_jornada_empleado ON registro_jornada (id_empleado);
