# E-Commerce Platform

## Overview
This project is a **microservices-based e-commerce platform** designed for handling product listings, order management, and analytics. The system consists of three main components:

1. **Commerce Service (Spring Boot)** - Handles user authentication, product management, orders, and payments.
2. **Analytics Service (Flask/Python)** - Performs machine learning-based analysis on user behavior and sales data.
3. **UI Application (Angular)** - The frontend that provides users with an interface to browse products, place orders, and view analytics.

## System Architecture
- **Backend:** Spring Boot (Java 21), Flask (Python)
- **Frontend:** Angular
- **Database:** PostgreSQL (for commerce service), MongoDB (for analytics service)
- **Message Queue:** Kafka/RabbitMQ for communication between services
- **Storage:** MinIO for storing product images
- **Security:** JWT-based authentication
- **Containerization:** Docker & Docker Compose for managing microservices

## Features
### **Commerce Service (Spring Boot)**
✅ User authentication & role-based access (JWT)  
✅ Product management (CRUD)  
✅ Order & payment processing  
✅ Integration with analytics service for insights  
