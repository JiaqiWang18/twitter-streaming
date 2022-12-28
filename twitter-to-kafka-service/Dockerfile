# java dockerfile
FROM openjdk:11
# copy jar file
COPY target/twitter-to-kafka-service-0.0.1-SNAPSHOT.jar /usr/app/
# set working directory
WORKDIR /usr/app
# set entrypoint
ENTRYPOINT ["java", "-jar", "twitter-to-kafka-service-0.0.1-SNAPSHOT.jar"]