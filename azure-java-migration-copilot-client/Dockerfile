FROM showpune/migration-toolset:latest

ARG WORKSPACE="/workspace"

RUN mkdir ${WORKSPACE}
COPY ./target/*.jar ${WORKSPACE}/app.jar

ENV COPILOT_APPCAT_HOME=${APPCAT_HOME}
ENV COPILOT_MAVEN_HOME=${MAVEN_HOME}

ENTRYPOINT [ "java", "-jar", "/workspace/app.jar","--path=/code"]