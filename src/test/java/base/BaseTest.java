package base;

import config.Config;
import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeAll;

public class BaseTest {

    @BeforeAll
    static void setup() {
        RestAssured.baseURI = "http://localhost:8080"; // The root
        RestAssured.basePath = "/bank";               // The context path (if applicable)
    }
}