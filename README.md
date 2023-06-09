# ITSkillsNow
The project is focused on developing an online platform that aims to provide IT courses and job opportunities for IT students or job seekers. This platform will allow companies to create and manage their courses, post job listings, and manage their content. It will also enable job seekers to browse job listings, search for jobs based on specific criteria, and apply for positions. Users will have the ability to create and manage their profiles, including their personal information, educational background, work experience, and job search preferences. In addition, the platform will integrate with a third-party payment gateway to allow for the purchase of paid courses.

## Table of Contents

- [Contributors](#contributors)
- [Installation](#installation)
- [Usage](#usage)
- [Application Architecture](#application-architecture)
- [API Documentation](#api-documentation)


## Contributors
- [Mohammed Aljader](https://www.linkedin.com/in/mohammed-aljader-12a376162/)

## Installation

1. Install [intellij](https://www.jetbrains.com/help/idea/installation-guide.html)
2. Clone the repository to your local machine.

   ```bash
   git clone https://github.com/mohammedaljader/ITSkillsNow_Backend.git
   ```
2. Navigate to the project directory.
   ```
   cd ITSkillsNow_Backend
   ```
3. Build the project using Maven.
   ```
   mvn clean install
   ```
4. Set up a PostgreSQL database.
   - [Install PostgreSQL](https://www.postgresql.org/download/)
   - Create the following databeses `authDB`, `userDB`, `jobDB` and `courseDB`
5. [RabbitMQ image on docker](https://hub.docker.com/_/rabbitmq).
6. [Zipkin image on docker](https://zipkin.io/pages/quickstart).
   
## Usage
### Running Locally
1. Start first `RabbitMQ` and `Zipkin` on Docker.
2. Start then the `Discovery Server`.
3. Start lastly the other microservices.

### Running with Docker Compose
1. Make sure you have Docker and Docker Compose installed.
2. Run the following command to start the containers.
   ```
   docker-compose up -d
   ```
   This command will start all the necessary containers, including PostgresSQL, RabbitMQ, Zipkin, the Discovery Server, and the microservices.

### Running in Minikube
1. install [MiniKube](https://minikube.sigs.k8s.io/docs/start/) and install [Kubectl](https://kubernetes.io/docs/tasks/tools/)
2. Start Minikube.
   ```
   minikube start
   ```
3. Apply the Kubernetes YAML files.
   ```
   kubectl apply -f ./K8S
   ```
   This command will create the necessary Kubernetes resources, including deployments, services, and ingress rules.
4. Wait for the microservices to be up and running. You can check the status using:
   ```
   kubectl get pods
   ```
   
## Application Architecture
- **API Gateway:** The API Gateway is the entry point for external clients to access the ITSkillsNow system. It sits between the clients and the microservices, providing a single entry point for all requests. The API Gateway is responsible for request routing, load balancing, and authentication/authorization. The technology used for the API Gateway is Spring Cloud Gateway.
- **Service Discovery:** Service Discovery is a crucial component in a microservices architecture. It allows microservices to register themselves with the discovery service, and also to discover other microservices that they need to communicate with. In this diagram, I’m using Netflix Eureka as the Service Discovery solution.
- **auth Service:** This microservice is responsible for authenticating and authorising users of the ITSkillsNow system. It communicates with the User Service to retrieve user information and validate credentials. The technology used for this microservice is Spring Boot.
- **User Service:** This microservice is responsible for managing user information, including user profiles, roles, and permissions. The technology used for this microservice is Spring Boot.
- **Course Service:** This microservice is responsible for managing courses, including adding, updating, and deleting courses. The technology used for this microservice is Spring Boot.
- **Job Service:** This microservice is responsible for managing job postings, including adding, updating, and deleting job postings. The technology used for this microservice is Spring Boot.
- **Email Service:** This microservice is responsible for sending emails to users. It has a separate [repository](https://github.com/mohammedaljader/ITSkillsNow_EmailService)
- **Payment Service:** This microservice processes payments for courses using an external payment provider. It updates course enrollment status and confirms payment with the client application. **_(Not Implemented Yet)_**
![image](https://github.com/mohammedaljader/ITSkillsNow_Backend/assets/78910660/2ed1ecfc-f29f-451c-9445-21b9bca2d98e)

## API Documentation

### Create User
- **URL:** `/auth/register`
- **Method:** POST
- **Request Body:**
  ```
    {
        "fullName":"test",
        "username": "test",    
        "email":"test@gmail.com",
        "password":"test"
    }
  ```
- **Response:** Status Code: 201 (Created)

### Login
- **URL:** `/auth/login`
- **Method:** POST
- **Request Body:**
  ```
    {
        "username": "test",
        "password": "test"
    }
  ```
- **Response:** Status Code: 200  (OK) + Tokens

### Refresh Token
- **URL:** `/refresh/{refreshToken}`
- **Method:** POST
- **Response:** New Tokens

### Delete User
- **URL:** `/auth/delete`
- **Method:** DELETE
- **Request Body:**
  ```
    {
        "username": "test",
        "password": "test"
    }
  ```
- **Response:** Status Code: 200  (OK)

### Get User
- **URL:** `/auth/user/{username}`
- **Method:** GET
- **Response:** USER DTO

### Get All Users
- **URL:** `/auth/users`
- **Method:** GET
- **Response:** List USER DTO
