# Manual Técnico

Esta es una aplicación para poder realizar el marcaje de entrada y salida de una organización, identificando a los usuarios por medio de su nombre y su rol. Así mismo, la aplicación almacena  y organiza la información para que esta pueda ser accedida por distintas solicitudes, por otro lado, esta aplicación permite obtener reportes sobre el desempeño de los integrantes de dicha organización.

# Derechos de autor

Esta aplicación fue realizada por *Estudiantes del curso de Desarrollo Web* de la facultad de Ingeniería de la Universidad Mariano Gálvez, sede Boca del Monte del departamento de Guatemala, con fecha de 18/08/2021
### Integrantes:
|    NOMBRE      |APELLIDOS						 |CARNET                        |
|----------------|-------------------------------|-----------------------------|
|Gustavo Daniel|Monzon Flores            |7690-20-20745           |
|Kennet Joab|Guzmán Ramírez            |7690-21-2903            |
|Gerson Mauricio|Escobar Aguilar|7690-20-3975|
|Santos Genaro|Hernandez Gabriel|7690-21-21318|
|William Josue|Carrillo Sandoval|7690-21-3740|
|Fernando Omar|López Morales|7690-21-20755|
|Carlos Andrés|Ramírez García|7690-21-10603|
|Victor Emanuel|Gonzalez Ortiz|7690-13-11075|
|Vani Norwin|Alcantara Mendoza|7690-18-1298|
|Bairon Ismael|Castellanos Valle|7690-21-10973|

## Sobre un sistema de marcaje de entrada/salida
Un sistema de entrada y salida es un método utilizado por las empresas para realizar un seguimiento de las horas trabajadas de sus empleados. 
La metodología utilizada consiste en registrar la hora exacta en que un empleado comienza a trabajar (marcado de entreda) y termina el trabajo (marcado de salida).

La siguiente aplicación permite realizar el registro de los empleados y el respectivo marcaje de entrada y salida de los empleados, restringiendo ciertos accesos por medio de asignación de roles.
# Aplicación de marcaje
Esta aplicación web permite mantener un control de de horas trabajadas para una organización utilizando el sistema de marcaje entrada y salida, así mismo se lleva un control de los integrantes de la organización y se determina su acceso por medio de la asignación de roles.
Para que un usuario o empleado de la organización sea capaz de ingresar en la aplicación para marcar su entrada y salida debe identificarse con usuario y contraseña.
## Características de la aplicación
Esta aplicación trabaja por medio de microservicios, los cuales se encuentran almazenados en distintas VPS, así mismo cada VPS se ejecuta en un sistema operativo de Linux, en este caso Ubuntu versión 22.
Todos los microservicios utilizan el software Nginx como servidor, más adelante se demuestra el paso a paso para su correcta instalación y ejecución.
## Base de datos
Para la creación y gestión de la base de datos de la aplicación se utiliza la herramienta de PostgreSQL.
La base de datos se creó en un modelo entidad-relación que permite organizar las tablas apropiadamente debido a su número de atributos fácil de gestionar. La base de datos de la aplicación almacena los usuarios y el marcaje de entrada y salida.

**Script de la base de datos**
```sql
CREATE SCHEMA proyecto_final_dw
CREATE TABLE Departamento (
    id_departamento SERIAL PRIMARY KEY,
    nombre_departamento VARCHAR(100) NOT NULL
);

CREATE TABLE Rol (
    id_rol SERIAL PRIMARY KEY,
    nombre_rol VARCHAR(50) NOT NULL
);

CREATE TABLE Usuario (
    id_usuario SERIAL PRIMARY KEY,
    username VARCHAR(20) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    nombre VARCHAR(100) NOT NULL,
    apellido VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL,
    telefono VARCHAR(20),
    enabled BOOLEAN DEFAULT TRUE,
    id_departamento INT NOT NULL,
    id_horario INT NOT NULL,
    FOREIGN KEY (id_departamento) REFERENCES Departamento(id_departamento),
    FOREIGN KEY (id_departamento) REFERENCES Horario (id_horario)
);

CREATE TABLE Horario (
    id_horario SERIAL PRIMARY KEY,
    hora_entrada TIME ,
    hora_salida TIME,
    tolerancia_entrada INT NOT NULL, 
    tolerancia_salida INT NOT NULL   
);

CREATE TABLE Marcaje (
    id_marcaje SERIAL PRIMARY KEY,
    id_usuario INT NOT NULL,
    fecha_marcaje DATE NOT NULL,
    hora_entrada TIME NOT NULL,
    hora_salida TIME NOT NULL,
    dentro_de_horario BOOLEAN NOT NULL, 
    FOREIGN KEY (id_usuario) REFERENCES Usuario(id_usuario)
);

CREATE TABLE UsuarioRol (
    id_usuario_rol SERIAL PRIMARY KEY,
    id_usuario INT NOT NULL,
    id_rol INT NOT NULL,
    FOREIGN KEY (id_usuario) REFERENCES Usuario(id_usuario),
    FOREIGN KEY (id_rol) REFERENCES Rol(id_rol)
);
```

