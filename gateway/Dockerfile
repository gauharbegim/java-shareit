FROM amazoncorretto:11

FROM eclipse-temurin:11-jre-jammy
VOLUME /tmp
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} app.jar
ENTRYPOINT ["sh", "-c", "java ${JAVA_OPTS} -jar -Dserver.port=8080 /app.jar"]
