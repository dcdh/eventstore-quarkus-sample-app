#!/bin/bash
# setup vault account
curl --header "X-Vault-Token: myroot" \
  --request POST \
  --data '{"type": "userpass"}' \
  http://127.0.0.1:8200/v1/sys/auth/userpass;
curl --header "X-Vault-Token: myroot" \
  --request POST \
  --data '{"password": "sinclair", "policies": "vault-quickstart-policy"}' \
  http://127.0.0.1:8200/v1/auth/userpass/users/bob;
curl --header "X-Vault-Token: myroot" \
  --request DELETE \
  http://127.0.0.1:8200/v1/sys/mounts/secret;
curl --header "X-Vault-Token: myroot" \
  --request POST \
  --data '{ "type": "kv" }' \
  http://127.0.0.1:8200/v1/sys/mounts/secret;
curl --header "X-Vault-Token: myroot" \
  --request PUT \
  --data '{"policy": "path \"secret/encryption\" {capabilities = [\"read\"]}"}' \
  http://127.0.0.1:8200/v1/sys/policy/vault-quickstart-policy;
curl --header "X-Vault-Token: myroot" \
  --request PUT \
  --data '{"policy": "path \"secret/*\" {capabilities = [\"read\", \"create\", \"update\"]}"}' \
  http://127.0.0.1:8200/v1/sys/policy/vault-quickstart-policy;
