# com.harsh.auth.auth-portal

This project uses Quarkus, the Supersonic Subatomic Java Framework.

If you want to learn more about Quarkus, please visit its website: <https://quarkus.io/>.

## Running the application in dev mode

You can run your application in dev mode that enables live coding using:

```shell script
./mvnw quarkus:dev
```

> **_NOTE:_**  Quarkus now ships with a Dev UI, which is available in dev mode only at <http://localhost:8080/q/dev/>.

## Packaging and running the application

The application can be packaged using:

```shell script
./mvnw package
```

It produces the `quarkus-run.jar` file in the `target/quarkus-app/` directory.
Be aware that it’s not an _über-jar_ as the dependencies are copied into the `target/quarkus-app/lib/` directory.

The application is now runnable using `java -jar target/quarkus-app/quarkus-run.jar`.

If you want to build an _über-jar_, execute the following command:

```shell script
./mvnw package -Dquarkus.package.jar.type=uber-jar
```

The application, packaged as an _über-jar_, is now runnable using `java -jar target/*-runner.jar`.

## Creating a native executable

You can create a native executable using:

```shell script
./mvnw package -Dnative
```

Or, if you don't have GraalVM installed, you can run the native executable build in a container using:

```shell script
./mvnw package -Dnative -Dquarkus.native.container-build=true
```

You can then execute your native executable with: `./target/com.harsh.auth.auth-portal-1.0.0-SNAPSHOT-runner`

If you want to learn more about building native executables, please consult <https://quarkus.io/guides/maven-tooling>.

## Related Guides

- Hibernate ORM with Panache ([guide](https://quarkus.io/guides/hibernate-orm-panache)): Simplify your persistence code for Hibernate ORM via the active record or the repository pattern
- Hibernate Validator ([guide](https://quarkus.io/guides/validation)): Validate object properties (field, getter) and method parameters for your beans (REST, CDI, Jakarta Persistence)
- REST ([guide](https://quarkus.io/guides/rest)): A Jakarta REST implementation utilizing build time processing and Vert.x. This extension is not compatible with the quarkus-resteasy extension, or any of the extensions that depend on it.
- JDBC Driver - PostgreSQL ([guide](https://quarkus.io/guides/datasource)): Connect to the PostgreSQL database via JDBC
- Mailer ([guide](https://quarkus.io/guides/mailer)): Send emails

## CODE FLOWS:-

## Authentication APIs
| Method | Endpoint         | Description                                                                                                     |
| ------ | ---------------- | --------------------------------------------------------------------------------------------------------------- |
| POST   | `/signup`        | Registers a new user. Stores password as salted hash using `BCrypt`. Returns verification token.                |
| GET    | `/verify?token=` | Verifies user email using token. Sets `isEmailVerified=true`.                                                   |
| POST   | `/login`         | Authenticates user and creates session via secure cookie. If not verified, returns a message to validate email. |
| GET    | `/logout`        | Logs out the user. Clears session cookie.                                                                       |

## User Data APIs
| Method | Endpoint   | Description                                                                                     |
| ------ | ---------- | ----------------------------------------------------------------------------------------------- |
| GET    | `/profile` | Returns current logged-in user's info from cookie-based session. Requires valid session cookie. |

## Session Management
- Session is maintained via an HTTP-only cookie (userId) with a 30-minute expiration. 
- Cookies are automatically cleared on logout from frontend using.

## Summary of Frontend Features
- Signup form with email and password.
- Password is encrypted and stored securely.
- Email validation flow with token.
- Login page handles both verified and unverified users:
- Verified: Access to portal.
- Not Verified: Shows "validate your email" message.
- Access portal page restricted to verified users.
- Profile page shows current user or "not logged in".
- Full logout handling on both backend (session) and frontend (localStorage).

## Frontend Part:
| Page                | Description                                            | Path                        |
| ------------------- | ------------------------------------------------------ | --------------------------- |
| `index.html`        | Login Page                                             | `/portal/index.html`        |
| `signup.html`       | Signup Page                                            | `/portal/signup.html`       |
| `accessportal.html` | Post-login success page for verified users             | `/portal/accessportal.html` |
| `profile.html`      | Profile Page showing user email or not-logged-in state | `/portal/profile.html`      |


## Deployment on AWS (Free Tier)
- The entire application is deployed on an Ubuntu EC2 instance (AWS Free Tier) using the following stack:

## Live Demo
- URL : http://<your-ec2-ip>:8080   // replace with public IP