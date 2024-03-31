FROM node:16.16.0-alpine3.15

WORKDIR ./flights/client

ARG CLIENT_PORT
ENV CLIENT_PORT $CLIENT_PORT
ARG SERVER_PORT
ENV SERVER_PORT $SERVER_PORT
ARG SERVER_IP
ENV SERVER_IP $SERVER_IP

EXPOSE ${CLIENT_PORT}

COPY ./dist ./dist

RUN npm i -g http-server

ENTRYPOINT [ "http-server", "dist" ]
