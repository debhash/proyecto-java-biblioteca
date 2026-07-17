FROM maven:3.9-eclipse-temurin-25 AS build
WORKDIR /build

COPY pom.xml .
RUN mvn -B -q dependency:go-offline

COPY src ./src
RUN mvn -B -q clean package

FROM tomcat:10.1-jdk25-temurin
WORKDIR /tmp

COPY --from=build /build/target/digital-library.war /tmp/digital-library.war

RUN mkdir -p /tmp/war-extracted \
    && cd /tmp/war-extracted \
    && jar xf /tmp/digital-library.war \
    && printf 'db.url=jdbc:h2:/data/library-db;AUTO_SERVER=TRUE\ndb.user=sa\ndb.password=\ndb.driver=org.h2.Driver\n' \
        > WEB-INF/classes/database.properties \
    && jar cf /usr/local/tomcat/webapps/digital-library.war . \
    && rm -rf /tmp/war-extracted /tmp/digital-library.war
