# Docker Compose (SpringBoot Application + MySQL)

In simple terms Docker compose is used to combine multiple containers into one single container where each of the child container can communicate with each other via a network. And whole system can run as a single unit.

## Steps to create Docker Compose

### 1. Modify the application.properties file in your SpringBoot application

Modify application.properties file of spring boot application. Previously the properties file was configured with mysql server that is installed in our local machine.

```
spring.application.name=spring-app-with-mysql


spring.datasource.url=jdbc:mysql://localhost:3306/docker
spring.datasource.username=root
spring.datasource.password=SarkarSQL@2001
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver

spring.jpa.show-sql = true

spring.jpa.hibernate.ddl-auto = update
spring.jpa.properties.hibernate.dialect = org.hibernate.dialect.MySQLDialect
```
But now as the mysql server will change, we need to reconfigure it based on the docker version of mysql that will will install later.

```
spring.application.name=spring-app-with-mysql


spring.datasource.url=${SPRING_DATASOURCE_URL:jdbc:mysql://localhost:3306/docker}
spring.datasource.username=${SPRING_DATASOURCE_USERNAME:root}
spring.datasource.password=${SPRING_DATASOURCE_PASSWORD:SarkarSQL@2001}
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver

spring.jpa.show-sql = true

spring.jpa.hibernate.ddl-auto = update
spring.jpa.properties.hibernate.dialect = org.hibernate.dialect.MySQLDialect
```

These are called environment variables with if-else condition. 

For example ```${SPRING_DATASOURCE_USERNAME:root}``` states that if env variable ```SPRING_DATASOURCE_USERNAME``` is present then go with it otherwise set ```root``` as username.

We will set the environment variables in the docker compose file. In this way the spring application can run both locally and globally (docker)

### 2. Create Docker image of your SpringBoot application

Our next step is to create the docker images of our spring boot application which is very straight forward. 

First step is to clean and install maven to create the jar file that will present in the target folder. 

After that create a ```Dockerfile``` with no extension in your root directory

```
# Use an official OpenJDK runtime as a parent image
FROM openjdk:17-jdk-alpine

# Set the working directory in the container
WORKDIR /app

# Copy the executable jar file from your local machine to the container
COPY target/name_of_the_jar_file.jar app.jar

# Expose the port the application will run on
EXPOSE 8080

# Run the jar file
ENTRYPOINT ["java","-jar","app.jar"]
```

By running the below docker command from your root directory will generate the docker image of your spring boot application (make sure docker desktop is running)

```
docker build -t spring-image-name:version .
```

### 3. Pull Mysql docker image from docker hub in your local machine

If we try to run the docker image with this command it will give an error

```
docker run -p 8080:8080 --name container-name spring-image-name:version
```

Because database is not configured yet. In this phase the docker can neither access the local mysql server nor mysql docker.

That's why our next step is to pull the mysql docker into our local machine. Just execute this command 

```
docker pull mysql
```

This will install the docker image of mysql into your docker desktop.

### 4. Creating Docker compose file

This is the main phase, at first create a ```compose.yml``` file in your root directory.

- The compose.yml file will create 2 services. One for our spring boot (app) and another is our mysql database (mysql).

```
version: '3.8'

services:
  mysql:
    

  app:

```

- Next task is to create container for both images and making sure that app will create after mysql

```
version: '3.8'

services:
  mysql:
    image: mysql:latest
    container_name: mysql-container-name

  app:
    image: spring-image-name
    container_name: spring-container-name
    depends_on:
      - mysql

```

- After that create environment variables. These environment variables will configure our app with mysql docker. ```MYSQL_USER``` should be same as ```SPRING_DATASOURCE_USERNAME``` and ```MYSQL_PASSWORD``` should be same as ```SPRING_DATASOURCE_PASSWORD```
- These env variables will further use in the ```application.properties``` file
- Also specify the port number where app and mysql will run. Remember even if mysql runs on port ```3307```, the port in the url should always ```3306```. 

```
version: '3.8'

services:
  mysql:
    image: mysql:latest
    container_name: mysql-container-name
    environment:
      MYSQL_ROOT_PASSWORD: root
      MYSQL_DATABASE: database-name
      MYSQL_USER: user
      MYSQL_PASSWORD: root
    ports:
      - "3307:3306"

  app:
    image: spring-image-name
    container_name: spring-container-name
    depends_on:
      - mysql
    environment:
      SPRING_DATASOURCE_URL: jdbc:mysql://mysql-container-name:3306/database-name?autoReconnect=true&allowPublicKeyRetrieval=true&useSSL=false
      SPRING_DATASOURCE_USERNAME: user
      SPRING_DATASOURCE_PASSWORD: root
    ports:
      - "8080:8080"

```

-  And lastly bind these 2 survices into a single network so that they can communicate with each other. Network name can be random but should be same for both survices

```
version: '3.8'

services:
  mysql:
    image: mysql:latest
    container_name: mysql-container-name
    environment:
      MYSQL_ROOT_PASSWORD: root
      MYSQL_DATABASE: database-name
      MYSQL_USER: user
      MYSQL_PASSWORD: root
    ports:
      - "3307:3306"
    networks:
      - network-name

  app:
    image: spring-image-name
    container_name: spring-container-name
    depends_on:
      - mysql
    environment:
      SPRING_DATASOURCE_URL: jdbc:mysql://mysql-container-name:3306/database-name?autoReconnect=true&allowPublicKeyRetrieval=true&useSSL=false
      SPRING_DATASOURCE_USERNAME: user
      SPRING_DATASOURCE_PASSWORD: root
    ports:
      - "8080:8080"
    networks:
      - network-name

networks:
  network-name:
```
- Finally compose.yml file is prepared

### 5. Running Docker compose

Simply run this command to run the docker compose file.

```
docker compose up
```

Check the containers in the docker desktop to varify if everything is working properly.
