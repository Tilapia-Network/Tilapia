FROM adoptopenjdk/openjdk11
RUN mkdir /opt/game-central

COPY . /opt/game-central/
RUN rm /opt/game-central/Dockerfile

WORKDIR /opt/game-central

HEALTHCHECK CMD bash -c "echo > /dev/tcp/127.0.0.1/8080"
EXPOSE 8080
CMD [ "sh", "./start.sh" ]
