FROM openjdk:17-jdk-slim

WORKDIR /app

ENV JAVA_TOOL_OPTIONS="-Dfile.encoding=UTF-8 -Dconsole.encoding=UTF-8"
ENV LC_ALL=C.UTF-8
ENV LANG=C.UTF-8

COPY mvnw pom.xml ./
COPY .mvn .mvn

RUN chmod +x ./mvnw

RUN ./mvnw dependency:go-offline -B

COPY src ./src

RUN ./mvnw clean package -DskipTests

RUN mv target/kafka-demo-*.jar target/app.jar

EXPOSE 8080

CMD ["java", "-jar", "target/app.jar"]