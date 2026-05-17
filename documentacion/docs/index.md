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

Una vez generado, buscar en la carpeta build/repots/jacoco/test: Seleccionar el html de los test para ver la cobertura de todas las ramas.

El reporte muestra:

* Cobertura de lineas
* Cobertura de metodos
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
* Documentacion de la API (Swagger): `http://localhost:8080/swagger-ui/index.html`
* El cliente se ejecuta en el puerto `8090` y proporciona la interfaz frontend.
* Acceso recomendado al frontend: `http://localhost:8090/login`

---

## Notas utiles

* Si no levanta Gradle, revisa que tu entorno este usando Java 21 (`JAVA_HOME` apuntando al JDK correcto).
* El proyecto incluye datos de ejemplo al arrancar por primera vez en base de datos vacia.
* Si no aparece el reporte de JaCoCo, asegúrate de ejecutar `jacocoTestReport`.
