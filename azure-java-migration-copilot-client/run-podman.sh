export CODE_DIR=/mnt/c/IdeaProjects/rabbitmq-servicebus
export FORCE=
podman run -v "/tmp:/tmp" -v "$CODE_DIR:/code" --env-file env.list showpune/azure-migration-copilot:latest  "--codemigration=Migrate MQ to Service Bus" "$FORCE"