### Conexión a la base de datos
Para poder acceder a la base de datos es necesário utilizar los siguientes datos de autenticación y conexión:

- *Usuario*: dw_user
- *Password:* Dw2024+
- *IP/Host:* 3.145.108.23

**Para conectarse a la base de datos**
Para poder conectarse a la base de datos utilizamos la herramienta de pgAdmin y seguimos el siguiente paso a paso:
1. Instalar pgAdmin
2. Abrir la aplicación y seleccionar la opción de "Add New Server"
3. Configurar los detalles del servidor
3.1 En la pestaña "General" asignar un nombre a la conexión
3.2 En la pestaña "Connection" ingresar los datos del Host (IP), el Port (Puerto de PostgreSQL), Usuario y Contraseña.
4. Guardar la conexión con el botón "Save".
# Herramientas necesarias para la ejecución del programa
## Configuración de Nginx

1.  **Instalar Nginx** (si no está instalado):

```bash
sudo apt update

sudo apt install nginx
```
2.  **Configurar Nginx para servir el frontend**: 

Crear un archivo de configuración en `/etc/nginx/sites-available/frontend_marcaje_web`:

```nginx
server {

listen 80;

server_name tu_dominio_o_IP;
root /var/www/frontend_marcaje_web/dist/frontend_marcaje_web;
index index.html index.htm;
 
location / {

try_files $uri $uri/ /index.html;
}
error_page 404 /index.html;

}
```
- Cambia `tu_dominio_o_IP` por tu dominio o IP.

- Cambia la ruta `root` por el directorio donde están los archivos construidos.

3.  **Habilitar la configuración**:
```bash
sudo ln -s /etc/nginx/sites-available/frontend_marcaje_web /etc/nginx/sites-enabled/
```
4.  **Verificar la configuración**:

```bash
sudo nginx -t
```
5.  **Reiniciar Nginx**:
```bash
sudo systemctl restart nginx
```
6.  **Ajustar permisos** para el directorio:

  ```bash
sudo chown -R www-data:www-data /var/www/frontend_marcaje_web
sudo chmod -R 755 /var/www/frontend_marcaje_web
```
### Ejecución

1.  **Acceder al Frontend**:

  Navega a `http://tu_dominio_o_IP` en tu navegador para ver la aplicación Angular en ejecución.
# Instalación y configuración de Apache Tomcat
### Paso 1: Descargar Apache Tomcat
1. Abre una terminal y navega al directorio donde quieres descargar Tomcat.
2. Usa `wget` para descargar Tomcat. Por ejemplo, para Tomcat 10:
    ```sh
    wget https://downloads.apache.org/tomcat/tomcat-10/v10.0.31/bin/apache-tomcat-10.0.31.tar.gz
    ```
### Paso 2: Descomprimir el archivo
1. Extrae el archivo descargado:
    ```sh
    tar -xvzf apache-tomcat-9.0.31.tar.gz
    ```
### Paso 3: Configurar las variables de entorno
1. Abre el archivo de perfil de tu shell (por ejemplo, `~/.bashrc`) en un editor de texto:
    ```sh
    nano ~/.bashrc
    ```
2. Agrega las siguientes líneas al final del archivo:
    ```sh
    export CATALINA_HOME=/ruta/a/apache-tomcat-9.0.31
    export PATH=$PATH:$CATALINA_HOME/bin
    ```
3. Guarda el archivo y cierra el editor.
4. Recarga las variables de entorno:
    ```sh
    source ~/.bashrc
    ```
### Paso 4: Iniciar Apache Tomcat
1. Navega hasta el directorio `bin` dentro de la carpeta de Tomcat:
    ```sh
    cd $CATALINA_HOME/bin
    ```
2. Ejecuta el script de inicio:
    ```sh
    ./startup.sh
    ```
### Paso 5: Verificar la instalación
1. Abre tu navegador web y ve a `http://localhost:8080`.
2. Deberías ver la página de bienvenida de Apache Tomcat, lo que indica que la instalación fue exitosa.

