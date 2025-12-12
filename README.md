# ***📦Product Review System – Spring Boot (RBAC + JWT + Permissions)***

## *A complete Role-Based Access Control (RBAC) product review system built using:*

Spring Boot 3 / Spring Security 7

JWT Authentication

Role + Permission-based Authorization

MySQL / H2

REST APIs

RBAC (Roles → Permissions → Authorities)

This project demonstrates how to build a production-level secure backend with fine-grained permissions for each API.

# ***🔐 Role-Based Access Control (RBAC)***

*Every request is validated using Spring Security + JWT filter.*

## ***Roles Available***

ADMIN

PRODUCT_OWNER

PRODUCT_REVIEWER

USER

## **Permissions Used**

PRODUCT:CREATE
PRODUCT:UPDATE
PRODUCT:READ_ALL
PRODUCT:DELETE_ALL
REVIEW:CREATE
USER:MANAGE

## **📌 Permission Assignment (via DataInitializer)**

## **Role	 Permissions**
ADMIN	- All Permissions
PRODUCT_OWNER -	Create, Update, Read All
PRODUCT_REVIEWER - Review Create, Read All
USER	- Review Create

## **📦 Product APIs**
### **Method	Endpoint	Permission Required**
POST	/api/v1/products	PRODUCT:CREATE
GET	/api/v1/products/{id}	Authenticated User
GET	/api/v1/products/all	PRODUCT:READ_ALL
PUT	/api/v1/products/{id}	PRODUCT:UPDATE
DELETE	/api/v1/products/{id}	PRODUCT:DELETE_ALL
