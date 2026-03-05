package com.qalife.apitests;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;

/**
 * API Tests for User Login
 *
 * Endpoint: POST /api/login
 * What it does: Logs in and returns a token
 * Request body: { email, password }
 */
public class LoginApiTest {

    private String testEmail;
    private String testPassword = "Test@123";

    @BeforeClass
    public void setup() {
        RestAssured.baseURI = "https://qalife-production.up.railway.app/api";

        // Register a user first so we have someone to login with
        testEmail = "logintest" + System.currentTimeMillis() + "@example.com";

        given()
                .contentType(ContentType.JSON)
                .body("{\"name\": \"Test User\", \"email\": \"" + testEmail + "\", \"password\": \"" + testPassword + "\"}")
        .when()
                .post("/register");
    }

    @Test
    public void testSuccessfulLogin() {
        given()
                .contentType(ContentType.JSON)
                .body("{\"email\": \"" + testEmail + "\", \"password\": \"" + testPassword + "\"}")
        .when()
                .post("/login")
        .then()
                .statusCode(200)                                        // 200 = OK
                .body("message", equalTo("Login successful"))
                .body("token", notNullValue());                         // Should return a JWT token
    }

    @Test
    public void testLoginWithWrongPassword() {
        given()
                .contentType(ContentType.JSON)
                .body("{\"email\": \"" + testEmail + "\", \"password\": \"WrongPass@999\"}")
        .when()
                .post("/login")
        .then()
                .statusCode(400)
                .body("message", equalTo("Invalid email or password"));
    }
}
