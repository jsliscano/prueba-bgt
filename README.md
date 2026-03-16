Prueba Tecnica BTG

Este proyecto implementa una API REST desarrollada con Spring Boot para la gestión de fondos de inversión y clientes. La aplicación permite registrar clientes, consultar fondos disponibles, suscribirse a un fondo, cancelar suscripciones, consultar el historial de transacciones y enviar notificaciones según la preferencia configurada (EMAIL o SMS).

Además, se incluyen endpoints para administrar fondos, consultar y recargar el saldo de los clientes.

Las pruebas de los endpoints se realizan mediante cURL:

1. Listar fondos

curl -s http://localhost:9090/api/fondos

2. Crear cliente (EMAIL)

curl -s -X POST http://localhost:9090/api/fondos/clientes -H "Content-Type: application/json" -d "{\"id\":\"cliente-001\",\"nombre\":\"Juan Perez\",\"preferenciaNotificacion\":\"EMAIL\",\"email\":\"estivenliscano2017@gmail.com\",\"telefono\":\"3001234567\"}"

3. Crear cliente (SMS)

curl -s -X POST http://localhost:9090/api/fondos/clientes -H "Content-Type: application/json" -d "{\"id\":\"cliente-002\",\"nombre\":\"Maria Lopez\",\"preferenciaNotificacion\":\"SMS\",\"email\":\"maria@ejemplo.com\",\"telefono\":\"3216350695\"}"

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


