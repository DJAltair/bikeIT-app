# BikeIT Application

BikeIT is a university group project created by [DJAltair](https://github.com/DJAltair) and [RedSkittleFox](https://github.com/RedSkittleFox). The project consists of a RESTful server application developed using the Spring Framework, H2 database engine, and an Android mobile app.

## Table of Contents

1. [Server](#server)
2. [Mobile App](#mobile-app)
3. [Database](#database)

## Server

The server is built using the Spring Framework and utilizes the H2 database engine for data persistence.

### Key Features
- **Authentication:** Uses JWT for bearer authentication to secure API endpoints.
- **SSL Certificates:** Implements self-signed SSL certificates to ensure secure communication.
- **User Roles:** Supports user roles (User, Admin) with specific endpoints accessible based on roles.
- **Notifications:** Implements basic pull notifications, allowing messages to be sent to all users or specific users.

## Mobile App

The Android mobile app integrates with the server and offers various features to enhance the user experience.

### Key Features
- **Google Maps Integration:** Displays maps and draws routes using GPS coordinates.
- **Route Sharing:** Allows users to share their saved routes with others.
- **Friends:** Users can add friends and share posts exclusively with them.
- **Account Management:** Supports user account creation and authentication directly from the app.

## Database

The H2 database engine is configured to provide persistent storage for the application. The database schema is linked with the server to ensure seamless data management and retrieval.
