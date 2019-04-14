FROM pwittchen/alpine-java8:latest
MAINTAINER leonardo
COPY target/inventory-0.0.1-SNAPSHOT.jar /opt/cin/lib/
ENTRYPOINT ["/usr/bin/java"]
CMD ["-jar", "/opt/cin/lib/inventory-0.0.1-SNAPSHOT.jar"]
VOLUME /var/lib/cin/config-inventory
EXPOSE 8080