### Paso 6: Configurar usuarios (opcional)
1. Abre el archivo `tomcat-users.xml` en la carpeta `conf` con un editor de texto:
    ```sh
    nano $CATALINA_HOME/conf/tomcat-users.xml
    ```
2. Agrega los usuarios y sus roles dentro del archivo:
    ```xml
    <tomcat-users>
        <role rolename="manager-gui"/>
        <role rolename="admin-gui"/>
        <user username="admin" password="password" roles="manager-gui,admin-gui"/>
    </tomcat-users>
    ```
3. Guarda los cambios y reinicia Tomcat:
    ```sh
    ./shutdown.sh
    ./startup.sh
    ```
### Paso 7: Detener Apache Tomcat
1. Navega hasta el directorio `bin` de Tomcat:
    ```sh
    cd $CATALINA_HOME/bin
    ```
2. Ejecuta el script de detención:
    ```sh
    ./shutdown.sh
    ```

# Arquitectura del Sistema
La aplicación se ejecuta por medio de microservicios distribuídos en distintas VPS lo cual incrementa la segurad y disponibilidad de la aplicación.

## Microservicio Login/ Registro/ Rol
> Link de github con el resto del código: https://github.com/Vanii-UMG/oauth-server/tree/master
### Endpoints utilizados
- **/login** POST Se logea en el servicio y devuelve el token
- **/signin** POST se registra en el servicio y se guarda en dB
- **/nuevo** Permite ingresar un nuevo usuario
- **/reset-password** PUT reinicia la contraseña del usuario
- **/rol/{idUsuario}** Obtener los roles de los usuarios por su id
- **/{id}** PUT actualizar usuario por su id
- **/{id}** DELETE eliminar un usuario por su id
### Funciones utilizadas para el Login
Para la función de Login se utilizó el sistema de Keycloak. La clase a continuación es la clase KeycloackService.kt la cuál cuenta con varias funciones para obtener la información de empleados, registro de usuarios, asignación de roles y restablecimiento de contraseñas.
 **Propiedades de la clase principal**
 -   `employeeService`: Servicio utilizado para obtener la información de los empleados.    
-   `restTemplateKeycloak`: Cliente REST para interactuar con Keycloak.    
-   `clientID`, `grantType`, `roleId`, `roleName`: Variables de configuración para Keycloak.    
-   `page`, `pageSize`: Configuración de paginación para la consulta de usuarios en Keycloak.    
-   `temporary`: Indica si la contraseña es temporal.    
-   `logger`: Logger para registrar mensajes e información.    
-   `headers`: Headers para las solicitudes HTTP.
  -   `mapper`: ObjectMapper para transformar objetos JSON.
**Métodos utilizados**
Se obtiene toda la información de los empleados en una lista de keyEmployeeResponse con el formato para crear usuarios en Keycloak.
```java
    fun getAllEmployeesInfo(): List<KeyEmployeeResponse>? {
        //return employeeService.getAllEmployees().asResponseKey()
    }
```
Cada uno de los empleados registrados reciben un usuario para acceder al sistema de marcaje.Es decir, cada empleado es recibido para realizar el post en la URI de keycloak que registra a los usuarios.
```java
fun addEmployeeKeycloak() {
        headers.contentType = MediaType.APPLICATION_JSON
        headers.accept = Collections.singletonList(MediaType.APPLICATION_JSON)

        getAllEmployeesInfo()?.forEach { employee ->

            val userPayload = mapOf(
                "username" to employee.username,
                "firstName" to employee.firstName,
                "lastName" to employee.lastName,
                "enabled" to true,
                "credentials" to listOf(
                    mapOf(
                        "type" to grantType,
                        "value" to "12345678",
                        "temporary" to temporary
                    )
                ),
                "attributes" to mapOf(
                    "idAutogestion" to listOf(employee.id),
                    "codeEmployee" to listOf(employee.employeeCode),
                )
            )

            try {
                restTemplateKeycloak.postForEntity(
                    "/users",
                    HttpEntity(userPayload, headers),
                    Void::class.java
                )

            } catch (e: HttpClientErrorException) {
                logger.warn("No se agrego el empleado ${employee.username} al servicio de keycloak: ${e.message}")
            }
        }
        logger.info("Los empleados fueron agregados correctamente al servicio de keycloak")
    }
 ```
