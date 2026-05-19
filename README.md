# DeustoHotel

Este proyecto consiste en la gestion y administracion de un hotel, simulando historias de usuario reales que cualquier usuario podria encontrarse en la vida real.
La aplicacion esta desarrollada con Spring Boot y gestionada con Gradle.

---

## Tecnologias utilizadas

* Java 21
* Spring Boot
* Gradle
* JUnit 5
* MySQL

---

## Requisitos previos

Antes de construir y ejecutar el proyecto, asegurate de tener instalado:

* Java 21 o superior
* MySQL
* Gradle (opcional, si no se usa el wrapper)

> Recomendado: usar el wrapper incluido en el proyecto (`gradlew` / `gradlew.bat`), asi no es necesario instalar Gradle manualmente.

---

## Construccion y ejecucion del proyecto (primera vez)

1. Clonar el repositorio y entrar al proyecto.

```bash
git clone <URL_DEL_REPOSITORIO>
cd DeustoHotel

```

2. Abrir MySQL Workbench.
3. Crear la base de datos.

```sql
CREATE DATABASE deusto_hotel;

```

4. Crear usuario y darle privilegios.

```sql
CREATE USER 'a'@'localhost' IDENTIFIED BY 'a';
GRANT ALL PRIVILEGES ON *.* TO 'a'@'localhost';
FLUSH PRIVILEGES;

```

5. Verificar version de Java (debe ser 21 o superior).

```bash
java -version

```

6. Compilar el proyecto.

### En Windows (PowerShell / CMD)

```powershell
.\gradlew.bat clean build

```

### En Linux/macOS

```bash
./gradlew clean build

```

---

## Ejecucion de tests

El proyecto incluye tests automatizados con JUnit 5.

### Ejecutar todos los tests

#### Windows

```powershell
.\gradlew.bat test

```

#### Linux/macOS

```bash
./gradlew test

```

### Ejecutar tests del server

#### Windows

```powershell
.\gradlew.bat :server:test

```

#### Linux/macOS

```bash
./gradlew :server:test

```

### Ejecutar un test especifico

```bash
./gradlew :server:test --tests "com.tu.paquete.NombreDelTest"

```

---

## Cobertura de codigo con JaCoCo

El proyecto utiliza JaCoCo para medir la cobertura de los tests.

### Ejecutar tests + generar reporte

#### Windows

```powershell
.\gradlew.bat :server:test :server:jacocoTestReport

```

#### Linux/macOS

```bash
./gradlew :server:test :server:jacocoTestReport

```

### Acceder al reporte

Una vez generado, buscar en la carpeta `server/build/reports/jacoco/test`: seleccionar el HTML para ver la cobertura (líneas, ramas y métodos) del módulo `server`.

El reporte muestra:

* Cobertura de líneas
* Cobertura de métodos
* Clases no cubiertas

---

## Integracion continua

El proyecto incluye integracion continua mediante GitHub Actions.

### ¿Que hace?

* Ejecuta automaticamente los tests en cada push y pull request
* Verifica que el proyecto compila correctamente
* Ayuda a detectar errores antes de integrar cambios

---

## Ejecucion del sistema

7. Ejecutar server y cliente en terminales separadas.

### Terminal 1 - Server (puerto 8080)

#### Windows

```powershell
.\gradlew.bat :server:bootRun

```

#### Linux/macOS

```bash
./gradlew :server:bootRun

```

### Terminal 2 - Client Proxy (puerto 8090)

#### Windows

```powershell
.\gradlew.bat :client-proxy:bootRun

```

#### Linux/macOS

```bash
./gradlew :client-proxy:bootRun

```

---

## Funcionamiento del proyecto

* El servidor se ejecuta en el puerto `8080`.
* Documentacion de la API (Swagger / OpenAPI): `http://localhost:8080/swagger-ui/index.html`

---

## Documentación de la API (Swagger)

La API REST está documentada vía OpenAPI/Swagger mediante la dependencia `springdoc-openapi`. Está disponible en tiempo de ejecución en la siguiente dirección:

* [http://localhost:8080/swagger-ui/index.html](https://www.google.com/search?q=http://localhost:8080/swagger-ui/index.html)

---

## Documentación del proyecto

La documentación general del proyecto está disponible en dos formas principales:

1. JavaDoc generado a partir del código fuente del módulo `server` (comentarios Javadoc en las clases y APIs).
2. Un sitio estático que incluye el JavaDoc integrado y páginas de índice (configurado con MkDocs en la carpeta `documentacion/`).

Ubicación local de la documentación ya generada:

* `documentacion/docs/javadoc` - JavaDoc generado (puede abrirse directamente en un navegador).
* `documentacion/` - configuración de MkDocs (`mkdocs.yml`) que referencia el JavaDoc y otras páginas estáticas.

Cómo se generó y cómo regenerarla:

Requisitos previos:

* JDK 21 (para ejecutar Gradle y generar JavaDoc)
* (Opcional) Python y MkDocs con el theme `material` si quieres servir/volver a generar el sitio con MkDocs

Comandos (Windows - PowerShell):

```powershell
# Generar JavaDoc desde el módulo server (depositará el resultado en documentacion/docs/javadoc)
.\gradlew.bat :server:javadoc

```

Comandos (Linux/macOS):

```bash
./gradlew :server:javadoc

```

Si quieres servir el sitio completo con MkDocs (necesitas Python y mkdocs-material):

```powershell
# Instalar MkDocs y el tema material (si no está instalado)
pip install mkdocs mkdocs-material

# Construir el sitio estático dentro de documentacion/site (u otra carpeta configurada)
mkdocs build -f documentacion/mkdocs.yml -d documentacion/site

# Servir localmente para desarrollo
mkdocs serve -f documentacion/mkdocs.yml

```

Notas importantes:

* El link para acceder a la web de la documentación es [http://ibaivl1605.github.io/DeustoHotel/](http://ibaivl1605.github.io/DeustoHotel/)
* El `task` Gradle `:server:javadoc` está configurado en `server/build.gradle` para generar el JavaDoc directamente en `documentacion/docs/javadoc`.
* El JavaDoc se extrae de los comentarios en el código del módulo `server` (anotaciones, javadoc tags, comentarios de métodos y clases).
* Si faltan páginas adicionales en `documentacion/docs`, añadir archivos Markdown en `documentacion/docs/` y actualizar `documentacion/mkdocs.yml`.
* El cliente se ejecuta en el puerto `8090` y proporciona la interfaz frontend.
* Acceso recomendado al frontend: `http://localhost:8090/login`

---

## Notas utiles

* Si no levanta Gradle, revisa que tu entorno este usando Java 21 (`JAVA_HOME` apuntando al JDK correcto).
* El proyecto incluye datos de ejemplo al arrancar por primera vez en base de datos vacia.
* Si no aparece el reporte de JaCoCo, asegúrate de ejecutar `jacocoTestReport`.
