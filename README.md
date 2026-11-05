# Retail Inventory Management System

A JavaFX desktop application for small and medium retail stores to manage products, sales and suppliers, with role-based access for managers and employees. Data is stored in MySQL and accessed over JDBC.

![Java](https://img.shields.io/badge/Java-25-orange)
![JavaFX](https://img.shields.io/badge/JavaFX-25.0.1-blue)
![MySQL](https://img.shields.io/badge/MySQL-8.x-4479A1)
![Connector/J](https://img.shields.io/badge/Connector%2FJ-9.5.0-lightgrey)

## Table of Contents

- [Features](#features)
- [Tech Stack](#tech-stack)
- [Role Permissions](#role-permissions)
- [Project Structure](#project-structure)
- [Getting Started](#getting-started)
- [Database Schema](#database-schema)
- [Security](#security)
- [Known Limitations and Roadmap](#known-limitations-and-roadmap)
- [Contributing](#contributing)
- [License](#license)

## Features

- **Authentication**: login with salted SHA-256 password hashing, plus change-password.
- **Role-based access**: `manager` and `employee` roles control which screens are shown.
- **Product management**: add, update, delete and list products (name, quantity, price).
- **Sales tracking**: record a sale; the total is calculated from the product price and stock is decremented automatically.
- **Supplier management**: add, edit, delete and list suppliers (name, email, phone).
- **Account management**: managers can create staff accounts.

## Tech Stack

| Layer | Technology |
|---|---|
| Language | Java 25 (JDK 25) |
| UI | JavaFX 25.0.1 (programmatic UI, no FXML) |
| Database | MySQL |
| Data access | JDBC with `PreparedStatement`, MySQL Connector/J 9.5.0 |
| Security | `java.security.MessageDigest` (SHA-256) and `SecureRandom` for salts |
| IDE / build | IntelliJ IDEA project (no Maven or Gradle) |

## Role Permissions

| Feature | Employee | Manager |
|---|:---:|:---:|
| Manage products | ✅ | ✅ |
| Manage sales | ✅ | ✅ |
| Change own password | ✅ | ✅ |
| Manage suppliers | ❌ | ✅ |
| Create user accounts | ❌ | ✅ |

## Project Structure

```
SSD-projectd/
└── src/
    ├── App.java              # JavaFX entry point (launches LoginPage)
    ├── LoginPage.java        # Authentication screen
    ├── SignupPage.java       # Account creation (manager only)
    ├── ChangePasswordPage.java
    ├── WelcomePage.java      # Dashboard; shows buttons by role
    ├── ProductsPage.java     # Product CRUD
    ├── SalesPage.java        # Record and list sales
    ├── SuppliersPage.java    # Supplier CRUD
    ├── DBUtils.java          # JDBC connection helpers and role lookup
    ├── SecurityUtils.java    # Salt generation and password hashing
    └── Models/
        ├── Product.java
        ├── Sale.java
        ├── Supplier.java
        └── UserSession.java  # Current username and role
```

## Getting Started

### Prerequisites

- JDK 25
- [JavaFX SDK 25.0.1](https://gluonhq.com/products/javafx/)
- MySQL Server 8.x
- [MySQL Connector/J 9.5.0](https://dev.mysql.com/downloads/connector/j/)
- IntelliJ IDEA (recommended, since the project ships with IntelliJ config)

### 1. Clone

```bash
git clone https://github.com/<your-username>/retail-inventory-management-system.git
cd retail-inventory-management-system
```

### 2. Create the database

The app connects to a database named `test` by default. Run the schema below, or see [Database Schema](#database-schema) for the details.

```sql
CREATE DATABASE IF NOT EXISTS test;
USE test;

CREATE TABLE users (
    user_id   INT AUTO_INCREMENT PRIMARY KEY,
    firstname VARCHAR(50)  NOT NULL,
    lastname  VARCHAR(50)  NOT NULL,
    username  VARCHAR(50)  NOT NULL UNIQUE,
    password  CHAR(64)     NOT NULL,   -- SHA-256 hex
    salt      CHAR(32)     NOT NULL,   -- 16 random bytes, hex
    role      ENUM('manager','employee') NOT NULL DEFAULT 'employee'
);

CREATE TABLE products (
    product_id INT AUTO_INCREMENT PRIMARY KEY,
    name       VARCHAR(100) NOT NULL,
    quantity   INT NOT NULL DEFAULT 0,
    price      DECIMAL(10,2) NOT NULL
);

CREATE TABLE sales (
    sale_id       INT AUTO_INCREMENT PRIMARY KEY,
    product_id    INT NOT NULL,
    quantity_sold INT NOT NULL,
    total_price   DECIMAL(10,2) NOT NULL,
    FOREIGN KEY (product_id) REFERENCES products(product_id)
);

CREATE TABLE suppliers (
    supplier_id   INT AUTO_INCREMENT PRIMARY KEY,
    supplier_name VARCHAR(100) NOT NULL,
    contact_email VARCHAR(100),
    phone         VARCHAR(30)
);
```

### 3. Configure the connection

Credentials are currently set as constants in [`SSD-projectd/src/DBUtils.java`](SSD-projectd/src/DBUtils.java):

```java
private static final String URL = "jdbc:mysql://localhost:3306/test";
private static final String APP_USERNAME = "root";
private static final String APP_PASSWORD = "";   // set your MySQL password
```

Edit these to match your environment. Do not commit real credentials.

### 4. Set up the libraries in IntelliJ

1. Open the `SSD-projectd` folder as a project.
2. **File → Project Structure → Libraries**, then add:
   - the `lib` folder of the JavaFX SDK (the bundled config expects `javafx-sdk-25.0.1/lib`)
   - `mysql-connector-j-9.5.0.jar`
3. Set the project SDK to JDK 25.

### 5. Run

Add these VM options to the run configuration of `App` (adjust the path):

```
--module-path "/path/to/javafx-sdk-25.0.1/lib" --add-modules javafx.controls
```

Run `App.java`. The login screen appears.

### 6. Create the first manager account

Account creation is manager-only, so the first manager has to be inserted directly in the database. Generate the salt and hash with `SecurityUtils` (`generateSalt()` then `hashPassword(password, salt)`) and insert them:

```sql
INSERT INTO users (firstname, lastname, username, password, salt, role)
VALUES ('Admin', 'User', 'admin', '<sha256_hash>', '<salt>', 'manager');
```

After that, log in as `admin` and create further accounts from the dashboard.

## Database Schema

```
users      (user_id PK, firstname, lastname, username UNIQUE, password, salt, role)
products   (product_id PK, name, quantity, price)
sales      (sale_id PK, product_id FK → products, quantity_sold, total_price)
suppliers  (supplier_id PK, supplier_name, contact_email, phone)
```

## Security

- Passwords are stored as `SHA-256(salt ‖ password)` with a per-user 16-byte random salt from `SecureRandom`.
- All database queries use `PreparedStatement` with bound parameters, which protects against SQL injection.
- Role is looked up from the database at login, and privileged dashboard actions are only shown to managers.

## Known Limitations and Roadmap

These are known gaps in the current version:

- [ ] Move DB credentials out of source into a config file or environment variables.
- [ ] Enforce a password policy (`SecurityUtils` currently only checks for non-empty input).
- [ ] Use a slow password hash (bcrypt, scrypt or Argon2) instead of a single SHA-256 round.
- [ ] Wrap "record sale" in a transaction and reject sales that exceed available stock.
- [ ] Validate numeric input (parsing errors are not handled in the UI).
- [ ] Enforce role checks in the data layer, not only by hiding UI buttons.
- [ ] Add Maven or Gradle for reproducible builds, and a bundled `schema.sql`.
- [ ] Add unit tests.

## Contributing

1. Fork the repository
2. Create a feature branch: `git checkout -b feature/your-feature`
3. Commit your changes: `git commit -m "Add your feature"`
4. Push the branch: `git push origin feature/your-feature`
5. Open a pull request

## License

Developed for academic and learning purposes. Add a `LICENSE` file (for example MIT) if you plan to distribute it.
