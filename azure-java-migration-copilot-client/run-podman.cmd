set CODE_DIR=C:\IdeaProjects\rabbitmq-servicebus
set FORCE=--force
podman run -v "%TMP%:/tmp" -v "%CODE_DIR%:/code" --env-file env.list showpune/azure-migration-copilot:latest  "--codemigration=Migrate MQ to Service Bus" "%FORCE%"