# Despliegue del backend en AWS (CloudFormation)

## Requisitos

- AWS CLI configurado
- Docker (para construir la imagen)
- Cuenta en Amazon ECR para la imagen

## Opción 1: Docker local

```bash
./gradlew bootJar
docker build -t fondos-btg-api .
docker run -p 8080:8080 -e SPRING_DATA_MONGODB_URI=mongodb://host.docker.internal:27017/fondos_btg fondos-btg-api
```

## Opción 2: AWS con CloudFormation

1. **Construir y subir imagen a ECR**

```bash
aws ecr get-login-password --region us-east-1 | docker login --username AWS --password-stdin <account-id>.dkr.ecr.us-east-1.amazonaws.com
docker build -t fondos-btg-api .
docker tag fondos-btg-api:latest <account-id>.dkr.ecr.us-east-1.amazonaws.com/fondos-btg-api:latest
docker push <account-id>.dkr.ecr.us-east-1.amazonaws.com/fondos-btg-api:latest
```

2. **Desplegar stack**

```bash
aws cloudformation create-stack --stack-name fondos-btg-api --template-body file://deployment/cloudformation.yaml --capabilities CAPABILITY_NAMED_IAM
```

3. **Base de datos**: usar Amazon DocumentDB (MongoDB compatible) o MongoDB Atlas y configurar `SPRING_DATA_MONGODB_URI` en la tarea ECS.

## Variables de entorno para la aplicación

| Variable | Descripción |
|----------|-------------|
| `SPRING_DATA_MONGODB_URI` | URI de conexión MongoDB (ej. `mongodb://host:27017/fondos_btg`) |

## API REST

- `GET /api/fondos` – Listar fondos
- `POST /api/fondos/clientes` – Crear cliente (saldo inicial COP 500.000)
- `POST /api/fondos/suscribir` – Suscribirse a un fondo
- `POST /api/fondos/cancelar` – Cancelar suscripción
- `GET /api/fondos/clientes/{clienteId}/transacciones` – Historial de transacciones
