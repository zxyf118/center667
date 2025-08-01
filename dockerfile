FROM alpine/java:22-jdk
ENV APP_PATH=/docker/helloworld_pipeline
WORKDIR $APP_PATH
ADD stock-manager-1.0.0-SNAPSHOT.jar $APP_PATH/stock-manager-1.0.0-SNAPSHOT.jar
EXPOSE 8082
ENTRYPOINT ["java","-jar"]
CMD ["stock-manager-1.0.0-SNAPSHOT.jar","-Dfile.encoding=GBK", "--server.port=8082"]
