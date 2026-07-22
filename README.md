# Polleria POS

Sistema de punto de venta para restaurante/polleria construido con JavaFX, Spring Boot y PostgreSQL. La aplicacion integra una interfaz de escritorio con vistas FXML, servicios de negocio administrados por Spring y persistencia mediante Spring Data JPA.

## Tabla de Contenido

- [Caracteristicas](#caracteristicas)
- [Stack Tecnico](#stack-tecnico)
- [Arquitectura](#arquitectura)
- [Requisitos](#requisitos)
- [Configuracion](#configuracion)
- [Ejecucion Local](#ejecucion-local)
- [Comandos Utiles](#comandos-utiles)
- [Estructura del Proyecto](#estructura-del-proyecto)
- [Base de Datos](#base-de-datos)
- [Pruebas](#pruebas)
- [Roadmap Sugerido](#roadmap-sugerido)

## Caracteristicas

- Interfaz de escritorio desarrollada con JavaFX y FXML.
- Integracion con Spring Boot para inyeccion de dependencias y gestion de servicios.
- Persistencia con Spring Data JPA e Hibernate.
- Base de datos PostgreSQL disponible mediante Docker Compose.
- Modulos base para pedidos, productos, usuarios, mesas, caja, ventas e inventario.
- Navegacion centralizada entre escenas JavaFX.
- Estilos CSS separados por vista para mantener la interfaz desacoplada de la logica.

## Stack Tecnico

| Categoria | Tecnologia |
| --- | --- |
| Lenguaje principal | Java 21 |
| UI de escritorio | JavaFX 21 |
| Framework backend | Spring Boot 3.5 |
| Persistencia | Spring Data JPA, Hibernate |
| Base de datos | PostgreSQL 17 |
| Seguridad utilitaria | Spring Security Crypto BCrypt |
| Build tool | Maven Wrapper |
| Contenedores | Docker Compose |

## Arquitectura

El proyecto combina JavaFX como capa de presentacion y Spring Boot como contenedor de aplicacion. Los controladores FXML son beans de Spring, lo que permite inyectar servicios directamente en las vistas.

Flujo general:

1. `JavaFxApplication` inicia el contexto de Spring.
2. JavaFX carga una vista FXML desde `src/main/resources/views`.
3. `FXMLLoader` usa el `ApplicationContext` como `ControllerFactory`.
4. Los controladores delegan la logica en servicios.
5. Los servicios acceden a repositorios Spring Data JPA.
6. Hibernate sincroniza las entidades con PostgreSQL.

## Requisitos

- JDK 21
- Docker y Docker Compose
- Maven Wrapper incluido en el repositorio (`./mvnw`)
- PostgreSQL local o el servicio incluido en `docker-compose.yml`

Verificar Java:

```bash
java -version
```

Debe apuntar a Java 21.

## Configuracion

La configuracion principal esta en:

```text
src/main/resources/application.properties
```

Valores por defecto para desarrollo:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/polleria_pos
spring.datasource.username=admin
spring.datasource.password=admin123
spring.jpa.hibernate.ddl-auto=update
```

Estas credenciales estan pensadas para entorno local. Para produccion, usa variables de entorno, perfiles de Spring o un gestor de secretos.

## Ejecucion Local

1. Levantar PostgreSQL:

```bash
docker compose up -d
```

2. Compilar el proyecto:

```bash
./mvnw -DskipTests package
```

3. Ejecutar la aplicacion JavaFX:

```bash
./mvnw javafx:run
```

4. Detener la base de datos cuando ya no sea necesaria:

```bash
docker compose down
```

Para eliminar tambien el volumen de datos local:

```bash
docker compose down -v
```

## Comandos Utiles

```bash
# Compilar y empaquetar sin ejecutar tests
./mvnw -DskipTests package

# Ejecutar tests
./mvnw test

# Ejecutar la app JavaFX
./mvnw javafx:run

# Levantar PostgreSQL
docker compose up -d

# Ver logs de PostgreSQL
docker compose logs -f postgres

# Detener servicios Docker
docker compose down
```

## Estructura del Proyecto

```text
restaurantPos/
├── docker-compose.yml
├── pom.xml
├── README.md
├── src/
│   ├── main/
│   │   ├── java/com/restaurantPos/
│   │   │   ├── application/      # Bootstrap JavaFX
│   │   │   ├── config/           # Integracion Spring/JavaFX
│   │   │   ├── controllers/      # Controladores FXML
│   │   │   ├── models/           # Entidades JPA
│   │   │   ├── repositories/     # Repositorios Spring Data
│   │   │   ├── services/         # Logica de negocio
│   │   │   └── utils/            # Constantes de rutas
│   │   └── resources/
│   │       ├── application.properties
│   │       ├── styles/           # CSS por vista
│   │       └── views/            # Vistas FXML
│   └── test/
└── target/
```

## Base de Datos

El servicio PostgreSQL se define en `docker-compose.yml`:

```yaml
POSTGRES_DB: polleria_pos
POSTGRES_USER: admin
POSTGRES_PASSWORD: admin123
```

Hibernate esta configurado con `spring.jpa.hibernate.ddl-auto=update`, por lo que puede actualizar el esquema automaticamente durante el desarrollo segun las entidades JPA.

Entidades principales incluidas:

- `Usuarios`
- `Roles`
- `Clientes`
- `Mesas`
- `Pedidos`
- `DetallePedido`
- `Productos`
- `Categorias`
- `Ventas`
- `Caja`
- `MovimientoCaja`
- `MovimientoInventario`

## Pruebas

Ejecutar la suite:

```bash
./mvnw test
```

Si aparece un error similar a `Cannot load driver class: org.h2.Driver`, revisa que la dependencia de H2 este disponible en el classpath de pruebas y que la configuracion de test no este forzando un driver sin resolver.

Para validar compilacion y empaquetado sin pruebas:

```bash
./mvnw -DskipTests package
```

## Roadmap Sugerido

- Agregar perfiles separados para `dev`, `test` y `prod`.
- Mover credenciales fuera de `application.properties`.
- Completar flujos de caja, pedidos, productos y reportes.
- Agregar datos iniciales para roles y usuario administrador.
- Cubrir servicios principales con pruebas unitarias.
- Agregar pruebas de carga FXML para detectar errores visuales en tiempo de build.
- Documentar convenciones de estilos FXML/CSS para nuevas pantallas.
