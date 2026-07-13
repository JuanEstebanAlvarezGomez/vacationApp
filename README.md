# VacationApp - Sistema de Solicitud de Vacaciones

Mini-aplicación para gestionar solicitudes de vacaciones con flujo de aprobación (Empleado → Jefe → RRHH). Desarrollada con **Spring Boot 3.5.16**, **Java 25** y **PostgreSQL**.

---

## Diseño arquitectónico

La solución se compone de tres capas principales:

- **Frontend**: HTML + CSS + JavaScript.
- **Backend**: API REST con Spring Boot, dividida en controladores, servicios, repositorios y entidades.
- **Persistencia**: PostgreSQL en deploy.

### Diagrama de componentes en Mermaid
https://mermaid.ai/d/429a7dab-e462-4482-9efa-f03c28e1f018

---

## Infraestructura propuesta

- **Despliegue**: Render (PaaS) con Docker.

- **Base de datos**: PostgreSQL desde render.

- **Almacenamiento de archivos estáticos**: Servidos desde el mismo JAR.

---

## Tecnologías elegidas y por qué

- **Java	v25**:	Última versión LTS en desarrollo, permite usar features modernos y tiene soporte.
- **Spring Boot	v3.5.16**:	Framework productivo que facilita la creación de APIs REST, seguridad y JPA.
- **Spring Data JPA**:	Abstracción de persistencia, reduce código boilerplate.
- **Spring Security**:	Autenticación y autorización basada en roles (Basic Auth para simplicidad).
- **PostgreSQL	v18**:	Base de datos relacional gratuita en Render.
- **Lombok	v1.18.38**: Reduce código repetitivo (getters, setters y constructores).
- **SpringDoc OpenAPI	v2.8.9**:	Genera documentación Swagger de forma automática.
- **HTML/CSS/JS**: Frontend liviano sin dependencias externas, fácil de integrar como recursos estáticos.
- **Docker**: Contenerización para despliegue consistente en cualquier entorno.
- **Render**:	Plataforma PaaS gratuita para desplegar la aplicación con Docker.

---

## Cómo correrlo (pasos)

1. Ingresa a https://vacationapp.onrender.com.

2. Ingresa las credenciales de inicio, **ya estan definidas**:

- **Empleado**: ManuelLara | pass    (ID: 4 para consulta)
- **Jefe**: RobertoTapiasPO | pass   (ID: 5 para consulta)
- **RRHH**: AlejaRRHH | pass         (ID: 6 para consulta)

3. Selecciona un rol en el desplegable y haz clic en "Ingresar".

4. Interactúa según el rol:

- **Empleado**: Crea solicitudes y consulta tu historial.

- **Jefe**: Aprueba o rechaza solicitudes pendientes.

- **RRHH**: Confirma solicitudes aprobadas y consulta saldos de empleados.

Tambien puedes ingresar al swagger para revisar el json de peticiones:

https://vacationapp.onrender.com/swagger-ui/index.html

---

## Cómo usaste la IA para construirlo

Durante el desarrollo de esta solución, se utilizaron herramientas de IA como apoyo para acelerar el proceso y mejorar la calidad del código.

### Herramientas utilizadas

- **ChatGPT**: Para resolver dudas sobre Spring Security, JPA y buenas prácticas.
- **GitHub Copilot**: Para autocompletar código repetitivo y sugerir mejoras en tiempo real.

### ¿En qué ayudó la IA?

- Generación de la estructura base del proyecto (pom.xml, dependencias, configuración de Spring Boot).
- Creación del frontend HTML/CSS/JS con funciones para cada rol.
- Sugerencias para la integración con PostgreSQL y el despliegue en Render.

### ¿Qué tuviste que corregir?

- **IA en el producto**: Inicialmente se integró un servicio de IA para generar observaciones de riesgo operativo, pero se eliminó para priorizar un sistema estable.
- **Errores de compilación**: Se corrigieron problemas con las versiones de Lombok y el plugin de Maven en Java 25.
- **Conexión a PostgreSQL**: Se ajustaron las URLs y credenciales para que funcionaran correctamente en Render, separando las variables de entorno (`DB_HOST`, `DB_USER`, `DB_PASSWORD`).
- **CORS**: Se corrigió el frontend para que las peticiones apunten al mismo origen en Render.
