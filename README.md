# Tennis Court Reservation System

System to create and manage reservations of tennis courts.

Functionality is described by the following Use Case Diagram.

![Use Case Diagram](https://drive.google.com/uc?export=view&id=1y7SAjTamDN5u9RGpYHToWVSvL4-U018L)

You can also refer to the following ERD Diagram.

![Entity Relationship Diagram](https://drive.google.com/uc?export=view&id=1c_cR1_JaBt25sr5_tItMfEhxBDkt8g92)

# Tech stack

- The backend is written in Java with Spring Boot and is connected to a PostgreSQL database
- The frontend is written in HTML, CSS and JavaScript

# Deployment

## Pre-requisites:
- Java 17+
- PostgreSQL database

## Installation:
Edit src/main/resources/application.yml and configure your own database and email client to send verification emails

Edit src/main/java/fi/muni/courtreservation/startup/CommandLineAppStartupRunner.java and set the adminEmail field to the email address which you will use to log in as an administrator.

After running the app you will receive an email with the password

Edit src/main/java/fi/muni/courtreservation/registration/RegistrationService.java and set the host field to your hosting domain

Upload the .jar file to your favorite hosting provider
