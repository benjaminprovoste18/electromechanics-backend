-- Consultas útiles para revisar los datos en SQL Developer (conectados como EMTERRENO).

SELECT table_name FROM user_tables ORDER BY table_name;

SELECT id_sucursal, nombre_cliente, direccion, latitud, longitud
FROM sucursal
ORDER BY nombre_cliente;

SELECT o.id_servicio,
       o.id_tecnico,
       s.nombre_cliente,
       o.estado,
       o.prioridad,
       TO_CHAR(DATE '1970-01-01' + o.fecha_programada / 86400000, 'DD/MM/YYYY') AS fecha_programada,
       o.aprobada
FROM orden_trabajo o
LEFT JOIN sucursal s ON s.id_sucursal = o.id_sucursal
ORDER BY o.fecha_programada, o.prioridad DESC;

SELECT o.id_servicio, o.descripcion, COUNT(a.id_actividad) AS actividades
FROM orden_trabajo o
LEFT JOIN registro_actividad a ON a.id_servicio = o.id_servicio
GROUP BY o.id_servicio, o.descripcion
ORDER BY actividades DESC;

SELECT estado, COUNT(*) AS cantidad
FROM orden_trabajo
GROUP BY estado;

SELECT j.id_empleado,
       j.tipo_marcacion,
       TO_CHAR(CAST(TO_TIMESTAMP_TZ('1970-01-01 00:00:00 +00:00', 'YYYY-MM-DD HH24:MI:SS TZH:TZM')
               + NUMTODSINTERVAL(j.fecha_hora / 1000, 'SECOND') AS TIMESTAMP WITH TIME ZONE)
               AT TIME ZONE 'America/Santiago', 'DD/MM/YYYY HH24:MI') AS fecha_hora_chile,
       s.nombre_cliente AS sucursal_cercana,
       ROUND(j.distancia_metros) AS distancia_m,
       j.dentro_de_radio,
       j.ubicacion_simulada,
       j.aprobado
FROM registro_jornada j
LEFT JOIN sucursal s ON s.id_sucursal = j.id_sucursal_cercana
ORDER BY j.fecha_hora DESC;

SELECT id_empleado,
       COUNT(*) AS marcaciones,
       SUM(CASE WHEN dentro_de_radio = 0 THEN 1 ELSE 0 END) AS fuera_de_radio
FROM registro_jornada
GROUP BY id_empleado
ORDER BY marcaciones DESC;
