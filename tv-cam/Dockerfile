FROM maven:3.9.6 AS build

WORKDIR /app

ARG CONTAINER_PORT

COPY pom.xml /app

#download all dependency we got from pom.xml
RUN mvn dependency:resolve

COPY . /app

RUN mvn clean


#create jar file and skip test "-X" is to see extra logs
RUN mvn package -DskipTests -X


FROM openjdk:17-alpine
# copy the jar file from the build to a new dir
COPY --from=build /app/target/*.jar app.jar

#expose the container port
EXPOSE ${CONTAINER_PORT}

CMD ["java", "-jar", "app.jar"]