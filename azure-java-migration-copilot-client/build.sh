mvn clean package -DskipTests=true --no-transfer-progress
export tag=$(date +"%Y%m%d%H%M%S")
docker build -t showpune/azure-migration-copilot:$tag . -f Dockerfile
docker push showpune/azure-migration-copilot:$tag

docker tag showpune/azure-migration-copilot:$tag showpune/azure-migration-copilot:latest
docker push showpune/azure-migration-copilot:latest