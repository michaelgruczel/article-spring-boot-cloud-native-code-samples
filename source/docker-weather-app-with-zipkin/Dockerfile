FROM frolvlad/alpine-oraclejdk8:slim
VOLUME /tmp
ADD build/libs/docker-weather-app-with-zipkin-0.0.1.jar app.jar
RUN sh -c 'touch /app.jar'
EXPOSE 8090
ENTRYPOINT [ "sh", "-c", "java -jar /app.jar" ]