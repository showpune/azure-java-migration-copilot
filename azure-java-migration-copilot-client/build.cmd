call mvn clean package "-DskipTests=true"
docker build -t showpune/azure-migration-copilot:latest . -f Dockerfile
docker push showpune/azure-migration-copilot:latest