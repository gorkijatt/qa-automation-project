package com.qalife.apitests;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;

/**
 * API Tests for Contact Form
 *
 * Endpoint: POST /api/contact
 * What it does: Submits a contact message (requires login token)
 * Request body: { name, email, message }
 * Header: Authorization: Bearer <token>
 * New entry
 */
public class ContactApiTest {

    private String token;

    @BeforeClass
    public void setup() {
        RestAssured.baseURI = "https://qalife-production.up.railway.app/api";

        // Step 1: Register a user
        String email = "contacttest" + System.currentTimeMillis() + "@example.com";
        String password = "Test@123";

        given()
                .contentType(ContentType.JSON)
                .body("{\"name\": \"Test User\", \"email\": \"" + email + "\", \"password\": \"" + password + "\"}")
        .when()
                .post("/register");

        // Step 2: Login to get a token
        Response loginResponse =
            given()
                    .contentType(ContentType.JSON)
                    .body("{\"email\": \"" + email + "\", \"password\": \"" + password + "\"}")
            .when()
                    .post("/login");

        // Step 3: Save the token — we'll use it in our tests
        token = loginResponse.jsonPath().getString("token");
    }

    @Test
    public void testSubmitContactWithToken() {
        given()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + token)             // Pass the login token
                .body("{\"name\": \"John\", \"email\": \"john@example.com\", \"message\": \"Hello!\"}")
        .when()
                .post("/contact")
        .then()
                .statusCode(200)
                .body("message", equalTo("Message received"));
    }

    @Test
    public void testSubmitContactWithoutToken() {
        // Try to submit WITHOUT a token — should be rejected
        given()
                .contentType(ContentType.JSON)
                .body("{\"name\": \"John\", \"email\": \"john@example.com\", \"message\": \"Hello!\"}")
        .when()
                .post("/contact")
        .then()
                .statusCode(401)                                        // 401 = Unauthorized
                .body("message", equalTo("No token provided"));
    }
}