Se obtiene la lista con todos los usuarios registrados en un mismo realm (motor de base de datos) de keycloak.
```java  
    fun getAllEmployeeKeycloak(): List<KeycloakResponse> {
        val allUsers = mutableListOf<KeycloakResponse>()

        while (true) {
            val response = restTemplateKeycloak.getForEntity(
                "/users?first=${page * pageSize}",
                Array<KeycloakResponse>::class.java
            )

            val users = response.body?.toList() ?: emptyList()

            if (users.isEmpty())
                break

            allUsers.addAll(users)
            page++
        }

        page = 0
        return allUsers
    }
```
Ahora se asigna el rol autogestion a todos los usuarios, cada usuario es recibido para realizar el post en la uri de kycloack que asigna los roles.
```java  
 fun addRoleEmployeeKeycloak() {

        headers.contentType = MediaType.APPLICATION_JSON
        headers.accept = Collections.singletonList(MediaType.APPLICATION_JSON)

        val userPayload = mapOf(
            "id" to roleId,
            "name" to roleName
        )

        getAllEmployeeKeycloak().forEach { employee ->

            try {
                restTemplateKeycloak.postForEntity(
                    "/users/${employee.id}/role-mappings/clients/${clientID}",
                    HttpEntity(
                        "[${mapper.writeValueAsString(userPayload)}]",
                        headers
                    ),
                    Void::class.java
                )
            } catch (e: HttpClientErrorException) {
                logger.warn("No se asignaron los roles al usuario ${employee.username}: ${e.message}")
            }
        }

        logger.info("Se asignaron correctamente los roles a los empleados")
    }
```
Esta función permite restablecer la contraseña de un empleado por medio del sistema keycloak, utilizando el id del usuario como parámetro.
```java  
    fun resetPasswordEmployeeKeycloak(idUser: UUID) {

        headers.contentType = MediaType.APPLICATION_JSON
        headers.accept = Collections.singletonList(MediaType.APPLICATION_JSON)
        val userPayload = mapOf(
            "type" to grantType,
            "value" to "12345678",
            "temporary" to temporary,
        )

        val userKeycloak = getAllEmployeeKeycloak().find { user ->
            user.attributes?.get("idAutogestion")?.contains(idUser) == true
        }
        try {
            restTemplateKeycloak.put(
                "/users/${userKeycloak?.id}/reset-password",
                HttpEntity(userPayload, headers)
            )

            logger.info("Se establecio correctamente la contraseña al empleado")

        } catch (e: HttpClientErrorException) {
            logger.warn("No se establecio la contraseña al empleado: ${e.message}")
        }
    }
```
## Microservicio Marcaje/ Lista/ Total
> Link del github: https://github.com/GersonEscobar99/marcaje-backend.git
### Endpoints utilizados
- Endpoint de este servicio: http://desarrollow.com/marcaje/proyecto-final/api/marcajes
#### Registrar Entrada `/entrada/{idUsuario}`
- **Método**: POST
- **Descripción**: Registra la entrada de un usuario.
- **Parámetros**: `idUsuario` (Long): ID del usuario.
- **Respuesta**: `200 OK`: Retorna `MarcajeDTO`.

#### Registrar Salida `/salida/{idUsuario}`
- **Método**: POST
- **Descripción**: Registra la salida de un usuario.
- **Parámetros**:   `idUsuario` (Long): ID del usuario.
- **Respuesta**: 
  - `201 CREATED`: Retorna `MarcajeDTO`.
  - `400 BAD REQUEST`: El usuario no tiene una entrada sin salida.
  - `404 NOT FOUND`: El usuario no existe.

#### Obtener Marcajes por Username `/historial/username/{username}`
- **Método**: GET
- **Descripción**: Obtiene marcajes por username.
- **Parámetros**: `username` (String): Username del usuario.
- **Respuesta**: 
  - `200 OK`: Retorna una lista de `MarcajeDTO`.
  - `404 NOT FOUND`: El usuario no existe.

#### Obtener Historial de Marcajes por ID `/historial/id/{id}`
- **Método**: GET
- **Descripción**: Obtiene el historial de marcajes por ID de usuario.
- **Parámetros**: `id` (Long): ID del usuario.
- **Respuesta**: 
  - `200 OK`: Retorna una lista de `MarcajeDTO`.
  - `204 NO CONTENT`: No se encontraron marcajes para el usuario.
  - `404 NOT FOUND`: Usuario con ID no encontrado.
#### Obtener Marcajes por Departamento `/departamento/{idDepartamento}`
- **Método**: GET
- **Descripción**: Obtiene marcajes por departamento.
- **Parámetros**: `idDepartamento` (Long): ID del departamento.
- **Respuesta**: 
  - `200 OK`: Retorna una lista de `MarcajeDTO`.
  - `204 NO CONTENT`: No se encontraron marcajes.

