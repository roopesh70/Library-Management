# Library Management System

This is a complete backend application for a Library Management System built with Java and Maven.

## Features

- Console-based interface for students and librarians.
- Add, search, borrow, and return books.
- Fine calculation for overdue books.
- User authentication (mock).
- Report generation for most borrowed books and overdue users.

## Technologies Used

- **Language:** Java 21 (configured in pom.xml)
- **Build Tool:** Maven
- **Database:** H2 (for development), PostgreSQL (support included)
- **Testing:** JUnit 5, Mockito
- **Logging:** SLF4J, Logback
- **CI/CD:** GitHub Actions, Docker

## Project Structure

```
.
├── .github/workflows/ci.yml
├── Dockerfile
├── pom.xml
├── src
│   ├── main
│   │   ├── java/com/example/library
│   │   │   ├── cli
│   │   │   ├── dao
│   │   │   ├── db
│   │   │   ├── interfaces
│   │   │   ├── model
│   │   │   └── service
│   │   └── resources
│   │       ├── application.properties
│   │       ├── logback.xml
│   │       └── schema.sql
│   └── test
│       └── java/com/example/library
└── README.md
```

## How to Run

### Prerequisites

- Java 17 or higher
- Apache Maven

### From the Command Line

1.  **Clone the repository:**
    ```sh
    git clone <repository-url>
    cd LIB-Managemnt-System
    ```

2.  **Build the project and run tests:**
    This command compiles the source code, runs all tests, and packages the application into a runnable JAR file.
    ```sh
    mvn clean package
    ```

3.  **Run the application:**
    After a successful build, you can run the application from the command line.
    ```sh
    java -jar target/lms-1.0.0.jar
    ```

    You will be greeted with the login menu in the console. You can log in with the default credentials:
    - **Student:** `student1`
    - **Librarian:** `librarian1`
    (No password is required for this version).

### Using Docker

1.  **Build the Docker image:**
    ```sh
    docker build -t lms-app .
    ```

2.  **Run the Docker container:**
    ```sh
    docker run -it lms-app
    ```

## Configuration

- The main configuration is in `src/main/resources/application.properties`.
- By default, the application uses an H2 in-memory database. You can switch to PostgreSQL by uncommenting the relevant lines.
- The database schema is automatically created if `db.init=true` (see `src/main/resources/schema.sql`).

## Upgrading to Java 21 (Latest LTS)

This project is configured to target Java 21 via the `java.version` property in `pom.xml`.

If your machine doesn't already have Java 21 and Maven installed, here are recommended Windows PowerShell steps.

Using winget (Windows 10/11):

```powershell
winget install --id Eclipse.Adoptium.Temurin.21.JDK -e --source winget
winget install --id Apache.Maven -e --source winget
```

Using Chocolatey:

```powershell
choco install temurinjdk21 -y
choco install maven -y
```

Verify installations:

```powershell
java -version
mvn -v
```

Build the project (skip tests for a quick check):

```powershell
mvn -DskipTests package
```

If you'd like, I can add the Maven Wrapper or a script to download JDK 21 for you. Tell me which you prefer.
