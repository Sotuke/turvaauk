# Turvaauk
This simple project was created as a test assignment. It contains multiple intentionally vulnerable and exploitable features.

# Components
- Database: Postgres 14 (used in Docker image)
- Backend: Spring Boot 3.4.5
- Frontend: Simple HTML and javascript, served by the Spring Boot server

## Features
Unauthenticated user can:
- Log in
- Register a new account

Authenticated user can:
- Upload new files
- Download already uploaded files
- Delete files
- Sort the file list table by Name, MIME Type and Uploaded At fields
- Logout

## Setup and running
### With Docker
Requirements
- Docker with Docker Compose support. Tested with Docker Engine version 28.0.4 and Docker Compose version v2.34.0

In project folder, run command:  
`docker-compose up --build -d`

To stop the containers:  
`docker-compose down`

### Manually
Requirements  
- Java JDK 17

Build the project  
`./gradlew clean build`

Run the project (against an existing Postgres database. DB credentials have to be configured in application.properties file)  
`./gradlew bootRun`

Run the project (against a H2 in-memory database)  
`./gradlew -Dspring.profiles.active=dev bootRun`

The application can be accessed at:  
`http://localhost:8000`