#### Obtener Marcajes Fuera de Horario `/fuera/horario/todos`
- **Método**: GET
- **Descripción**: Obtiene todos los marcajes fuera de horario.
- **Respuesta**: 
  - `200 OK`: Retorna una lista de `MarcajeDTO`.
  - `204 NO CONTENT`: No se encontraron marcajes.

#### Obtener Usuarios Fuera de Horario `/fuera/horario`
- **Método**: GET
- **Descripción**: Obtiene usuarios fuera de horario.
- **Respuesta**: 
  - `200 OK`: Retorna una lista de `Usuario`.
  - `204 NO CONTENT`: No se encontraron usuarios.

#### Obtener Marcaje por ID `/marcaje/{id}`
- **Método**: GET
- **Descripción**: Obtiene un marcaje por ID.
- **Parámetros**: - `id` (Long): ID del marcaje.
- **Respuesta**: 
  - `200 OK`: Retorna `MarcajeDTO`.
### Funciones utilizadas para almacenamiento del marcaje
**`RegistrarEntrada`**
Registra la entrada de un usuario, verificando si está dentro del horario permitido y almacenando la información.
```java
public MarcajeDTO registrarEntrada(Long idUsuario) {
    Usuario usuario = usuarioRepository.findById(idUsuario)
        .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
    Marcaje marcaje = new Marcaje();
    marcaje.setUsuario(usuario);
    marcaje.setFechaMarcaje(LocalDate.now());
    marcaje.setHoraEntrada(LocalTime.now());
    Horario horarioUsuario = usuario.getHorario();
    marcaje.setDentroDeHorario(verificarHorarioEntrada(marcaje.getHoraEntrada(), horarioUsuario));
    Marcaje savedMarcaje = marcajeRepository.save(marcaje);
    return new MarcajeDTO(
        savedMarcaje.getIdMarcaje(),
        usuario.getIdUsuario(),
        usuario.getUsername(),
        savedMarcaje.getFechaMarcaje(),
        savedMarcaje.getHoraEntrada(),
        savedMarcaje.getHoraSalida(),
        savedMarcaje.getDentroDeHorario()
    );
}
```
**`Registrar Salida`**
Registra la salida de un usuario, asegurándose de que esté dentro del horario permitido y actualizando la información correspondiente.
```java
public MarcajeDTO registrarSalida(Usuario usuario) {
    List<Marcaje> marcajes = marcajeRepository.findByUsuario(usuario);
    if (!marcajes.isEmpty()) {
        Marcaje ultimoMarcaje = marcajes.get(marcajes.size() - 1);
        if (ultimoMarcaje.getHoraSalida() == null) {
            Horario horarioUsuario = usuario.getHorario();
            ultimoMarcaje.setHoraSalida(LocalTime.now());
            ultimoMarcaje.setDentroDeHorario(verificarHorarioSalida(LocalTime.now(), horarioUsuario));
            Marcaje updatedMarcaje = marcajeRepository.save(ultimoMarcaje);
            return new MarcajeDTO(
                updatedMarcaje.getIdMarcaje(),
                usuario.getIdUsuario(),
                usuario.getUsername(),
                updatedMarcaje.getFechaMarcaje(),
                updatedMarcaje.getHoraEntrada(),
                updatedMarcaje.getHoraSalida(),
                updatedMarcaje.getDentroDeHorario()
            );
        }
    }
    return null;
}
```
**`Convertir a marcaje DTO`**
```java
private MarcajeDTO convertirAMarcajeDTO(Marcaje marcaje) {
    return new MarcajeDTO(
        marcaje.getIdMarcaje(),
        marcaje.getUsuario().getIdUsuario(),
        marcaje.getUsuario().getUsername(),
        marcaje.getFechaMarcaje(),
        marcaje.getHoraEntrada(),
        marcaje.getHoraSalida(),
        marcaje.getDentroDeHorario()
    );
}
```
**`Obtener Marcajes por usuario`**
Obtiene todos los marcajes de un usuario específico y los convierte a `MarcajeDTO`.
```java
public List<MarcajeDTO> obtenerMarcajes(Usuario usuario) {
    return marcajeRepository.findByUsuario(usuario)
        .stream()
        .map(this::convertirAMarcajeDTO)
        .collect(Collectors.toList());
}
```
**`Obtener Todos Los Marcajes`**
```java
public List<MarcajeDTO> obtenerTodosLosMarcajes() {
    return marcajeRepository.findAll()
        .stream()
        .map(this::convertirAMarcajeDTO)
        .collect(Collectors.toList());
}
```
**`Obtener Lista de Marcaje`**
Obtiene el primer marcaje de un usuario, ordenado por fecha de marcaje, y lo convierte a `MarcajeDTO`.
```java
public Optional<MarcajeDTO> obtenerListaDeMarcajes(Long idUsuario) {
    return marcajeRepository.findFirstByUsuarioIdUsuarioOrderByFechaMarcajeDesc(idUsuario)
        .map(this::convertirAMarcajeDTO);
}
```
**`Verificar Hora Entrada`**
Verifica si la hora de entrada está dentro del horario permitido.
```java
private boolean verificarHorarioEntrada(LocalTime horaEntrada, Horario horario) {
    LocalTime horaLimiteEntrada = horario.getHoraEntrada().plusMinutes(horario.getToleranciaEntrada());
    return horaEntrada.isBefore(horaLimiteEntrada) || horaEntrada.equals(horaLimiteEntrada);
}
```
**`Verificar Hora Salida`**
Verifica si la hora de salida está dentro del horario permitido
```java
private boolean verificarHorarioSalida(LocalTime horaSalida, Horario horario) {
    LocalTime horaLimiteSalida = horario.getHoraSalida().minusMinutes(horario.getToleranciaSalida());
    return horaSalida.isAfter(horaLimiteSalida) || horaSalida.equals(horaLimiteSalida);
}
```
**`Obtener Marcaje por Departamento`**
Obtiene los marcajes de un departamento específico y los convierte a `MarcajeDTO`.
```java
public List<MarcajeDTO> obtenerMarcajesPorDepartamento(Long idDepartamento) {
    return marcajeRepository.findByDepartamentoId(idDepartamento)
        .stream()
        .map(this::convertirAMarcajeDTO)
        .collect(Collectors.toList());
}
```
**`Obtener Marcaje fuera de horario`**
Obtiene los marcajes realizados fuera del horario permitido y los convierte a `MarcajeDTO`.
```java
public List<MarcajeDTO> obtenerMarcajesFueraDeHorario() {
    return marcajeRepository.findMarcajesFueraDeHorario()
        .stream()
        .map(this::convertirAMarcajeDTO)
        .collect(Collectors.toList());
}
```
**`Obtener marcaje por usuario`**
**Por ID**
```java
public Marcaje obtenerMarcajePorId(Long idMarcaje) {
    return marcajeRepository.findById(idMarcaje)
        .orElseThrow(() -> new RuntimeException("Marcaje no encontrado con id: " + idMarcaje));
}
```
**Por nombre ID de usuario**
```java
public List<MarcajeDTO> obtenerMarcajesPorUsuarioId(Long idUsuario) {
    List<Marcaje> marcajes = marcajeRepository.findByUsuario_IdUsuario(idUsuario);
    return marcajes.stream()
        .map(marcaje -> new MarcajeDTO(
            marcaje.getIdMarcaje(),
            marcaje.getUsuario().getIdUsuario(),
            marcaje.getUsuario().getUsername(),
            marcaje.getFechaMarcaje(),
            marcaje.getHoraEntrada(),
            marcaje.getHoraSalida(),
            marcaje.getDentroDeHorario()))
        .collect(Collectors.toList());
}
```
## Microservicio Reporte Marcaje General
>Link de github con el resto de código: https://github.com/KennetJRamirez/Microservicio-Reporteria/tree/main

