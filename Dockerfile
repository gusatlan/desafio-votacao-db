FROM ubuntu:latest
ARG DEBIAN_FRONTEND=noninteractive
ENV TZ=America/Sao_Paulo
RUN apt update && apt -y install openjdk-17-jdk tzdata;apt autoclean;apt -y autoremove;apt clean
EXPOSE 8080
WORKDIR /tmp
ADD /build/libs/*.jar /tmp/app.jar
ENTRYPOINT ["java", "-Djava.awt.headless=true", "-XX:-UseGCOverheadLimit", "-Xms512M", "-Xmx6G", "-jar", "app.jar"]
