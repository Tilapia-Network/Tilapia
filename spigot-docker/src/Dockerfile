FROM adoptopenjdk/openjdk8
ARG SPIGOT_JAR
RUN mkdir /opt/minecraft

COPY . /opt/minecraft/
RUN mv /opt/minecraft/$SPIGOT_JAR /opt/minecraft/server.jar
RUN rm /opt/minecraft/Dockerfile
RUN sh -c "echo \"eula=true\" > /opt/minecraft/eula.txt"

WORKDIR /opt/minecraft

HEALTHCHECK CMD bash -c "ls /opt/minecraft/SERVER_STARTED"
EXPOSE 25565
CMD [ "sh", "./start.sh" ]