Datos de la VPS en donde se gestiona el microservicio de reportería:
*IP pública:* 35.172.219.43

Se utiliza la herramienta de Maven para poder gestionar los datos y así mismo se utilizó el software de Jasper para poder generar las plantillas en formato .jrxml.
Link del software: https://community.jaspersoft.com/download-jaspersoft/community-edition/

Las dependencias para el Jasper son las siguientes:

```xml
<!--  JasperReports  -->
<dependency>
<groupId>net.sf.jasperreports</groupId>
<artifactId>jasperreports</artifactId>
<version>6.20.6</version>
</dependency>

  <!--  JasperReports  Fonts  -->

<dependency>
<groupId>net.sf.jasperreports</groupId>
<artifactId>jasperreports-fonts</artifactId>
<version>6.20.6</version>
</dependency>
 
<!--  Itext  for  PDF  Export  -->

<dependency>
<groupId>com.itextpdf</groupId>
<artifactId>itextpdf</artifactId>
<version>5.5.13.2</version>
</dependency>

  <!--  Lombok  -->

<dependency>
<groupId>org.projectlombok</groupId>
<artifactId>lombok</artifactId>
<version>1.18.24</version>
<scope>provided</scope>
</dependency>
 
<!--  PostgreSQL  -->
<dependency>

<groupId>org.postgresql</groupId>

<artifactId>postgresql</artifactId>

<version>42.6.0</version>

</dependency>
</dependencies>
```

