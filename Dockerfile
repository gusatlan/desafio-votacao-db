FROM ubuntu:latest
RUN apt update && apt -y install openjdk-17-jdk;apt autoclean;apt -y autoremove;apt clean
EXPOSE 8080
WORKDIR /tmp
ADD /build/libs/*.jar /tmp/app.jar
ENTRYPOINT ["java", "-Djava.awt.headless=true", "-XX:-UseGCOverheadLimit", "-Xms512M", "-Xmx6G", "-jar", "app.jar"]
