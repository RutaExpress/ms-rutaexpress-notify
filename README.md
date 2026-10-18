# ms-rutaexpress-notify

Microservicio de **notificaciones** de RutaExpress (plataforma de envíos de última milla).
Consume comandos desde **RabbitMQ** que publica `ms-rutaexpress-shipments` y ejecuta tareas asíncronas:

- Email al destinatario (envío aceptado, en ruta, entregado, cancelado) → `q.cmd.email`
- Ticket de picking para bodega (crear / anular) → `q.cmd.warehouse`
- Generación de etiqueta / guía de despacho → `q.cmd.label`

> Según el caso, este servicio **no es público**: no expone endpoints HTTP, solo consume colas.

## Tecnologías
Java 21 · Spring Boot 4.1.1 · Spring AMQP · RabbitMQ 4.2 (clúster de 2 nodos)

## Estructura
```
.
├── README.md
├── .github/workflows/ci.yml    ← CI: build, tests y build de la imagen
└── ms-rutaexpress-notify/
    ├── Dockerfile
    ├── pom.xml
    └── src/main/java/ms_rutaexpress_notify/
        ├── domain/          modelos (records), excepciones, puertos in/out
        ├── application/     casos de uso (validación del mensaje)
        └── infrastructure/
            ├── config/      RabbitMQConfig (topología), RetryProperties
            └── adapter/
                ├── in/messaging/   AbstractCommandListener + listeners email, warehouse, label, dlq
                └── out/            adaptadores de email, bodega y etiqueta (log)
```

## Topología 
| Cola | DLQ | Key direct (`cmd.direct`) | Patrón topic (`cmd.topic`) | Key DLX (`cmd.dead.dlx`) |
|---|---|---|---|---|
| q.cmd.email | q.cmd.email.dlq | email.send | email.* | email.dead |
| q.cmd.warehouse | q.cmd.warehouse.dlq | warehouse.ticket | warehouse.# | warehouse.dead |
| q.cmd.label | q.cmd.label.dlq | label.gen | label.* | label.dead |

Colas principales: TTL 1 h y máximo 1000 mensajes. DLQ: TTL 24 h.

## Manejo de errores
| Caso | Acción |
|---|---|
| Procesado OK | ACK |
| Dato inválido (`InvalidMessageException`) | NACK sin reintentos → DLQ |
| Falla temporal (`TemporaryFailureException`) | 3 intentos (espera 1 s, 2 s); si se agotan → NACK → DLQ |
| Mensaje en una DLQ | `DeadLetterAlertListener` registra `[DLQ-ALERTA]` |

## Variables de entorno
| Variable | Por defecto | Uso |
|---|---|---|
| `RABBITMQ_ADDRESSES` | `localhost:5672` | Nodos del clúster separados por coma |
| `RABBITMQ_USER` / `RABBITMQ_PASSWORD` | `guest` / `guest` | En la nube: usuario `rx-notify` |
| `RABBITMQ_VHOST` | `/` | En la nube: `rutaexpress` |
| `NOTIFY_DLQ_ALERT_ENABLED` | `true` | `false` deja los fallidos visibles en la DLQ |
| `NOTIFY_SMTP_FAIL_FIRST` | `0` | Prueba de resiliencia: los primeros N emails fallan (2 = reintentos que se recuperan, 3 = va a DLQ) |

## Ejecución
- Local: `mvnw.cmd spring-boot:run` (con RabbitMQ levantado).
- Docker: `docker build -t ms-notify .` dentro de `ms-rutaexpress-notify/`.

## Avance
| Paso | Descripción | Estado |
|---|---|---|
| B1–B5 | Conexión, topología, ACK/NACK, DLQ, reintentos | ✅ |
| B7 | Consumidores de bodega y etiqueta | ✅ |
| F1 | Limpieza: sin endpoints de desarrollo, configuración por variables de entorno, Dockerfile y CI | ✅ |