**Paso a paso del proceso en la generación de un reporte:**
1. Se utiliza una clase *InputStream* para leer el archivo generado *.jrxml* (la plantilla).
2. Se crea un objeto Jasper el cual recibe la plantilla
3. Se llama la función SQL del reporte y se ejecuta
4. Se mapean los parámetros
5. La clase Jasper llena el reporte pdf
6. Se genera un archivo .pdf
7. Los 3 reportes generados en las funciones se integran en un solo reporte y se exporta a una entrada
8. Finalmente se genera un nuevo objeto Jasper Output
9. Se exporta el pdf incluyendo toda la información obtenida por medio de las consultas hacia la base de datos
10. El método exportReport genera el pdf
11. El reporte generado se publica con el endpoint */pdf-nombre-ruta*

#### Función de extracción de la información en la base de datos
``` bash
public interface MarcajeRepository extends JpaRepository<Marcaje, Integer> {

    // Para encontrar registros fuera del horario de entrada
    @Query("SELECT m FROM Marcaje m WHERE m.dentroDeHorario = false AND m.horaEntrada > m.usuario.horario.horaEntrada")
    List<Marcaje> findMarcajesFueraDeHorario();

    // Para encontrar empleados que marcaron antes de la hora de salida
    @Query("SELECT m FROM Marcaje m WHERE m.horaSalida < m.usuario.horario.horaSalida")
    List<Marcaje> findMarcajesAntesDeHoraSalida();
}
```
#### Función para generar el pdf
``` bash
    @GetMapping("/pdf-fuera-horario")
    public ResponseEntity<?> generarPdfFueraDeHorario(HttpServletResponse response) {
        try {
            response.setContentType("application/pdf");
            response.setHeader("Content-Disposition", "attachment; filename=empleados_fuera_horario.pdf");

            List<Marcaje> marcajes = reporteService.obtenerEmpleadosFueraDeHorario();
            InputStream jasperStream = this.getClass().getResourceAsStream("/empleados_fuera_horario.jrxml");
            JasperReport jasperReport = JasperCompileManager.compileReport(jasperStream);

            JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(marcajes);
            Map<String, Object> parameters = new HashMap<>();
            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, dataSource);

            JRPdfExporter exporter = new JRPdfExporter();
            exporter.setExporterInput(new SimpleExporterInput(jasperPrint));
            exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(response.getOutputStream()));
            exporter.exportReport();

            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("codigo", 1, "mensaje", "Consulte con el administrador"));
        }
    }
``` 
#### Código para realizar pruebas por medio de Postman
```json
{
	"info": {
		"_postman_id": "f1bfd1d1-3b0f-42f1-ae26-5bfe3568e96b",
		"name": "Reporteria marcaje",
		"schema": "https://schema.getpostman.com/json/collection/v2.1.0/collection.json",
		"_exporter_id": "31371725"
	},
	"item": [
		{
			"name": "Marcaje general",
			"request": {
				"method": "GET",
				"header": [],
				"url": {
					"raw": "http://35.172.219.43:8080/reportes/marcajeGeneral",
					"protocol": "http",
					"host": [
						"35",
						"172",
						"219",
						"43"
					],
					"port": "8080",
					"path": [
						"reportes",
						"marcajeGeneral"
					]
				}
			},
			"response": []
		},
		{
			"name": "Marcaje fuera de horario",
			"request": {
				"method": "GET",
				"header": [],
				"url": {
					"raw": "http://35.172.219.43:8080/reportes/pdf-fuera-horario",
					"protocol": "http",
					"host": [
						"35",
						"172",
						"219",
						"43"
					],
					"port": "8080",
					"path": [
						"reportes",
						"pdf-fuera-horario"
					]
				}
			},
			"response": []
		},
		{
			"name": "Marcaje antes hora salida",
			"request": {
				"method": "GET",
				"header": [],
				"url": {
					"raw": "http://35.172.219.43:8080/reportes/pdf-antes-hora-salida",
					"protocol": "http",
					"host": [
						"35",
						"172",
						"219",
						"43"
					],
					"port": "8080",
					"path": [
						"reportes",
						"pdf-antes-hora-salida"
					]
				}
			},
			"response": []
		},
		{
			"name": "Marcaje por departamento",
			"request": {
				"method": "POST",
				"header": [],
				"body": {
					"mode": "raw",
					"raw": "\r\n{\r\n  \"departamentoId\": 1\r\n}\r\n",
					"options": {
						"raw": {
							"language": "json"
						}
					}
				},
				"url": {
					"raw": "http://35.172.219.43:8080/reportes/departamento",
					"protocol": "http",
					"host": [
						"35",
						"172",
						"219",
						"43"
					],
					"port": "8080",
					"path": [
						"reportes",
						"departamento"
					]
				}
			},
			"response": []
		},
		{
			"name": "Marcaje por Usuario",
			"request": {
				"method": "POST",
				"header": [],
				"body": {
					"mode": "raw",
					"raw": "\r\n{\r\n  \"usuarioId\": 1\r\n}\r\n",
					"options": {
						"raw": {
							"language": "json"
						}
					}
				},
				"url": {
					"raw": "http://35.172.219.43:8080/reportes/individual",
					"protocol": "http",
					"host": [
						"35",
						"172",
						"219",
						"43"
					],
					"port": "8080",
					"path": [
						"reportes",
						"individual"
					]
				}
			},
			"response": []
		}
	]
}
```
## Microservicio Frontend
>Link del repositorio en github que contiene este microservicio: https://github.com/wcarrillo1/proyecto_final

