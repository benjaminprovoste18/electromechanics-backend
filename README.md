# EM Terreno: Backend de microservicios

Backend del caso **ELECTROMECHANICS INGENIERÍA SPA** (DSY1105, Experiencia 3). Son tres microservicios **Spring Boot 3.5.15** con **Java 21**, que guardan sus datos en **Oracle Database** y responden a la app Android EM Terreno, que se conecta con Retrofit.

> Todos los datos son ficticios (Ley N.º 19.628 y Ley N.º 21.719).

## Integrantes

| Nombre | Aporte en el backend |
|---|---|
| Benjamin Provoste | ms-sucursales, ms-jornadas (incluye la llamada a ms-sucursales) y sus pruebas |
| Alonso Pino | ms-ordenes, scripts de Oracle, colección de Postman y sus pruebas |

## Microservicios

| Microservicio | Puerto | Tablas en Oracle | Endpoints principales |
|---|---|---|---|
| ms-sucursales | 8081 | `SUCURSAL` | `GET/POST /api/sucursales` · `GET/PUT/DELETE /api/sucursales/{id}` |
| ms-ordenes | 8082 | `ORDEN_TRABAJO`, `REGISTRO_ACTIVIDAD` | `GET/POST /api/ordenes?tecnicoId=&estado=` · `GET/PUT/DELETE /api/ordenes/{id}` · `GET /api/ordenes/{id}/actividades` · `GET/POST /api/actividades` · `PUT /api/actividades/{id}` |
| ms-jornadas | 8083 | `REGISTRO_JORNADA` | `GET/POST /api/jornadas?empleadoId=` · `GET/PUT /api/jornadas/{id}` · `GET /api/jornadas/resumen?fecha=2026-11-20` |

Cada microservicio tiene las mismas capas vistas en clases:

```
ms-xxx/src/main/java/cl/duoc/emterreno/xxx/
├── model/        # Entidades JPA (@Entity) y enums
├── repository/   # Interfaces JpaRepository
├── dto/          # Records que viajan como JSON, con validaciones (@NotBlank, @Size...)
├── service/      # Lógica y reglas de negocio
├── controller/   # @RestController con los endpoints
├── exception/    # Manejo de errores: 400, 404 y 409 con mensaje en español
└── config/       # Datos de prueba al iniciar (DataLoader)
```

### Reglas de negocio
- **ms-sucursales**: no se permiten dos sucursales con el mismo nombre; coordenadas dentro de Chile.
- **ms-ordenes**: una orden no puede volver a un estado anterior; EN_PROCESO exige hora de inicio; FINALIZADO exige hora de término posterior al inicio; solo se aprueban órdenes finalizadas y la aprobación no se pierde; las actividades solo se registran en órdenes iniciadas; solo se eliminan órdenes pendientes.
- **ms-jornadas**: el servidor recalcula la sucursal más cercana con la fórmula de Haversine, preguntándole a ms-sucursales (radio válido de 300 m); la aprobación del supervisor no se pierde; resumen de horas trabajadas por día.

## Cómo ejecutar

### Opción A: todo con Docker (recomendada)

Requiere Docker Desktop.

1. Copiar `.env.example` como `.env` y cambiar las contraseñas. El archivo `.env` **no se sube** a GitHub.
2. En la carpeta del proyecto:
   ```
   docker compose up -d --build
   ```
3. La primera vez Oracle tarda entre 3 y 6 minutos en crear la base. Revisar con `docker compose ps` que `oracle_emterreno` diga `healthy`.
4. Probar en el navegador: `http://localhost:8081/api/sucursales`, `http://localhost:8082/api/ordenes` y `http://localhost:8083/api/jornadas`.

Para detener: `docker compose down`. Para borrar también los datos de Oracle: `docker compose down -v`.

### Opción B: Oracle en Docker y microservicios desde VS Code

1. Crear el `.env` igual que en la opción A y levantar solo la base: `docker compose up -d oracle-db`.
2. Abrir la carpeta del backend en VS Code (con Extension Pack for Java y Spring Boot Extension Pack).
3. Ejecutar `SucursalesApplication`, `OrdenesApplication` y `JornadasApplication` con **Run** sobre el método `main`. Cada uno lee el `.env` de la carpeta del backend.

Para usar `mvnw` como en clases, copiar `mvnw`, `mvnw.cmd` y la carpeta `.mvn` del proyecto de clases dentro de cada microservicio. Luego: `.\mvnw.cmd spring-boot:run`.

### Opción C: Oracle XE instalado en el PC

1. En SQL Developer, conectado como `SYSTEM`, ejecutar `database/01_crear_usuario_xe.sql` (pide la contraseña del usuario `emterreno`).
2. En el `.env` agregar: `DB_URL=jdbc:oracle:thin:@localhost:1521/XEPDB1` y poner esa contraseña en `DB_PASSWORD`.
3. Ejecutar los microservicios como en la opción B.

## Ver la base de datos

Conexión en SQL Developer: usuario `emterreno`, contraseña la del `.env`, host `localhost`, puerto `1521`, nombre del servicio `FREEPDB1` (o `XEPDB1` si usan XE). Las tablas las crea Hibernate al iniciar cada microservicio. El modelo de referencia está en `database/02_modelo_referencia.sql` y hay consultas listas en `database/03_consultas.sql`.

## Probar con Postman

Importar `postman/EM-Terreno.postman_collection.json`. Trae una carpeta por microservicio, con ejemplos válidos y ejemplos que devuelven 400 y 409.

## Pruebas unitarias (JUnit 5 + Mockito)

| Microservicio | Pruebas |
|---|---|
| ms-sucursales | `SucursalServiceTest` (7) y `SucursalControllerTest` (4) |
| ms-ordenes | `OrdenReglasTest` (7), `OrdenServiceTest` (9) y `OrdenControllerTest` (4) |
| ms-jornadas | `JornadaReglasTest` (6), `JornadaServiceTest` (5) y `JornadaControllerTest` (4) |

No necesitan la base de datos. Se ejecutan desde la pestaña **Testing** de VS Code o con `mvn test` dentro de cada microservicio.

## Conectar la app Android

En el proyecto Android, `data/remote/ApiConfig.kt` define el servidor. Con el emulador se usa `http://10.0.2.2` (el PC visto desde el emulador). Con un celular físico, la IP del PC en la red wifi.

## Despliegue en AWS (Examen Transversal)

Siguiendo la guía de clases "Desplegando backend en AWS":

1. Crear una instancia EC2 con Amazon Linux 2023. Oracle necesita al menos 2 GB de RAM solo para la base, así que conviene **t3.medium** (4 GB) en vez de t3.small.
2. Grupo de seguridad: SSH (22) y TCP personalizado **8081-8083** desde cualquier lugar.
3. Instalar Docker, Docker Compose y git en la instancia (mismos comandos de la guía).
4. Clonar este repositorio, crear el `.env` en la instancia y ejecutar `docker compose up -d --build`.
5. Asignar una **IP elástica** y ponerla en `ApiConfig.HOST` de la app antes de generar el APK firmado.
