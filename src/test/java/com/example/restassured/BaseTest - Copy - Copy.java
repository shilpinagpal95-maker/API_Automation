package com.example.restassured;

import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeAll;

public class BaseTest {

    // Base URI for the PetStore API
    protected static final String BASE_URI = "https://petstore.swagger.io/v2";

    @BeforeAll
    static void setup() {
        // Set the base URI for all REST Assured requests
        RestAssured.baseURI = BASE_URI;
        
        // This setup will apply to all tests inheriting from BaseTest.
        // For demonstration, we'll enable logging directly in the tests
        // using .log().all() to show specific request/response details per test.
        // For global logging across all tests, one could use filters like:
        // RestAssured.filters(new RequestLoggingFilter(), new ResponseLoggingFilter());
        // but for a training session, explicit logging per test is clearer.
    }
}