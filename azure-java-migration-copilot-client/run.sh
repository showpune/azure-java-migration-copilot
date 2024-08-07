export CODE_DIR=/home/user/IdeaProjects/rabbitmq-servicebus
export FORCE=
docker run -v "$TMP:/tmp" -v "$CODE_DIR:/code" --env-file env.list showpune/azure-migration-copilot:latest  "--codemigration=Migrate MQ to Service Bus" "$FORCE"