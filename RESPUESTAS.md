# RESPUESTAS – Reto Técnico

## 1. ¿Usaste IA para construir la solución? ¿Cuál(es) y para qué exactamente? (Si no la usaste, cuéntanos cómo trabajaste.)

Sí, usé **ChatGPT** y **GitHub Copilot** para resolver dudas sobre Spring Security y Frontend, también para la escritura de código repetitivo (constructores, mapeos de repositorios) y sugerencias de mejoras en validaciones.

---

## 2. Si usaste IA (para construir o dentro del producto), cuéntanos una vez que te dio algo mal o incompleto: ¿cómo te diste cuenta y qué hiciste?

Integré un servicio de IA para generar observaciones de riesgo operativo al conceder vacaciones segun el motivo dado por el empleado y el estado de operación para la fecha especificada. ChatGPT sugirió usar KeylessAI, pero al probarlo devolvía errores 404 y 401. Intenté con OpenAPIs y Pawan Krd, entre otras, pero también fallaban por timeout o autenticación. Me di cuenta de que estos servicios gratuitos en su mayoria no funcionan. Decidí **eliminar completamente la IA del producto** y enfocarme en el flujo principal.

---

## 3. ¿Por qué elegiste esta arquitectura y este stack? ¿Qué alternativa descartaste y por qué?

Elegí **Spring Boot + Java** porque es el stack que mejor conozco y he trabajado recientemente en otro proyecto del cual reutilicé logica en este. Además, Spring Boot ofrece un ecosistema que permite construir rápidamente una aplicación confiable.

**Alternativas descartadas**:
- **Base de datos PostgreSQL en desarrollo**: Descartado por simplicidad; H2 en memoria es ideal para pruebas y no requiere instalación adicional.

---

## 4. ¿Cómo garantizas que el control de días y los estados del flujo sean siempre correctos, sin importar el orden en que ocurran las acciones? (determinismo)

El sistema está diseñado con **validaciones explícitas en cada transición de estado**:

- **Estados**: Cada acción (crear, aprobar, rechazar, confirmar) solo es válida si la solicitud está en el estado correcto. Por ejemplo, un jefe solo puede aprobar si el estado es `PENDING_BOSS`.
- **Control de días**: El descuento de días solo ocurre cuando RRHH confirma la solicitud, y se calcula usando el servicio `HolidayService` que cuenta días hábiles excluyendo fines de semana y festivos colombianos.
- **Validación de saldo**: Antes de crear una solicitud, se verifica que el empleado tenga días suficientes.
- **Superposición**: No se permite crear solicitudes que se solapen con otras ya aprobadas o pendientes.
- **Transaccionalidad**: Todas las operaciones críticas están anotadas con `@Transactional`, lo que garantiza atomicidad y consistencia.
- **Control de concurrencia**: Se usa `@Version` en las entidades para bloqueo optimista.

---

## 5. ¿Le pusiste IA al producto? Si sí, ¿dónde, por qué ahí y cómo evitas que se equivoque? Si no, ¿por qué decidiste que no aportaba?

**No**, no incluí IA en el producto final. Inicialmente sí, pero los servicios gratuitos fallaban constantemente. Decidí no incluirla porque:

- Un sistema que a veces falla por un servicio externo no cumple como mvp.

---

## 6. Si esto se usara de verdad con 200 empleados, ¿qué cambiarías o qué se rompería?

**Cambios necesarios**:
- **Base de datos**: usaría PostgreSQL o MySQL en producción para persistencia.
- **Autenticación**: Basic Auth en texto plano es inseguro; usaría JWT + OAuth2.
- **Seguridad**: Passwords hasheadas con BCrypt en lugar de `{noop}`.
- **Notificaciones**: Enviar correos electrónicos al cambiar de estado (con Spring Mail).
- **Logs y monitoreo**: Agregaría logs estructurados y métricas.
- **Frontend**: Separaría el frontend del backend para mejor rendimiento y mantenibilidad.

**Posibles rupturas**:
- **Condiciones de carrera**: Reforzar bloqueo optimista o usar pesimista.

---

## 7. ¿Qué fue lo más difícil y cómo lo resolviste?

Lo más difícil fue **el manejo de días hábiles y festivos colombianos**. Lo resolví creando un `HolidayService` con una lista de festivos para 2026 y un método que cuenta días excluyendo fines de semana y festivos.


---

## 8. Si tuvieras más tiempo, ¿qué le agregarías?

- **Notificaciones por correo** (Spring Mail) al cambiar de estado.
- **Dashboard para RRHH** con gráficos de uso de vacaciones.
- **Soporte para años múltiples** y acumulación de días no usados.
- **Mejora del frontend** con validaciones en tiempo real y mejor UX.

---

## 9. ¿Cuánto tiempo real te tomó? (Y si usaste IA, aprox. qué porcentaje hizo ella.)

Fue de aproximadamente **10 horas distribuidas en 2 días**.

- **IA (ChatGPT + Copilot)**: Ayudó en un **30-40%** del código, especialmente en Frontend y en la resolución de dudas de configuración.
- **El resto** fue trabajo manual: configuración de seguridad, lógica de negocio, pruebas, frontend, corrección de errores y despliegue pero reciclé codigo de otro proyecto que me servia.

---

## 10. Cuéntanos una idea tuya que probaste aunque no te la pedimos.

Implementé un **validador de superposición de fechas** que impide que un empleado solicite vacaciones en un período donde ya tenga otra solicitud aprobada o pendiente, pues evita confusiones y error humano. También agregué un **contador de días hábiles** que excluye automáticamente fines de semana y festivos, para ser mas preciso.

Además, diseñé una **interfaz web** con HTML+CSS+JS que permite cambiar de rol sin recargar la página, lo que facilita las pruebas y sirve para el enfoque del usuario final.