package com.qalife.apitests;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;

/**
 * API Tests for User Registration
 *
 * Endpoint: POST /api/register
 * What it does: Creates a new user account
 * Request body: { name, email, password }
 */
public class RegisterApiTest {

    @BeforeClass
    public void setup() {
        // Tell RestAssured the base URL for all API calls
        RestAssured.baseURI = "https://qalife-production.up.railway.app/api";
    }

    @Test
    public void testSuccessfulRegistration() {
        // Use timestamp to make a unique email every time we run the test
        String uniqueEmail = "user" + System.currentTimeMillis() + "@example.com";

        given()
                .contentType(ContentType.JSON)                          // We are sending JSON
                .body("{"
                        + "\"name\": \"John Doe\","
                        + "\"email\": \"" + uniqueEmail + "\","
                        + "\"password\": \"Test@123\""
                        + "}")
        .when()
                .post("/register")                                      // Hit the register endpoint
        .then()
                .statusCode(201)                                        // 201 = Created
                .body("message", equalTo("User registered"));           // Check response message
    }

    @Test
    public void testDuplicateEmailRegistration() {
        // First, register a user
        String email = "duplicate" + System.currentTimeMillis() + "@example.com";

        given()
                .contentType(ContentType.JSON)
                .body("{\"name\": \"John\", \"email\": \"" + email + "\", \"password\": \"Test@123\"}")
        .when()
                .post("/register");

        // Now try to register again with the SAME email — should fail
        given()
                .contentType(ContentType.JSON)
                .body("{\"name\": \"John\", \"email\": \"" + email + "\", \"password\": \"Test@123\"}")
        .when()
                .post("/register")
        .then()
                .statusCode(400)                                        // 400 = Bad Request
                .body("message", equalTo("Email already exists"));
    }
}