El Frontend se realizó por medio de la herramienta de Angular, por lo tanto para poder administrarla, editarla y ejecutarla es necesário contar con la aplicación instalada y ejecutándose.
### Instalación de Angular en Linux

#### Paso 1: Instalar Node.js y npm
1. Abre una terminal y asegúrate de tener instalado Node.js y npm. Si no los tienes, puedes instalarlos con los siguientes comandos:
    ```sh
    curl -sL https://deb.nodesource.com/setup_14.x | sudo -E bash -
    sudo apt-get install -y nodejs
    ```
2. Verifica la instalación de Node.js y npm:
    ```sh
    node --version
    npm --version
    ```

#### Paso 2: Instalar Angular CLI
1. Una vez que Node.js y npm estén instalados, puedes instalar Angular CLI usando npm:
    ```sh
    sudo npm install -g @angular/cli
    ```
2. Verifica la instalación de Angular CLI:
    ```sh
    ng version
    ```

#### Paso 3: Crear un nuevo proyecto Angular
1. Crea un nuevo proyecto Angular en el directorio de tu elección:
    ```sh
    mkdir mi-proyecto-angular
    cd mi-proyecto-angular
    ng new mi-proyecto-angular
    ```
2. Sigue las instrucciones en pantalla para configurar tu proyecto. Puedes aceptar los valores predeterminados o personalizar según tus necesidades.

#### Paso 4: Iniciar el proyecto
1. Navega al directorio del proyecto:
    ```sh
    cd mi-proyecto-angular
    ```
2. Inicia el servidor de desarrollo:
    ```sh
    ng serve
    ```
3. Abre tu navegador web y ve a `http://localhost:4200` para ver tu aplicación Angular en acción.
### Dominio propio
El dominio propio es un nombre reservado y único en internet que identifica una subárea, sustituyendo la dirección IPv4.
Conectada a la VPS que administra el Fronted se utiliza el dominio **desarrollow.com** el cual fue registrado a través de los servicios de reventa de Amazon.
Para que el dominio permanezca conectado a los servicios VPS de Amazon necesita mantener los siguientes registros NS:
- ns-1228.awsdns-25.org  
- ns-135.awsdns-16.com  
- ns-1881.awsdns-43.co.uk  
- ns-563.awsdns-06.net
> Para poder revisar el estado del domínio y su conexión se puede utilizar el site whatsmydns.net

### VPS
Es un servidor virtual privado que actúa como un entorno virtual aislado en un servidor físico que pertenece y opera un proveedor de hosting web o en la nube.
**Servicios AWS Lightsail**
Es un servicio que porpociona recursos en la nube fáciles de usar para hacer funcionar un aplicación web. Lightsail ofrece servicios simplificados como instancias, contenedores, base de datos, almacenamiento, etc.

La aplicación utiliza un servicio de VPS por medio de AWS Lightsail para almacenar la mayoría de los microservicios; es decir, cada microservicio se encuentra en una VPS distinta, esto permite proporcionar mayor disponibilidad de la aplicación y confiabilidad en caso de presentarse problemas en un servidor en específico.
