package transaction;

import base.BaseTest;
import io.restassured.http.ContentType;
import model.TransferRequest;
import org.junit.jupiter.api.Test;
import utils.TokenManager;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

public class TransactionTest extends BaseTest {

    @Test
    void createUser_shouldSucceed() {
        given()
                .contentType(ContentType.JSON)
                .body("""
                {
                  "name": "reza",
                  "initialDeposit": 500
                }
                """)
                .when()
                .post("/accounts") // Use the path that actually exists in your Controller
                .then()
                .statusCode(201)   // Success code for creation
                .body("name", equalTo("reza"));
    }
}