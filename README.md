Prueba Tecnica BTG

Este proyecto implementa una API REST desarrollada con Spring Boot para la gestión de fondos de inversión y clientes. La aplicación permite registrar clientes, consultar fondos disponibles, suscribirse a un fondo, cancelar suscripciones, consultar el historial de transacciones y enviar notificaciones según la preferencia configurada (EMAIL o SMS).

Cada cliente se crea con un saldo inicial, el cual se utiliza para realizar suscripciones a los diferentes fondos disponibles. Cuando un cliente se suscribe a un fondo, el monto de la inversión se descuenta de su saldo. En caso de cancelar la suscripción, el valor previamente invertido se devuelve nuevamente al saldo del cliente.

Los fondos se inicializan automáticamente al iniciar la aplicación, permitiendo que el sistema cuente desde el inicio con opciones de inversión disponibles para los clientes.

Además, se incluyen endpoints para administrar fondos, consultar y recargar el saldo de los clientes.

Las pruebas de los endpoints se realizan mediante cURL:

1. Listar fondos

curl -s http://localhost:9090/api/fondos

2. Crear cliente (EMAIL)

curl -s -X POST http://localhost:9090/api/fondos/clientes -H "Content-Type: application/json" -d "{\"nombre\":\"Juan Perez\",\"preferenciaNotificacion\":\"EMAIL\",\"email\":\"estivenliscano2017@gmail.com\",\"telefono\":\"3001234567\"}"

3. Crear cliente (SMS)

curl -s -X POST http://localhost:9090/api/fondos/clientes -H "Content-Type: application/json" -d "{\"nombre\":\"Maria Lopez\",\"preferenciaNotificacion\":\"SMS\",\"email\":\"maria@ejemplo.com\",\"telefono\":\"3216350695\"}"

4. Suscribir a un fondo

curl -s -X POST http://localhost:9090/api/fondos/suscribir -H "Content-Type: application/json" -d "{\"clienteId\":\"cliente-001\",\"fondoId\":1,\"monto\":75000}"


5. Cancelar suscripción

curl -s -X POST http://localhost:9090/api/fondos/cancelar -H "Content-Type: application/json" -d "{\"clienteId\":\"cliente-001\",\"fondoId\":1}"

6. Ver historial de transacciones (POST, clienteId en body)

curl -s -X POST http://localhost:9090/api/fondos/clientes/transacciones -H "Content-Type: application/json" -d "{\"clienteId\":\"cliente-001\"}"


Endpoint adicionales:
7. Crear fondo

curl -s -X POST http://localhost:9090/api/fondos -H "Content-Type: application/json" -d "{\"id\":10,\"nombre\":\"FONDO_NUEVO\",\"montoMinimo\":80000,\"categoria\":\"FPV\"}"

8. Eliminar fondo

curl -s -X DELETE http://localhost:9090/api/fondos/10

9. Ver saldo del cliente

curl -s http://localhost:9090/api/fondos/clientes/cliente-001/saldo

10. Recargar saldo

curl -s -X POST http://localhost:9090/api/fondos/clientes/recargar -H "Content-Type: application/json" -d "{\"clienteId\":\"cliente-001\",\"monto\":100000}"


