package auth;

import base.BaseTest;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

public class LoginTest extends BaseTest {

    // ✅ TC_Auth_001 - Valid Login
    @Test
    void login_shouldWork() {
        given()
                .contentType(ContentType.JSON)
                .body("""
        {
          "email": "eve.holt@reqres.in",
          "password": "cityslicka"
        }
        """)
                .when()
                .post("/login")
                .then()
                .statusCode(200)
                .body("token", notNullValue());
    }
    // ❌ TC_Auth_002 - Missing Password
    @Test
    void loginWithoutPassword_shouldReturnError() {
        given()
                .contentType(ContentType.JSON)
                .body("""
                {
                    "username": "user1"
                }
            """)
                .when()
                .post("/login")
                .then()
                .statusCode(400)
                .body("message", containsString("password required"));
    }

    // ❌ TC_Auth_003 - Invalid Credentials
    @Test
    void loginWithWrongPassword_shouldFail() {
        given()
                .contentType(ContentType.JSON)
                .body("""
                {
                    "username": "user1",
                    "password": "wrongpass"
                }
            """)
                .when()
                .post("/login")
                .then()
                .statusCode(401);
    }
}