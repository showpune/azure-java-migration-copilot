FROM mcr.microsoft.com/openjdk/jdk:17-ubuntu AS builder

ARG APPCAT_HOME="/azure-appcat-cli"
ARG APPCAT_VERSION="6.3.0.8-preview"
ARG MAVEN_HOME="/maven"
ARG WORKSPACE="/workspace"
ARG MAVEN_VERSION="3.9.8"

RUN mkdir -p ${APPCAT_HOME}
RUN apt-get update && apt-get install libarchive-tools wget git -y
RUN wget -O- https://aka.ms/appcat/azure-migrate-appcat-for-java-cli-${APPCAT_VERSION}.zip > appcat.zip
RUN bsdtar -C ${APPCAT_HOME} -xf appcat.zip  --strip-components=1
RUN chmod +x ${APPCAT_HOME}/bin/appcat

ARG GIT_PR_FOLDER="/shell"
COPY azure-java-migration-copilot-client/shell ${GIT_PR_FOLDER}
RUN chmod +x ${GIT_PR_FOLDER}/*.sh
RUN ${GIT_PR_FOLDER}/copypr.sh

RUN mkdir -p ${MAVEN_HOME}
RUN wget -O- https://dlcdn.apache.org/maven/maven-3/${MAVEN_VERSION}/binaries/apache-maven-${MAVEN_VERSION}-bin.tar.gz > maven.tar.gz
RUN tar -C ${MAVEN_HOME} -xf maven.tar.gz --strip-components=1
RUN chmod +x ${MAVEN_HOME}/bin/mvn

ARG module="azure-java-migration-copilot-client"
RUN mkdir ${WORKSPACE}
COPY . ${WORKSPACE}
RUN ${MAVEN_HOME}/bin/mvn -pl ${module} -f ${WORKSPACE}/pom.xml clean install -DskipTests=true


FROM mcr.microsoft.com/openjdk/jdk:17-ubuntu

ENV APPCAT_HOME="/azure-appcat-cli"
RUN mkdir -p ${APPCAT_HOME}

ENV MAVEN_HOME="/maven"
RUN mkdir -p ${MAVEN_HOME}

ENV GIT_PR_FOLDER="/shell"
ENV WORKSPACE="/workspace"
ENV module="azure-java-migration-copilot-client"

ENV PATH=$PATH:$APPCAT_HOME/bin:$MAVEN_HOME/bin

COPY --from=builder ${MAVEN_HOME} ${MAVEN_HOME}
COPY --from=builder ${APPCAT_HOME} ${APPCAT_HOME}
#COPY --from=builder ${GIT_PR_FOLDER}/mergedresult/rules/rules-reviewed ${APPCAT_HOME}/rules/migration-core
COPY --from=builder ${WORKSPACE}/${module}/target/*.jar ${WORKSPACE}

ENTRYPOINT [ "/bin/bash", "-c" ]