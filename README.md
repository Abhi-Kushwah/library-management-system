# Automated Library Management System

A web application that replaces the paper register of a college library. Admins (librarians) manage
books and members and handle issue/return, while borrowers can search the catalogue and see what
they have borrowed.

Built by a team of 5 as a final year academic project (Aug 2025 - Feb 2026).

## Tech stack

- Java 17, Spring Boot 3 (Web, Data JPA, Validation)
- MySQL 8
- Thymeleaf + plain CSS for the pages
- BCrypt for password hashing

## What it does

- Login for two roles: ADMIN and MEMBER
- Books: add, edit, delete, search by title / author / ISBN / category
- Members: admin can register new members
- Issue: a book is issued for 14 days, a member can hold at most 3 books, the same book cannot be
  issued twice to one person
- Return: stock goes back up automatically, late fine is Rs. 2 per day
- Inventory updates immediately after every issue/return, so the Books page always shows the
  correct number of available copies to both admin and members
- Dashboard with counts (titles, available copies, issued, overdue, members)

## Database

Three tables, created from `src/main/resources/schema.sql`:

- `users` (id, name, email, password, phone, role)
- `books` (id, isbn, title, author, category, total_copies, available_copies)
- `issues` (id, book_id -> books, user_id -> users, issue_date, due_date, return_date, fine, status)

Book and borrower details are stored once and the `issues` table only points to them with foreign
keys, so nothing is repeated. ISBN and email are unique.

## How to run

1. Install JDK 17 and MySQL, and start MySQL.
2. Open `src/main/resources/application.properties` and put your MySQL username and password.
   The database `lms_db` is created automatically.
3. From the project folder run:

   ```
   mvn spring-boot:run
   ```

4. Open http://localhost:8080

On the first run a few sample books and users are added.

| Role   | Email          | Password |
|--------|----------------|----------|
| Admin  | admin@lms.com  | admin123 |
| Member | rahul@lms.com  | rahul123 |
| Member | priya@lms.com  | priya123 |

## Project structure

```
config/       interceptor for login/role check, password encoder, sample data
controller/   request handling
service/      issue/return rules, book and member logic
repository/   Spring Data JPA queries
model/        User, Book, Issue entities
resources/templates/   html pages
```
