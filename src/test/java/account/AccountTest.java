package account;

import base.BaseTest;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

public class AccountTest extends BaseTest {

    String token = "mock-token";

    // ✅ TC_Account_001 - Create Account
    @Test
    void createAccount_shouldSucceed() {
        given()
                .contentType(ContentType.JSON)
                .body("{ \"name\": \"Reza\", \"initialDeposit\": 1000 }")
                .when()
                .post("/accounts")
                .then()
                .statusCode(201);
    }

    // ❌ TC_Account_002 - Negative Deposit
    @Test
    void createAccount_negativeDeposit_shouldFail() {
        given()
                .header("Authorization", "Bearer " + token)
                .contentType(ContentType.JSON)
                .body("""
                {
                    "name": "Reza",
                    "initialDeposit": -100
                }
            """)
                .when()
                .post("/accounts")
                .then()
                .statusCode(400);
    }

    // ✅ TC_Account_003 - Get Account Details
    @Test
    void getUsers_shouldReturnList() {
        given()
                .when()
                .get("/users?page=2")
                .then()
                .statusCode(200);
    }
}