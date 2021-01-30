FROM alpine:3.11
RUN apk add openjdk11

ENV PAYARA_VERSION=5.2020.7
ENV PAYARA_URL=https://s3-eu-west-1.amazonaws.com/payara.fish/Payara+Downloads/${PAYARA_VERSION}/payara-${PAYARA_VERSION}.zip
RUN mkdir /opt/payara
WORKDIR /opt/payara

RUN wget ${PAYARA_URL} && \
    unzip payara-${PAYARA_VERSION}.zip

RUN cd /opt/payara/payara5/bin &&  ./asadmin start-domain
