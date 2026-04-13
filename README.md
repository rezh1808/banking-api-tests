# NyuboBank - Full Stack Banking Application

A secure banking web application built with **Spring Boot** and **Vanilla JavaScript**. This project demonstrates secure authentication flows, real-time OTP verification, and anti-spam UI patterns.

---

## 🚀 Key Features

### 🛡️ Security & Backend
* **BCrypt Hashing:** Passwords are never stored in plain text. We use `BCryptPasswordEncoder` to ensure one-way secure hashing.
* **Data Privacy:** Implemented `@JsonIgnore` and **DTO patterns** to prevent sensitive user data (like password hashes) from appearing in the browser's Network tab or Page Source.
* **OTP Verification:** Secure email-based One-Time Password (OTP) system for registration and password resets.

### 🖱️ User Experience (UX)
* **Anti-Spam Click Protection:** All primary action buttons (Register, Login, Update Password, Transfer) feature a "Lock & Load" state. 
    * Buttons disable instantly upon click.
    * Text changes to "Processing..." to provide immediate feedback.
    * Prevents duplicate database entries and accidental multiple form submissions.
* **Stateful UI:** The "Forgot Password" flow handles multi-step transitions (Request -> Verify -> Reset) smoothly without losing user context in the browser.

## 🛠️ Technical Stack
* **Backend:** Java 17, Spring Boot, Spring Data JPA, Hibernate.
* **Security:** Spring Security (BCrypt).
* **Database:** H2 / MySQL.
* **Frontend:** HTML5, CSS3, JavaScript (ES6+).
* **Email:** SMTP integration via Mailtrap for OTP delivery.

---
## 📝 How to Run
1. Clone the repository.
2. Update `src/main/resources/application.properties` with your Mailtrap credentials.
3. Run `mvn spring-boot:run`.
4. Navigate to `http://localhost:8080/ui/login`.

## 📩 Mailtrap Setup (Required)
This project requires a Mailtrap account to handle the SMTP traffic for OTP codes.

1.  Sign up at [Mailtrap.io](https://mailtrap.io).
2.  Go to **Email Testing** > **Inboxes**.
3.  Click your Inbox and find the **SMTP Settings** tab.
4.  In the "Integrations" dropdown, select **Spring Boot**.
5.  Open `src/main/resources/application.properties` and update the following:

```properties
spring.mail.host=sandbox.smtp.mailtrap.io
spring.mail.port=2525
spring.mail.username=YOUR_MAILTRAP_USERNAME
spring.mail.password=YOUR_MAILTRAP_PASSWORD
