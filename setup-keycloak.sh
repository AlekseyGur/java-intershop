#!/bin/bash

# Параметры подключения
KEYCLOAK_URL="http://localhost:8082"
ADMIN_USER="admin"
ADMIN_PASSWORD="admin"
CLIENT_ID="intershop"

# Получаем токен администратора
ADMIN_TOKEN=$(curl -s -X POST \
  "${KEYCLOAK_URL}/realms/master/protocol/openid-connect/token" \
  -H "Content-Type: application/x-www-form-urlencoded" \
  -d "username=${ADMIN_USER}" \
  -d "password=${ADMIN_PASSWORD}" \
  -d "grant_type=password" \
  -d "client_id=admin-cli" \
  | jq -r '.access_token')

# JSON-тело запроса
CLIENT_JSON='{
  "protocol": "openid-connect",
  "clientId": "'"${CLIENT_ID}"'",
  "publicClient": false,
  "serviceAccountsEnabled": true,
  "directAccessGrantsEnabled": true,
  "standardFlowEnabled": true,
  "redirectUris": ["http://localhost:8082/*"],
  "webOrigins": ["http://localhost:8082"],
  "attributes": {
    "oauth2.device.authorization.grant.enabled": "false"
  }
}'

# Создаём клиента
curl -s -X POST \
  "${KEYCLOAK_URL}/admin/realms/master/clients" \
  -H "Authorization: Bearer ${ADMIN_TOKEN}" \
  -H "Content-Type: application/json" \
  -d "${CLIENT_JSON}"

# Получаем ID созданного клиента
CLIENT_UUID=$(curl -s -X GET \
  "${KEYCLOAK_URL}/admin/realms/master/clients?clientId=${CLIENT_ID}" \
  -H "Authorization: Bearer ${ADMIN_TOKEN}" \
  | jq -r '.[0].id')

# Получаем Client Secret
CLIENT_SECRET=$(curl -s -X GET \
  "${KEYCLOAK_URL}/admin/realms/master/clients/${CLIENT_UUID}/client-secret" \
  -H "Authorization: Bearer ${ADMIN_TOKEN}" \
  | jq -r '.value')

# Выводим результат
echo ""
echo "Client Secret:  ${CLIENT_SECRET}"