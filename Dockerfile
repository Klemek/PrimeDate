FROM maven

# Create app directory
WORKDIR /usr/src/app

# Bundle app source
COPY src/main ./src/main
COPY pom.xml ./pom.xml

RUN mvn clean package

WORKDIR /usr/src/app/data

CMD [ "sh", "-c", "echo -e $CONSUMER_KEY > consumer_keys.txt && java -jar ../target/primedate-1.0.jar consumer_keys.txt" ]
