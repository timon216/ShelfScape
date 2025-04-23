![](https://skillicons.dev/icons?i=java,spring,hibernate,mysql,js,html,css)

# ShelfScape

Web-based virtual library system made from scratch with Spring Boot, Thymeleaf, MySQL, Hibernate, styled with vanilla CSS.
It is my first full-stack project, with no previous knowledge of Spring Boot. I decided to not use any tutorials - I used mostly the documentation and tried to use help of AI, but it became mostly code debugging then.

The most valuable thing I learnt during that project: patience.

---

## Table of contents

1. [Features](#features) 
2. [Process](#process) 
3. [Design](#design) 
4. [Installation & Setup](#installation-setup)
5. [Future Improvements](#future-improvements)

---
<a name="features"></a>
## 1. Features
- **Security**: Uses Spring Security with password hashing via BCrypt for secure login.
- **Role Management**: Users are assigned the 'USER' role by default. Admins can manage roles and users. 
- **Book Management**: 
	- Users can reserve up to 6 books at the time. 
	- User then goes to the library and admin can change the status from "reserved" to "borrowed", when user takes the book. Reservations last 7 days, after that they expire. Books can be borrowed for 30 days (it can be extended by the admin).
	- User returns the book and admin changes the status to "returned".
- **Admin Dashboard**: Admins can manage users, books, and loans. 
- **Responsive Design**: Built with Thymeleaf and responsive HTML with CSS to ensure accessibility on all devices. 

---
<a name="process"></a>
## 2. Process
- **Idea**: the idea came from the course "Java JUMP" - fast paced course consisting of basics of Java Programming. Our mentor gave us a project idea at the end - car rental portal. I decided to change the topic and try something more complicated.
- **Implementation**:
	- it was my first contact with this big project, and with Spring Boot. I wanted to challenge my ability to read the documentation, as well as using chatGPT to help me understand the code and simplify the process of getting to know Spring Boot.
	- I started from entities, and made controllers and services one by one for all needed functionalities.
	- I made the design in Penpot (it's better alternative to Figma) and then code it page by page in vanilla CSS (that's my favourite part!)

---
<a name="design"></a>
## 3. Design


---
<a name="installation-setup"></a>
## 4. Installation & Setup
##### Prerequisites: make sure you have the following installed: 
- **Java 17+** 
- **MySQL 8.0.33** 
- **Maven** 

To run **ShelfScape** locally, follow these steps: 

1. **Clone the repository**: 
```bash
git clone https://github.com/timon216/ShelfScape
```
1. **Navigate to the project folder**:
```bash
cd shelfscape
```
1. **Configure MySQL Database**:
	- Create a new MySQL database (e.g., `shelfscape_db`).
	- Update the `application.properties` file with your database connection details:
```
spring.datasource.url=jdbc:mysql://localhost:3306/shelfscape_db 
spring.datasource.username=yourusername 
spring.datasource.password=yourpassword
```
1. **Build the project**: Run the following command to install dependencies and build the project:
```bash
mvn clean install
```
1. **Run the application**: Start the Spring Boot application:
```bash
mvn spring-boot:run
```
1. **Access the application**: Open your browser and go to http://localhost:8080

#### Usage
- **Register** as a new user by providing your email, first name, last name, and password.
- **Login** with your email and password.
- **Browse Books**: View available books in the library and reserve them (max. 6 at the same time).
- **Admin Dashboard**: Admins can manage users, books, and loans.

---
<a name="future-improvements"></a>
## 5.Future Improvements 
 - [ ] Implement automated tests (unit & integration) 
 - [ ] Add notifications for due dates 
 - [ ] Add wishlist fot books that are not available
 - [ ] Improve UI
