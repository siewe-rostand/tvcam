## Build Stage
#FROM maven:3.6.3-openjdk-17 AS build
#WORKDIR /build
#
## Copy the POM file
#COPY pom.xml .
## Download all required dependencies into one layer
#RUN mvn dependency:go-offline -B
#
## Copy source files
#COPY src ./src
## Build the application
#RUN mvn clean package -DskipTests
#
## Runtime Stage
#FROM openjdk:17-alpine
#ARG PROFILE=dev
#ARG APP_VERSION=1.0.0
#
#WORKDIR /app
#
## Copy the built artifact from the build stage
#COPY --from=build /build/target/*.jar ./app.jar
#
#
#EXPOSE 8085
#
#ENV DB_URL=jdbc:mysql://mysql_db:3306/tv_cam
#ENV ACTIVE_PROFILE=${PROFILE}
#ENV JAR_VERSION=${APP_VERSION}
#
#
## Use shell form of CMD to allow variable substitution
#CMD ["sh", "-c", "java -jar -Dspring.profiles.active=${ACTIVE_PROFILE} -Dspring.datasource.url=${DB_URL} app/app.jar"]


# Build Stage
FROM maven:3.6.3-openjdk-17 AS build
WORKDIR /build

# Copy the POM file
COPY pom.xml .
# Download all required dependencies into one layer
RUN mvn dependency:go-offline -B

# Copy source files
COPY src ./src
# Build the application skip test "-X" is to see extra logs
RUN mvn clean package -DskipTests -X

# Runtime Stage
FROM openjdk:17-alpine
ARG PROFILE=dev
ARG APP_VERSION=1.0.0

WORKDIR /app

# Copy the built artifact from the build stage
COPY --from=build /build/target/tv-cam-*.jar /app/


EXPOSE 8085

ENV DB_URL=jdbc:mysql://mysql_db:3306/tv_cam
ENV ACTIVE_PROFILE=${PROFILE}
ENV JAR_VERSION=${APP_VERSION}


# Use shell form of CMD to allow variable substitution
CMD ["java -jar -Dspring.profiles.active=${ACTIVE_PROFILE} -Dspring.datasource.url=${DB_URL} tv-cam-${JAR_VERSION}.jar"]