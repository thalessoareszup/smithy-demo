FROM amazoncorretto:25

COPY ./build/install/incident-hub/lib/incident-hub.jar incident-hub.jar

ENTRYPOINT exec java -jar incident-hub.jar

EXPOSE 8080