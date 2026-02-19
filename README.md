# Crowdfunding API

RESTful API for a crowdfunding platform built with Java 21 and Spring Boot.

The project follows a modular domain structure (campaign, user, donation, payment) 
and implements layered architecture (Controller-Service-Repository) 
with JWT authentication, pagination, caching and global exception handling.

## Tech Stack

- Java 21
- Spring Boot
- Spring Security (JWT)
- Spring Data JPA / Hibernate
- MySQL
- Spring Cache
- Bean Validation
- OpenAPI / Swagger

## Architecture

Each module follows a layered structure:
- controller
- service
- repository
- entity
- dto

  ## Features

- Campaign CRUD with status control (DRAFT, ACTIVE, COMPLETED, CANCELLED)
- JWT authentication and role-based authorization (USER, ADMIN)
- Donation processing with automatic campaign total update
- Simulated payment gateway
- Pagination for listing endpoints
- Global exception handling
- Caching for frequently accessed resources

## Authentication

1. User logs in via POST /auth/login
2. JWT token is generated and returned
3. Token must be sent in Authorization header:
   Bearer <token>
4. Security filter validates token on each request

## Running the Application

1. Clone the repository
2. Configure MySQL database in application.properties
3. Run the project:

./mvnw spring-boot:run

Access Swagger UI:
http://localhost:8080/swagger-ui.html

## Git Workflow

- main → stable version
- develop → development branch
- feat/* → feature branches
- refactor/* → structural improvements

## Future Improvements

- Integration tests
- Redis cache implementation
- Docker containerization
- CI/CD pipeline

