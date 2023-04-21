FROM adoptopenjdk/openjdk11
ARG PROXY_JAR
RUN mkdir /opt/proxy

COPY . /opt/proxy/
RUN rm /opt/proxy/Dockerfile

WORKDIR /opt/proxy

HEALTHCHECK CMD bash -c "echo > /dev/tcp/127.0.0.1/25565"
EXPOSE 25565
CMD [ "sh", "./start.sh" ]
