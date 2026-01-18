package com.example.restassured;

import io.restassured.http.ContentType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.concurrent.ThreadLocalRandom;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

public class PetApiTest extends BaseTest {

    private static final String PET_CREATE_JSON_PATH = "src/test/resources/testdata/pet_create.json";



    @Test
    @DisplayName("Test POST /pet and GET /pet/{id} - Create a new pet and then verify its details")
    void testCreatePetAndVerify() throws IOException {
        int uniquePetId = ThreadLocalRandom.current().nextInt(1_000_000, Integer.MAX_VALUE / 1000);
        String petName = "test-pet-" + uniquePetId;
        String petStatus = "pending";

        // Read JSON template; ensure template uses numeric placeholder for id (no quotes around {{id}})
        String requestBody = new String(Files.readAllBytes(Paths.get(PET_CREATE_JSON_PATH)));

        // Replace placeholders with dynamic data
        requestBody = requestBody.replace("{{id}}", String.valueOf(uniquePetId));
        requestBody = requestBody.replace("{{name}}", petName);
        requestBody = requestBody.replace("{{status}}", petStatus);

        // 1. POST /pet to create a new pet
        given()
            .log().all()
            .contentType(ContentType.JSON)
            .body(requestBody)
        .when()
            .post("/pet")
        .then()
            .log().all()
            .statusCode(200)
            .body("id", equalTo(uniquePetId))
            .body("name", equalTo(petName))
            .body("status", equalTo(petStatus));

        // 2. GET /pet/{id} to verify the created pet
        given()
            .log().all()
        .when()
            .get("/pet/{id}", uniquePetId)
        .then()
            .log().all()
            .statusCode(200)
            .body("id", equalTo(uniquePetId))
            .body("name", equalTo(petName))
            .body("status", equalTo(petStatus));
    }

    @Test
    @DisplayName("Test GET /pet/{id} - Verify 404 for a non-existent pet")
    void testInvalidPetReturns404() {
        int nonExistentPetId = ThreadLocalRandom.current().nextInt(1_000_000, 2_000_000_000);

        // Attempt to delete first to ensure the ID does not exist (idempotent)
        given()
            .log().all()
        .when()
            .delete("/pet/{id}", nonExistentPetId)
        .then()
            .log().all()
            // delete may return 200 (if existed) or 404 (if not). Accept either for idempotence.
            .statusCode(org.hamcrest.Matchers.anyOf(equalTo(200), equalTo(404)));

        // Now GET should return 404
        given()
            .log().all()
        .when()
            .get("/pet/{id}", nonExistentPetId)
        .then()
            .log().all()
            .statusCode(404);
    }
}
