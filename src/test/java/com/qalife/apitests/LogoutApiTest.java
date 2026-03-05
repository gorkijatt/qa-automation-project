package com.qalife.apitests;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;

/**
 * API Tests for Logout
 *
 * Endpoint: POST /api/logout
 * What it does: Logs out the user (requires login token)
 * Header: Authorization: Bearer <token>
 */
public class LogoutApiTest {

    private String token;

    @BeforeClass
    public void setup() {
        RestAssured.baseURI = "https://qalife-production.up.railway.app/api";

        // Register and login to get a token
        String email = "logouttest" + System.currentTimeMillis() + "@example.com";
        String password = "Test@123";

        given()
                .contentType(ContentType.JSON)
                .body("{\"name\": \"Test User\", \"email\": \"" + email + "\", \"password\": \"" + password + "\"}")
        .when()
                .post("/register");

        Response loginResponse =
            given()
                    .contentType(ContentType.JSON)
                    .body("{\"email\": \"" + email + "\", \"password\": \"" + password + "\"}")
            .when()
                    .post("/login");

        token = loginResponse.jsonPath().getString("token");
    }

    @Test
    public void testLogoutWithToken() {
        given()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + token)
        .when()
                .post("/logout")
        .then()
                .statusCode(200)
                .body("message", equalTo("Logged out"));
    }

    @Test
    public void testLogoutWithoutToken() {
        given()
                .contentType(ContentType.JSON)
        .when()
                .post("/logout")
        .then()
                .statusCode(401)
                .body("message", equalTo("No token provided"));
    }
}
