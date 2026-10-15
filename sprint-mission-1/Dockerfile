FROM amazoncorretto:17
LABEL authors="kyj"

WORKDIR /app

ENV PROJECT_NAME=discodeit \
    PROJECT_VERSION=1.2-M8 \
    JVM_OPTS=""

COPY . .
RUN ./gradlew clean build -x test

EXPOSE 80

ENTRYPOINT ["sh", "-c", "java ${JVM_OPTS} -jar build/libs/${PROJECT_NAME}-${PROJECT_VERSION}.jar"]