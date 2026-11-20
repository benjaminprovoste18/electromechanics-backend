# Commits del repositorio backend

Repositorio nuevo y **público**: `electromechanics-backend`. Son 13 commits: 7 de Benjamin y 6 de Alonso. Así queda evidencia de que los dos construyeron los microservicios.

## Preparación

1. Benjamin crea el repositorio en GitHub **sin** README ni .gitignore, y agrega a Alonso y al profesor en `Settings > Collaborators`.
2. Cada uno lo clona y, en Git Bash, define las rutas (cambiarlas por las suyas):

```bash
ORIGEN=~/Desktop/backend-EP3
REPO=~/Desktop/electromechanics-backend
```

- **ORIGEN**: el zip del backend descomprimido. No se edita.
- **REPO**: la carpeta clonada.

Cada uno configura su nombre y el correo de su cuenta de GitHub una vez:
```bash
git config --global user.name "Nombre Apellido"
git config --global user.email "correo-de-su-github@ejemplo.cl"
```

| Orden | Quién | Rama | Commits |
|---|---|---|---|
| 1 | Benjamin | main | 1 |
| 2 | Benjamin | feature/ms-sucursales | 2 a 4 |
| 3 | Alonso | feature/ms-ordenes | 5 a 8 |
| 4 | Benjamin | feature/ms-jornadas | 9 a 11 |
| 5 | Alonso | feature/oracle-docs | 12 y 13 |

Al terminar cada rama: `git push -u origin <rama>`, Pull Request en GitHub y que **el otro** lo apruebe. Antes de una rama nueva: `git checkout main && git pull`.

---

## Benjamin · main

**Commit 1**
```bash
cd "$ORIGEN" && cp --parents .gitignore .env.example docker-compose.yml "$REPO" && cd "$REPO"
git add -A && git commit -m "chore: estructura del backend con docker compose, Oracle Free y variables de entorno"
git branch -M main && git push -u origin main
```

## Benjamin · feature/ms-sucursales

```bash
cd "$REPO" && git checkout -b feature/ms-sucursales
```

**Commit 2**
```bash
cd "$ORIGEN" && cp --parents ms-sucursales/pom.xml ms-sucursales/Dockerfile ms-sucursales/src/main/resources/application.yaml ms-sucursales/src/main/java/cl/duoc/emterreno/sucursales/SucursalesApplication.java "$REPO" && cd "$REPO"
git add -A && git commit -m "chore: crea microservicio ms-sucursales con Spring Boot 3.5, JPA y driver Oracle"
```

**Commit 3**
```bash
cd "$ORIGEN" && cp --parents $(find ms-sucursales/src/main/java -name '*.java' ! -name SucursalesApplication.java) "$REPO" && cd "$REPO"
git add -A && git commit -m "feat: CRUD de sucursales con validaciones, manejo de errores y datos de prueba"
```

**Commit 4**
```bash
cd "$ORIGEN" && cp --parents $(find ms-sucursales/src/test -name '*.java') "$REPO" && cd "$REPO"
git add -A && git commit -m "test: pruebas JUnit y Mockito del servicio y controlador de sucursales"
git push -u origin feature/ms-sucursales
```

## Alonso · feature/ms-ordenes

```bash
cd "$REPO" && git checkout main && git pull && git checkout -b feature/ms-ordenes
B=ms-ordenes/src/main/java/cl/duoc/emterreno/ordenes
```

**Commit 5**
```bash
cd "$ORIGEN" && cp --parents ms-ordenes/pom.xml ms-ordenes/Dockerfile ms-ordenes/src/main/resources/application.yaml $B/OrdenesApplication.java "$REPO" && cd "$REPO"
git add -A && git commit -m "chore: crea microservicio ms-ordenes"
```

**Commit 6**
```bash
cd "$ORIGEN" && cp --parents $(find $B/model $B/repository $B/dto $B/exception -name '*.java') "$REPO" && cd "$REPO"
git add -A && git commit -m "feat: entidades de orden de trabajo y actividad con relación ManyToOne y DTOs validados"
```

**Commit 7**
```bash
cd "$ORIGEN" && cp --parents $(find $B/service $B/controller $B/config -name '*.java') "$REPO" && cd "$REPO"
git add -A && git commit -m "feat: endpoints de órdenes y bitácora con reglas de cambio de estado y aprobación"
```

**Commit 8**
```bash
cd "$ORIGEN" && cp --parents $(find ms-ordenes/src/test -name '*.java') "$REPO" && cd "$REPO"
git add -A && git commit -m "test: pruebas de reglas, servicio y controlador de órdenes"
git push -u origin feature/ms-ordenes
```

## Benjamin · feature/ms-jornadas

```bash
cd "$REPO" && git checkout main && git pull && git checkout -b feature/ms-jornadas
B=ms-jornadas/src/main/java/cl/duoc/emterreno/jornadas
```

**Commit 9**
```bash
cd "$ORIGEN" && cp --parents ms-jornadas/pom.xml ms-jornadas/Dockerfile ms-jornadas/src/main/resources/application.yaml $B/JornadasApplication.java $(find $B/model $B/repository $B/dto $B/exception -name '*.java') "$REPO" && cd "$REPO"
git add -A && git commit -m "feat: crea microservicio ms-jornadas con entidad de registro de jornada"
```

**Commit 10**
```bash
cd "$ORIGEN" && cp --parents $(find $B/client $B/service $B/controller -name '*.java') "$REPO" && cd "$REPO"
git add -A && git commit -m "feat: marcaciones con radio de 300 m calculado consultando a ms-sucursales con RestClient"
```

**Commit 11**
```bash
cd "$ORIGEN" && cp --parents $(find ms-jornadas/src/test -name '*.java') "$REPO" && cd "$REPO"
git add -A && git commit -m "test: pruebas de Haversine, horas trabajadas, servicio y controlador de jornadas"
git push -u origin feature/ms-jornadas
```

## Alonso · feature/oracle-docs

```bash
cd "$REPO" && git checkout main && git pull && git checkout -b feature/oracle-docs
```

**Commit 12**
```bash
cd "$ORIGEN" && cp --parents database/01_crear_usuario_xe.sql database/02_modelo_referencia.sql database/03_consultas.sql postman/EM-Terreno.postman_collection.json "$REPO" && cd "$REPO"
git add -A && git commit -m "docs: scripts de Oracle (usuario, modelo y consultas) y colección de Postman"
```

**Commit 13**
```bash
cd "$ORIGEN" && cp --parents README.md docs/COMMITS_BACKEND.md "$REPO" && cd "$REPO"
git add -A && git commit -m "docs: README con microservicios, ejecución con Docker, pruebas y despliegue en AWS"
git push -u origin feature/oracle-docs
```

## Después de los commits

Levantar todo (`docker compose up -d --build`), probar con Postman y con la app. Cada corrección que hagan va como commit `fix:` de quien la encontró.
