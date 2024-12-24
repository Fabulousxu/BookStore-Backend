FROM openjdk:25-slim-bullseye
COPY out/artifacts/BookStoreMain_jar /usr/src/bookstore
WORKDIR /usr/src/bookstore
CMD ["java", "-jar", "BookStoreMain.jar"]