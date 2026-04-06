package utils;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;

public class TokenManager {

    private static String token;

    public static String getToken() {
        if (token == null) {
            token = RestAssured.given()
                    .contentType(ContentType.JSON)
                    .body("""
                        {
                          \"username\": \"user1\",
                          \"password\": \"password123\"
                        }
                    """)
                    .post("/auth/login")
                    .then()
                    .extract()
                    .path("token");
        }
        return token;
    }
}