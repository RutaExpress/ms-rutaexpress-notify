# ms-rutaexpress-notify

Microservicio de **notificaciones** de RutaExpress (plataforma de envíos de última milla).
Consume comandos desde **RabbitMQ** y ejecuta tareas asíncronas:

- Email al destinatario (envío aceptado, en ruta, entregado)
- Ticket de picking para bodega
- Generación de etiqueta / guía de despacho

> Según el caso, este servicio **no es público**: no expone endpoints en producción, solo consume colas.

## Tecnologías
- Java 21 · Spring Boot 4.1.1 · Spring AMQP
- RabbitMQ 4.2 (Docker)

## Estructura del repositorio
```
.
├── README.md
└── ms-rutaexpress-notify/      ← proyecto Spring Boot
    ├── pom.xml
    └── src/
```



## Variables de entorno
| Variable | Por defecto (local) | Uso |
|---|---|---|
| `SPRING_PROFILES_ACTIVE` | `dev` | En la nube se usa otro perfil |
| `RABBITMQ_HOST` | `localhost` | Host de RabbitMQ |
| `RABBITMQ_PORT` | `5672` | Puerto AMQP |
| `RABBITMQ_USER` | `guest` | Usuario |
| `RABBITMQ_PASSWORD` | `guest` | Clave |

## Endpoint de prueba (solo perfil `dev`)
Permite publicar mensajes desde Postman mientras se desarrolla (basado en la Guía 2.1.2, paso 9).

```
POST http://localhost:8083/api/test/publish?exchange=<exchange>&routingKey=<key>
Content-Type: application/json

{ "eventId": "b1-001", "mensaje": "hola RabbitMQ desde notify" }
```
Respuesta: `202 Accepted`. Si se omite `exchange`, se usa el exchange por defecto.

## Avance
| Paso | Descripción | Estado |
|---|---|---|
| B1 | Configuración base (conexión a RabbitMQ, ACK manual, prefetch) y REST de prueba | ✅ Probado en local |
| B2 | Topología: exchanges, colas, DLQ y bindings | ✅ Probado en local |
| B3 | Consumidor de email con ACK manual (y NACK a DLQ si el mensaje es inválido) | ✅ Probado en local |
| B4 | Errores no recuperables → NACK → DLQ, clase abstracta de consumidores y alerta de DLQ | ✅ Probado en local |
| B5 | Reintentos | ⏳ |
| B6 | Idempotencia | ⏳ |
| B7 | Consumidores de bodega y etiqueta | ⏳ |